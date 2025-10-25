package com.sisl.gpsvideorecorder.data.remote.utils

import com.sisl.gpsvideorecorder.data.installerFile.saveToFile
import com.sisl.gpsvideorecorder.data.remote.requests.LoginReqData
import com.sisl.gpsvideorecorder.data.remote.requests.RequestLocationData
import com.sisl.gpsvideorecorder.data.remote.requests.RequestVideoLocationData
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.data.remote.response.AppUpdateVersionApiResp
import com.sisl.gpsvideorecorder.data.remote.response.LocationsUploadResp
import com.sisl.gpsvideorecorder.data.remote.response.LoginApiResp
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.models.LoginRequest
import com.sisl.gpsvideorecorder.domain.models.LoginResponse
import com.sisl.gpsvideorecorder.presentation.state.DownloadState
import com.sisl.gpsvideorecorder.utils.Utils
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentDisposition.Companion.File
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.build
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readAvailable
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import kotlinx.io.readByteArray
import okio.FileSystem
import okio.Path
import okio.SYSTEM
import okio.buffer
import okio.use

interface ApiService {
    suspend fun uploadCoordinate(
        videoName: String,
        locationData: List<LocationData>
    ): Flow<ApiResponse<LocationsUploadResp>>

    suspend fun userLogin(
        loginRequest: LoginRequest
    ): Flow<ApiResponse<LoginResponse>>

    suspend fun downloadAppFile(
        platformType: String,
    ): Flow<Float>

    suspend fun checkAppUpdateVersion(): Flow<ApiResponse<AppUpdateVersionApiResp>>

}

class ApiServiceImpl(private val httpClient: HttpClient, private val binaryHttpClient: HttpClient) :
    ApiService {

    override suspend fun uploadCoordinate(
        videoName: String,
        locationData: List<LocationData>
    ): Flow<ApiResponse<LocationsUploadResp>> =
        flow {
            emit(ApiResponse.Loading)
            try {
                val requestLocationData = locationData.map { location ->
                    RequestLocationData(
                        time = location.time,
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed
                    )
                }

                val requestBody = RequestVideoLocationData(
                    videoname = videoName,
                    locations = requestLocationData
                )

                val response = httpClient.post("${Utils.BASE_URL}/location/upload") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }

                if (response.status.isSuccess()) {
                    val responseData = response.body<LocationsUploadResp>()
                    emit(ApiResponse.Success(responseData))
                } else {
                    emit(ApiResponse.Error(response.status.description, response.status.value))
                }

            } catch (e: RedirectResponseException) {
                emit(ApiResponse.Error("Redirect error: ${e.message}", e.response.status.value))
            } catch (e: ClientRequestException) {
                emit(ApiResponse.Error("Client error: ${e.message}", e.response.status.value))
            } catch (e: ServerResponseException) {
                emit(ApiResponse.Error("Server error: ${e.message}", e.response.status.value))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message ?: "Unknown error"))
            }
        }

    override suspend fun userLogin(loginRequest: LoginRequest): Flow<ApiResponse<LoginResponse>> =
        flow {
            try {
                emit(ApiResponse.Loading)
                val request = LoginReqData(
                    userId = loginRequest.userId,
                    password = loginRequest.password
                )
                val response = httpClient.post("${Utils.BASE_URL}/location/login") {
                    contentType(ContentType.Application.Json)
                    setBody(request)
                }

                if (response.status.isSuccess()) {
                    val response = LoginResponse(
                        code = response.status.value,
                        message = "User Found",
                        user = response.body<LoginApiResp>().user ?: ""
                    )
                    emit(ApiResponse.Success(response))
                } else {
                    emit(ApiResponse.Error("User Not Found", response.status.value))
                }

            } catch (e: Exception) {
                emit(ApiResponse.Error("User Not Found", 404))
            }
        }


    override suspend fun downloadAppFile(platformType: String): Flow<Float> = channelFlow {
        try {
            withContext(Dispatchers.IO) {
                val url = when (platformType.lowercase()) {
                    "android" -> Utils.APK_DOWNLOAD_URL
                    "ios" -> Utils.IPA_DOWNLOAD_URL
                    else -> throw IllegalArgumentException("Unsupported platform type")
                }

                println("🔵 Downloading APK from: $url")

                val response = binaryHttpClient.get(url) {
                    headers {
                        append("Accept", "application/vnd.android.package-archive")
                        append("Content-Type", "application/octet-stream")
                    }
                    onDownload { bytesSentTotal, contentLength ->
                        val progress = bytesSentTotal.toFloat() / (contentLength ?: 1L)
                        trySend(progress.coerceIn(0f, 1f))
                    }
                }

                val totalBytes = response.contentLength() ?: -1L
                val fileName = if (platformType.lowercase() == "android") Utils.ANDROID_APK_NAME else Utils.IOS_IPA_NAME
                val filePath = getCacheDirectory() / fileName

                println("🔵 Saving to: $filePath, Total bytes: $totalBytes")

                // Delete existing file if it exists
                if (FileSystem.SYSTEM.exists(filePath)) {
                    FileSystem.SYSTEM.delete(filePath)
                    println("🔵 Deleted existing file")
                }

                var downloadedBytes = 0L
                var lastProgress = 0f

                // Stream download with progress
                FileSystem.SYSTEM.sink(filePath).buffer().use { sink ->
                    val channel = response.body<ByteReadChannel>()
                    val buffer = ByteArray(8192)

                    while (!channel.isClosedForRead) {
                        val bytesRead = channel.readAvailable(buffer)
                        if (bytesRead > 0) {
                            sink.write(buffer, 0, bytesRead)
                            downloadedBytes += bytesRead

                            // Emit progress
                            if (totalBytes > 0) {
                                val progress = downloadedBytes.toFloat() / totalBytes
                                if (progress - lastProgress >= 0.01f) {
                                    trySend(progress.coerceIn(0f, 1f))
                                    lastProgress = progress
                                }
                            }
                        } else if (bytesRead == 0) {
                            delay(10)
                        }
                    }
                    sink.flush()
                }

                // Verify download
                val fileSize = FileSystem.SYSTEM.metadata(filePath).size ?: 0L
                println("🔵 Download completed: $fileSize bytes written")

                if (fileSize > 0) {
                    trySend(1.0f) // Final 100% progress
                }
            }
        } catch (e: Exception) {
            println("❌ Download failed: ${e.message}")
            e.printStackTrace()
            throw e // Re-throw to be caught by repository
        }
    }

    suspend fun ByteReadChannel.copyToOkioPath(filePath: Path) {
        FileSystem.SYSTEM.sink(filePath).buffer().use { sink ->
            val channel = this
            val buffer = ByteArray(8192)

            while (!channel.isClosedForRead) {
                val bytesRead = channel.readAvailable(buffer)
                if (bytesRead > 0) {
                    sink.write(buffer, 0, bytesRead)
                } else {
                    delay(1)
                }
            }
            sink.flush()
        }
    }


    override suspend fun checkAppUpdateVersion(): Flow<ApiResponse<AppUpdateVersionApiResp>> =
        flow {
            try {
                emit(ApiResponse.Loading)
                val response = httpClient.get("${Utils.BASE_URL}/location/version") {
                    contentType(ContentType.Application.Json)
                }

                if (response.status.isSuccess()) {
                    val responseData = response.body<AppUpdateVersionApiResp>()
                    emit(ApiResponse.Success(responseData))
                } else {
                    emit(ApiResponse.Error(response.status.description, response.status.value))
                }

            } catch (e: RedirectResponseException) {
                emit(ApiResponse.Error("Redirect error: ${e.message}", e.response.status.value))
            } catch (e: ClientRequestException) {
                emit(ApiResponse.Error("Client error: ${e.message}", e.response.status.value))
            } catch (e: ServerResponseException) {
                emit(ApiResponse.Error("Server error: ${e.message}", e.response.status.value))
            } catch (e: Exception) {
                emit(ApiResponse.Error(e.message ?: "Unknown error"))
            }
        }


}

expect fun getFileSize(filePath: Path): Long
expect fun isFileExists(filePath: Path): Boolean
expect suspend fun getCacheDirectory(): Path

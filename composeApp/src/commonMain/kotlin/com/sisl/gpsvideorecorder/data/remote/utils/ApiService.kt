package com.sisl.gpsvideorecorder.data.remote.utils

import com.sisl.gpsvideorecorder.data.remote.requests.LoginReqData
import com.sisl.gpsvideorecorder.data.remote.requests.RequestLocationData
import com.sisl.gpsvideorecorder.data.remote.requests.RequestVideoLocationData
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.data.remote.response.LocationsUploadResp
import com.sisl.gpsvideorecorder.data.remote.response.LoginApiResp
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.models.LoginRequest
import com.sisl.gpsvideorecorder.domain.models.LoginResponse
import com.sisl.gpsvideorecorder.utils.Utils
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentLength
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.build
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.io.readByteArray

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


}

class ApiServiceImpl(private val httpClient: HttpClient) : ApiService {
    override suspend fun uploadCoordinate(
        videoName: String,
        locationData: List<LocationData>
    ): Flow<ApiResponse<LocationsUploadResp>> = flow {
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

    override suspend fun downloadAppFile(platformType: String): Flow<Float> = flow {

        val url = when (platformType.lowercase()) {
            "android" -> Utils.APK_DOWNLOAD_URL
            "ios" -> Utils.IPA_DOWNLOAD_URL
            else -> throw IllegalArgumentException("Unsupported platform type")
        }
        val response = httpClient.get(url)
        val channel: ByteReadChannel = response.body()
        val buffer = ByteArray(1024)
        val totalBytes = response.contentLength() ?: -1L
        var readBytes = 0L

        while (!channel.isClosedForRead) {
            val bytesRead = channel.readAvailable(buffer)
            if (bytesRead > 0 && totalBytes > 0) {
                readBytes += bytesRead
                emit(readBytes.toFloat() / totalBytes)
            }
        }
    }


}


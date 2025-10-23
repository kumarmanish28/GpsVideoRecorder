package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity
import com.sisl.gpsvideorecorder.data.local.entities.toDomain
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.data.remote.response.LocationsUploadResp
import com.sisl.gpsvideorecorder.data.remote.utils.ApiService
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.models.LoginRequest
import com.sisl.gpsvideorecorder.domain.models.LoginResponse
import com.sisl.gpsvideorecorder.domain.models.UploadVideoDomain
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.state.DownloadState
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import com.sisl.gpsvideorecorder.utils.Utils.BASE_URL
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.BytePacketBuilder
import io.ktor.utils.io.core.writeFully
import io.ktor.utils.io.readAvailable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationRepositoryImpl(
    private val dao: LocationDao,
    private val apiService: ApiService
) :
    LocationRepository, KoinComponent {
    private val locationDataSource: LocationDataSource by inject()


    private val _uploadProgress = MutableStateFlow<Map<Long, UploadProgressData>>(emptyMap())
    val uploadProgress: StateFlow<Map<Long, UploadProgressData>> = _uploadProgress.asStateFlow()

    data class UploadProgressData(
        val progress: Float = 0f,
        val uploadedBytes: Long = 0,
        val totalBytes: Long = 0,
        val speed: Float = 0f, // bytes per second
        val startTime: Long = Clock.System.now().epochSeconds
    )


    override fun startLocationTracking() {
        locationDataSource.startLocationTracking()
    }

    override fun stopLocationTracking() {
        locationDataSource.stopLocationTracking()
    }

    override val locationUpdates: Flow<LocationData>
        get() = locationDataSource.locationUpdates

    override suspend fun insertLocation(location: LocationEntity) {
        dao.insertLocation(location)
    }

    override suspend fun getAllLocation(): Flow<List<LocationData>> {
        return dao.getAllLocations().map { locationEntity ->
            locationEntity.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getLastLocation(): Flow<LocationData?> {
        return dao.getLastLocation().map {
            it?.toDomain()
        }
    }

    override suspend fun getRecordForVideoHistory(): List<VideoItem> {
        return dao.getDateWisePendingLocationData()
    }

    override suspend fun updateLocationWithVideoName(
        videoId: Long,
        videoName: String,
        videoPath: String
    ) {
        dao.updateLocationWithVideoName(videoId, videoName, videoPath)
    }

    // Networking Operations
    override suspend fun uploadLocation(videoId: Long?): Flow<ApiResponse<LocationsUploadResp>> {
        val listOfPendingData: List<LocationEntity> = if (videoId == null) {
            dao.getAllPendingLocations()
        } else {
            dao.getAllLocationsBasedOnVideoId(videoId)
        }

        val locationDataList = listOfPendingData.map { it.toDomain() }
        var videoName = if (videoId == null) "" else listOfPendingData[0].videoName

        // Get current instant
        val now = Clock.System.now()
        // Convert to local date time in system timezone
        val currentDateTime: LocalDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        videoName = if (videoName.isNullOrEmpty()) "video_$currentDateTime.mp4" else videoName
        return apiService.uploadCoordinate(videoName, locationDataList)
    }

    override suspend fun deleteLocation(videoId: Long): Flow<ApiResponse<Boolean>> = flow {
        try {
            emit(ApiResponse.Loading)
            dao.deleteDataBasedOnVideoId(videoId)
            emit(ApiResponse.Success(true))
        } catch (ex: Exception) {
            emit(ApiResponse.Error(ex.message ?: "No data found", 409))
        }
    }

    override suspend fun userLogin(request: LoginRequest): Flow<ApiResponse<LoginResponse>> {
        return apiService.userLogin(request)
    }

    override suspend fun downloadAppFile(platformType: String): Flow<DownloadState> = flow {
        try {
            emit(DownloadState.Progress(0f))

            apiService.downloadAppFile(platformType).collect { progress ->
                emit(DownloadState.Progress(progress))
            }
            emit(DownloadState.Success(ByteArray(0)))
        } catch (e: Exception) {
            emit(DownloadState.Error(e.message ?: "Unknown error"))
        }
    }



//    override suspend fun uploadVideo(videoId: Long): Flow<ApiResponse<UploadVideoDomain>> {
//        return flow {
//            emit(ApiResponse.Loading)
//
//            try {
//                val videoPath = getVideoPath(videoId)
//                if (videoPath.isNullOrEmpty()) {
//                    emit(ApiResponse.Error("Video path not found"))
//                    return@flow
//                }
//
//                val file = getFileFromContentUri(videoPath)
//                if (file == null || !file.exists()) {
//                    emit(ApiResponse.Error("Video file not found"))
//                    return@flow
//                }
//
//                // Initialize progress
//                _uploadProgress.update { it + (videoId to UploadProgressData(totalBytes = file.length())) }
//
//                // Upload with progress tracking
//                val response = uploadVideoWithProgress(videoId, file)
//
//                emit(ApiResponse.Success(response))
//                _uploadProgress.update { it - videoId }
//
//            } catch (e: Exception) {
//                _uploadProgress.update { it - videoId }
//                emit(ApiResponse.Error(e.message ?: "Video upload failed"))
//            }
//        }
//    }
//
//    private suspend fun uploadVideoWithProgress(videoId: Long, file: ByteArray): VideoUploadResponse {
//        return httpClient.post("${BASE_URL}upload/video") {
//            setBody(
//                MultiPartFormDataContent(
//                    formData {
//                        append("videoId", videoId.toString())
//                        append(
//                            "video",
//                            file.readBytes(),
//                            Headers.build {
//                                append(HttpHeaders.ContentType, getMimeType(file))
//                                append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
//                            }
//                        )
//                    }
//                )
//            )
//
//            // Progress tracking
//            onUpload { bytesSentTotal, contentLength ->
//                val currentTime = Clock.System.now().epochSeconds
//                val currentProgress = _uploadProgress.value[videoId]
//                val startTime = currentProgress?.startTime ?: currentTime
//
//                // Calculate upload speed
//                val timeElapsed = (currentTime - startTime) / 1000f // in seconds
//                val speed = if (timeElapsed > 0) bytesSentTotal / timeElapsed else 0f
//
//                _uploadProgress.update { current ->
//                    current + (videoId to UploadProgressData(
//                        progress = bytesSentTotal.toFloat() / contentLength.toFloat(),
//                        uploadedBytes = bytesSentTotal,
//                        totalBytes = contentLength,
//                        speed = speed,
//                        startTime = startTime
//                    ))
//                }
//            }
//        }.body()
//    }
//
//    private fun getMimeType(file: File): String {
//        return when (file.extension.lowercase()) {
//            "mp4" -> "video/mp4"
//            "avi" -> "video/x-msvideo"
//            "mov" -> "video/quicktime"
//            "mkv" -> "video/x-matroska"
//            "webm" -> "video/webm"
//            else -> "video/mp4"
//        }
//    }
//
//    private fun getFileFromContentUri(contentUri: String): File? {
//        return try {
//            val uri = Uri.parse(contentUri)
//            when {
//                uri.scheme == "file" -> File(uri.path!!)
//                uri.scheme == "content" -> {
//                    val cursor = context.contentResolver.query(
//                        uri,
//                        arrayOf(MediaStore.Video.Media.DATA),
//                        null,
//                        null,
//                        null
//                    )
//                    cursor?.use {
//                        if (it.moveToFirst()) {
//                            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
//                            val filePath = it.getString(columnIndex)
//                            File(filePath)
//                        } else {
//                            null
//                        }
//                    }
//                }
//                else -> null
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            null
//        }
//    }
}
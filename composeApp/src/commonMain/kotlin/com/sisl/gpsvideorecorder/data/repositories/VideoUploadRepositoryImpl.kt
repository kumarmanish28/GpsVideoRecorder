package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.getPlatformFile
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.remote.api.VideoUploadApiService
import com.sisl.gpsvideorecorder.domain.repositories.VideoUploadRepository
import com.sisl.gpsvideorecorder.presentation.state.UploadVideoState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch


class VideoUploadRepositoryImpl(
    private val apiService: VideoUploadApiService,
    private val dao: LocationDao
) : VideoUploadRepository {

    private var currentUploadJob: Job? = null
    private var isUploading = false


    override suspend fun uploadVideo(videoId: Long): Flow<UploadVideoState> = callbackFlow {
        try {

            val videoPath = dao.getVideoPathByVideoId(videoId)

            if (isUploading || videoPath.isNullOrEmpty()) {
                trySend(UploadVideoState.Error("Upload already in progress"))
                close()
                return@callbackFlow
            }

            isUploading = true
            trySend(UploadVideoState.Preparing)

            val videoFile = getPlatformFile(videoPath)
            if (!videoFile.exists()) {
                trySend(UploadVideoState.Error("Video file not found at: $videoPath"))
                close()
                return@callbackFlow
            }

            val totalBytes = videoFile.getFileSize()
            val fileName = videoFile.name

            println("📁 Uploading video: $fileName, Size: $totalBytes bytes")

            val videoBytes = videoFile.readBytes()
            println("📁 Read ${videoBytes.size} bytes from file")

            if (videoBytes.isEmpty()) {
                trySend(UploadVideoState.Error("Video file is empty"))
                close()
                return@callbackFlow
            }

            currentUploadJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = apiService.uploadVideo(
                        videoBytes = videoBytes,
                        fileName = fileName,
                        onProgress = { progress ->
                            val uploadedBytes = (progress * totalBytes).toLong()
                            trySend(UploadVideoState.Progress(progress, uploadedBytes, totalBytes))
                        }
                    )

                    trySend(
                        UploadVideoState.Success(
                            filename = response.filename ?: "",
                            savedPath = response.saved_path ?: "",
                            message = response.message ?: ""
                        )
                    )
                    close()
                } catch (e: CancellationException) {
                    trySend(UploadVideoState.Cancelled)
                    close()
                } catch (e: Exception) {
                    trySend(UploadVideoState.Error(e.message ?: "Upload failed: ${e}"))
                    close()
                    e.printStackTrace()
                } finally {
                    isUploading = false
                }
            }

            awaitClose {
                currentUploadJob?.cancel()
                isUploading = false
            }

        } catch (e: Exception) {
            trySend(UploadVideoState.Error("Upload preparation failed: ${e.message}"))
            close()
            isUploading = false
            e.printStackTrace()
        }
    }

    override suspend fun cancelUpload() {
        currentUploadJob?.cancel()
        isUploading = false
    }
}
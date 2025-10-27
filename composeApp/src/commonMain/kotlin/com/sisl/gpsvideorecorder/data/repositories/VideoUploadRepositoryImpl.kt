package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.getPlatformFile
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.remote.api.VideoUploadApiService
import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadRequest
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


//class VideoUploadRepositoryImpl(
//    private val apiService: VideoUploadApiService,
//    private val dao: LocationDao
//) : VideoUploadRepository {
//
//    private var currentUploadJob: Job? = null
//    private var isUploading = false
//
//
//    override suspend fun uploadVideo(videoId: Long): Flow<UploadVideoState> = callbackFlow {
//        try {
//
//            val videoPath = dao.getVideoPathByVideoId(videoId)
//
//            if (isUploading || videoPath.isNullOrEmpty()) {
//                trySend(UploadVideoState.Error("Upload already in progress"))
//                close()
//                return@callbackFlow
//            }
//
//            isUploading = true
//            trySend(UploadVideoState.Preparing)
//
//            val videoFile = getPlatformFile(videoPath)
//            if (!videoFile.exists()) {
//                trySend(UploadVideoState.Error("Video file not found at: $videoPath"))
//                close()
//                return@callbackFlow
//            }
//
//            val totalBytes = videoFile.getFileSize()
//            val fileName = videoFile.name
//
//            println("📁 Uploading video: $fileName, Size: $totalBytes bytes")
//
//            val videoBytes = videoFile.readBytes()
//            println("📁 Read ${videoBytes.size} bytes from file")
//
//            if (videoBytes.isEmpty()) {
//                trySend(UploadVideoState.Error("Video file is empty"))
//                close()
//                return@callbackFlow
//            }
//
//            currentUploadJob = CoroutineScope(Dispatchers.IO).launch {
//                try {
//                    val response = apiService.uploadVideo(
////                        videoBytes = videoBytes,
//                        videoPath = videoFile.path,
//                        fileName = fileName,
//                        onProgress = { progress ->
//                            val uploadedBytes = (progress * totalBytes).toLong()
//                            trySend(UploadVideoState.Progress(progress, uploadedBytes, totalBytes))
//                        }
//                    )
//
//                    trySend(
//                        UploadVideoState.Success(
//                            filename = response.filename ?: "",
//                            savedPath = response.saved_path ?: "",
//                            message = response.message ?: ""
//                        )
//                    )
//                    close()
//                } catch (e: CancellationException) {
//                    trySend(UploadVideoState.Cancelled)
//                    close()
//                } catch (e: Exception) {
//                    trySend(UploadVideoState.Error(e.message ?: "Upload failed: ${e}"))
//                    close()
//                    e.printStackTrace()
//                } finally {
//                    isUploading = false
//                }
//            }
//
//            awaitClose {
//                currentUploadJob?.cancel()
//                isUploading = false
//            }
//
//        } catch (e: Exception) {
//            trySend(UploadVideoState.Error("Upload preparation failed: ${e.message}"))
//            close()
//            isUploading = false
//            e.printStackTrace()
//        }
//    }
//
//    override suspend fun cancelUpload() {
//        currentUploadJob?.cancel()
//        isUploading = false
//    }
//}

class VideoUploadRepositoryImpl(
    private val apiService: VideoUploadApiService,
    private val dao: LocationDao
) : VideoUploadRepository {

    private var currentUploadJob: Job? = null
    private var isUploading = false
    private val CHUNK_SIZE = 1024 * 1024 // 1MB chunks

    override suspend fun uploadVideo(videoId: Long): Flow<UploadVideoState> = callbackFlow {
        try {
            val videoPath = dao.getVideoPathByVideoId(videoId)

            if (isUploading || videoPath.isNullOrEmpty()) {
                trySend(UploadVideoState.Error("Upload already in progress or invalid video path"))
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

            currentUploadJob = CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Step 1: Initiate upload
                    trySend(UploadVideoState.Initiating)
                    val initiateResponse = apiService.initiateUpload(
                        InitiateUploadRequest(
                            filename = fileName,
                            total_size = totalBytes
                        )
                    )

                    val uploadId = initiateResponse.upload_id
                    if (uploadId.isNullOrEmpty()) {
                        throw Exception("Failed to get upload ID")
                    }

                    println("🆔 Upload initiated with ID: $uploadId")

                    // Step 2: Upload chunks
                    trySend(UploadVideoState.Uploading(0f, 0L, totalBytes))

                    val inputStream = videoFile.inputStream()
                    val buffer = ByteArray(CHUNK_SIZE)
                    var chunkNumber = 0
                    var bytesUploaded = 0L

                    while (true) {
                        val bytesRead = inputStream.read(buffer)
                        if (bytesRead == -1) break

                        chunkNumber++

                        // Take only the portion of buffer that was actually read
                        val chunkData = if (bytesRead < CHUNK_SIZE) {
                            buffer.copyOf(bytesRead)
                        } else {
                            buffer
                        }

                        println("📦 Uploading chunk $chunkNumber, size: ${chunkData.size} bytes")

                        val chunkResponse = apiService.uploadChunk(
                            uploadId = uploadId,
                            chunkNumber = chunkNumber,
                            chunkData = chunkData
                        ) { chunkProgress ->
                            // Calculate overall progress including previous chunks
                            val chunkContribution = chunkData.size.toFloat() / totalBytes
                            val currentProgress = (bytesUploaded.toFloat() / totalBytes) + (chunkProgress * chunkContribution)
                            trySend(UploadVideoState.Uploading(currentProgress, bytesUploaded + (chunkProgress * chunkData.size).toLong(), totalBytes))
                        }

                        if (chunkResponse.message.isNullOrEmpty()) {
                            throw Exception("Chunk $chunkNumber upload failed: ${chunkResponse.message}")
                        }

                        bytesUploaded += chunkData.size

                        // Send progress update after chunk completion
                        val progress = bytesUploaded.toFloat() / totalBytes
                        trySend(UploadVideoState.Uploading(progress, bytesUploaded, totalBytes))

                        println("✅ Chunk $chunkNumber uploaded successfully, total progress: ${(progress * 100).toInt()}%")
                    }

                    inputStream.close()

                    // Step 3: Complete upload
                    trySend(UploadVideoState.Finalizing)
                    val completeResponse = apiService.completeUpload(
                        CompleteUploadRequest(
                            upload_id = uploadId,
                        )
                    )

                    trySend(
                        UploadVideoState.Success(
                            filename = completeResponse.filename ?: fileName,
                            savedPath = completeResponse.saved_path ?: "",
                            message = completeResponse.message ?: "Upload completed successfully"
                        )
                    )
                    close()

                } catch (e: CancellationException) {
                    // If upload was cancelled, notify the server
                    try {
                        val uploadId = getCurrentUploadId() // You might need to track this
                        uploadId?.let {
                            apiService.cancelUpload(CancelUploadRequest(upload_id = it))
                        }
                    } catch (cancelEx: Exception) {
                        // Ignore cancellation errors during cleanup
                    }
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

    // Helper method to track current upload ID (you might want to store this properly)
    private fun getCurrentUploadId(): String? {
        return null // Implement proper tracking if needed
    }
}
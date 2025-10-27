package com.sisl.gpsvideorecorder.data.remote.api

import com.sisl.gpsvideorecorder.data.datasources.getPlatformFile
import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.ChunkUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadResponse
import com.sisl.gpsvideorecorder.presentation.state.VideoUploadResponse
import com.sisl.gpsvideorecorder.utils.Utils
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.plugins.timeout
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.content.OutgoingContent
import io.ktor.http.contentType
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.writeFully

//class VideoUploadApiServiceImpl(
//    private val httpClient: HttpClient
//) : VideoUploadApiService {
//
//    override suspend fun uploadVideo(
//        videoBytes: ByteArray,
//        fileName: String,
//        onProgress: (percentage: Float) -> Unit
//    ): VideoUploadResponse {
//
//        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo") {
//            setBody(
//                MultiPartFormDataContent(
//                    formData {
//                        append(
//                            key = "video",
//                            value = videoBytes,
//                            headers = Headers.build {
//                                append(
//                                    HttpHeaders.ContentType,
//                                    "video/mp4"
//                                )
//                                append(
//                                    HttpHeaders.ContentDisposition,
//                                    "form-data; name=\"video\"; filename=\"$fileName\""
//                                )
//                            }
//                        )
//                    }
//                ))
//
//            // Progress tracking for the entire request
//            onUpload { bytesSentTotal, contentLength ->
//                contentLength?.let {
//                    val progress = bytesSentTotal.toFloat() / contentLength
//                    onProgress(progress.coerceIn(0f, 1f))
//                }
//            }
//        }.body()
//    }
//}

//class VideoUploadApiServiceImpl(
//    private val httpClient: HttpClient
//) : VideoUploadApiService {
//
//    override suspend fun uploadVideo(
//        videoPath: String,
//        fileName: String,
//        onProgress: (percentage: Float) -> Unit
//    ): VideoUploadResponse {
//        val videoFile = getPlatformFile(videoPath)
//
//        if (!videoFile.exists()) {
//            throw IllegalArgumentException("Video file does not exist: $videoPath")
//        }
//
//        val fileSize = videoFile.getFileSize()
//        println("🚀 Starting upload: $fileName (${fileSize / (1024 * 1024 * 1024)} GB)")
//
//        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo") {
//            setBody(object : OutgoingContent.WriteChannelContent() {
//                override val contentLength: Long = fileSize
//                override val contentType: ContentType = ContentType.MultiPart.FormData
//
//                override suspend fun writeTo(channel: ByteWriteChannel) {
//                    videoFile.inputStream().use { stream ->
//                        val buffer = ByteArray(64 * 1024) // 64KB buffer for better performance
//                        var totalBytesWritten = 0L
//                        var lastProgressUpdate = 0f
//                        var bytesSinceLastUpdate = 0L
//                        val updateThreshold = fileSize * 0.01f // Update every 1% or 10MB, whichever is smaller
//
//                        while (true) {
//                            val bytesRead = stream.read(buffer)
//                            if (bytesRead <= 0) break
//
//                            // Write chunk to channel
//                            channel.writeFully(buffer, 0, bytesRead)
//                            totalBytesWritten += bytesRead
//                            bytesSinceLastUpdate += bytesRead
//
//                            // Calculate progress
//                            val progress = totalBytesWritten.toFloat() / fileSize
//
//                            // Update progress only when significant change occurs
//                            if (progress - lastProgressUpdate >= 0.01f ||
//                                bytesSinceLastUpdate >= 10 * 1024 * 1024L || // Or every 10MB
//                                progress >= 1.0f) {
//
//                                onProgress(progress.coerceIn(0f, 1.0f))
//                                lastProgressUpdate = progress
//                                bytesSinceLastUpdate = 0L
//                            }
//
//                            // Small delay to prevent overwhelming the system
//                            if (bytesRead > 0) {
//                                kotlinx.coroutines.delay(1L)
//                            }
//                        }
//
//                        // Final progress update
//                        onProgress(1.0f)
//                        println("✅ Upload completed: $totalBytesWritten bytes written")
//                    }
//                }
//            })
//
//            headers {
//                append(HttpHeaders.ContentDisposition, "form-data; name=\"video\"; filename=\"$fileName\"")
//                append(HttpHeaders.ContentType, "video/mp4")
//                // Add any authentication headers if needed
//                // append("Authorization", "Bearer $token")
//            }
////            // Timeout configuration for large files
//            timeout {
//                requestTimeoutMillis = 30 * 60 * 1000L // 30 minutes
//                connectTimeoutMillis = 60 * 1000L // 1 minute
//                socketTimeoutMillis = 30 * 60 * 1000L // 30 minutes
//            }
//        }.body()
//
//    }
//}

class VideoUploadApiServiceImpl(
    private val httpClient: HttpClient
) : VideoUploadApiService {

    override suspend fun initiateUpload(request: InitiateUploadRequest): InitiateUploadResponse {
        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo/initiate") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun uploadChunk(
        uploadId: String,
        chunkNumber: Int,
        chunkData: ByteArray,
        onProgress: (Float) -> Unit
    ): ChunkUploadResponse {
        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo/chunk") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append("upload_id", uploadId)
                        append("chunk_number", chunkNumber.toString())
                        append("chunk", chunkData, Headers.build {
                            append(HttpHeaders.ContentType, "application/octet-stream")
                            append(
                                HttpHeaders.ContentDisposition,
                                "filename=\"chunk_$chunkNumber.bin\""
                            )
                        })
                    }
                ))

            onUpload { bytesSentTotal, contentLength ->
                contentLength?.let {
                    val progress = bytesSentTotal.toFloat() / contentLength
                    onProgress(progress.coerceIn(0f, 1.0f))
                }
            }
        }.body()
    }

    override suspend fun completeUpload(request: CompleteUploadRequest): CompleteUploadResponse {
        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo/complete") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

    override suspend fun cancelUpload(request: CancelUploadRequest): CancelUploadResponse {
        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo/cancel") {
            contentType(ContentType.Application.Json)
            setBody(request)
        }.body()
    }

}
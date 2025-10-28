package com.sisl.gpsvideorecorder.data.remote.api

import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.ChunkUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadResponse
import com.sisl.gpsvideorecorder.utils.Utils
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onUpload
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType

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
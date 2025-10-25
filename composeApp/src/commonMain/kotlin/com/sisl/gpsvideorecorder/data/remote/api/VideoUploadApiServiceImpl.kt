package com.sisl.gpsvideorecorder.data.remote.api

import com.sisl.gpsvideorecorder.presentation.state.VideoUploadResponse
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

    override suspend fun uploadVideo(
        videoBytes: ByteArray,
        fileName: String,
        onProgress: (percentage: Float) -> Unit
    ): VideoUploadResponse {
        return httpClient.post("${Utils.BASE_URL}/location/uploadVideo") {
            setBody(
                MultiPartFormDataContent(
                    formData {
                        append(
                            key = "video",
                            value = videoBytes,
                            headers = Headers.build {
                                append(
                                    HttpHeaders.ContentType,
                                    "video/mp4"
                                )
                                append(
                                    HttpHeaders.ContentDisposition,
                                    "form-data; name=\"video\"; filename=\"$fileName\""
                                )
                            }
                        )
                    }
                ))

            // Progress tracking for the entire request
            onUpload { bytesSentTotal, contentLength ->
                contentLength?.let {
                    val progress = bytesSentTotal.toFloat() / contentLength
                    onProgress(progress.coerceIn(0f, 1f))
                }
            }
        }.body()
    }
}
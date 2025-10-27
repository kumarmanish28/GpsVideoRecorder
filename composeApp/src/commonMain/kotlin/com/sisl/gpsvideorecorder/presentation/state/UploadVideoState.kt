package com.sisl.gpsvideorecorder.presentation.state

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//sealed class UploadVideoState {
//    object Idle : UploadVideoState()
//    object Preparing : UploadVideoState()
//    data class Progress(val percentage: Float, val uploadedBytes: Long, val totalBytes: Long) : UploadVideoState()
//    data class Success(val filename: String, val savedPath: String, val message: String) : UploadVideoState()
//    data class Error(val message: String) : UploadVideoState()
//    object Cancelled : UploadVideoState()
//}


@Serializable
data class VideoUploadResponse(
    @SerialName("filename")
    val filename: String?,
    @SerialName("message")
    val message: String?,
    @SerialName("saved_path")
    val saved_path: String?,
    val error: String?=null
)


sealed class UploadVideoState {
    object Idle : UploadVideoState()
    object Preparing : UploadVideoState()
    object Initiating : UploadVideoState()
    data class Uploading(val progress: Float, val uploadedBytes: Long, val totalBytes: Long) : UploadVideoState()
    object Finalizing : UploadVideoState()
    data class Success(val filename: String, val savedPath: String, val message: String) : UploadVideoState()
    data class Error(val message: String) : UploadVideoState()
    object Cancelled : UploadVideoState()
}
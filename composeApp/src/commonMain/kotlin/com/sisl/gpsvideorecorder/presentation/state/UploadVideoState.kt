package com.sisl.gpsvideorecorder.presentation.state

sealed class UploadVideoState {
    object Idle : UploadVideoState()
    object Preparing : UploadVideoState()
    data class Progress(val percentage: Float, val uploadedBytes: Long, val totalBytes: Long) :
        UploadVideoState()

    data class Success(val filename: String, val savedPath: String, val message: String) :
        UploadVideoState()

    data class Error(val message: String) : UploadVideoState()
    object Cancelled : UploadVideoState()
}

data class VideoUploadResponse(
    val filename: String?,
    val message: String?,
    val saved_path: String?,
    val error: String?
)
package com.sisl.gpsvideorecorder.presentation.state

sealed class DownloadState {
    data class Progress(val percentage: Float) : DownloadState()
    data class Success(val bytes: ByteArray?) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

package com.sisl.gpsvideorecorder.presentation.state

sealed class DownloadState {
    object Loading : DownloadState()
    data class Progress(val percentage: Float) : DownloadState()
    data class Success(val filePath: String?) : DownloadState()
    data class Error(val message: String) : DownloadState()
}

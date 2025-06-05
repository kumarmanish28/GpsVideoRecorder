package com.sisl.gpsvideorecorder.presentation.components.recorder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// In commonMain
expect class VideoRecorder(onVideoRecorded: (VideoRecInfo) -> Unit) {
    fun initialize(onReady: () -> Unit = {})
    fun startRecording()
    fun stopRecording()

    @Composable
    fun CameraPreview(modifier: Modifier = Modifier)
}

enum class RecordingState {
    RECORDING, STOPPED
}

data class VideoRecInfo(
    var videoUri: String? = "",
    var videoName: String? = "",
)

@Composable
expect fun rememberVideoRecorder(onVideoRecorded: (VideoRecInfo) -> Unit): VideoRecorder
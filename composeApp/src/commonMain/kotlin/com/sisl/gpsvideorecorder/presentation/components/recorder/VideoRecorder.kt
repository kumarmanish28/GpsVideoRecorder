package com.sisl.gpsvideorecorder.presentation.components.recorder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// In commonMain
expect class VideoRecorder() {
    fun initialize(onReady: () -> Unit = {})
    fun startRecording()
    fun stopRecording()

    @Composable
    fun CameraPreview(modifier: Modifier = Modifier)
}

enum class RecordingState {
    RECORDING, STOPPED
}

@Composable
expect fun rememberVideoRecorder(): VideoRecorder
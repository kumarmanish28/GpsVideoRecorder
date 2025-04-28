package com.sisl.gpsvideorecorder.data

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

// In commonMain
expect class VideoRecorder() {
    fun initialize(onReady : () -> Unit = {})
    fun startRecording()
    fun stopRecording()
    fun getRecordingState(): RecordingState

    @Composable
    fun CameraPreview(modifier: Modifier = Modifier)
}

enum class RecordingState {
    IDLE, RECORDING, STOPPED
}

@Composable
expect fun rememberVideoRecorder(): VideoRecorder
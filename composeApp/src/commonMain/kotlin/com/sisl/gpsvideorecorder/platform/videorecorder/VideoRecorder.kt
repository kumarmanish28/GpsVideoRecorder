package com.sisl.gpsvideorecorder.platform.videorecorder

import androidx.compose.runtime.Composable

interface VideoRecorder {
    fun startRecording()
    fun stopRecording()
    fun isRecording(): Boolean
}

expect fun getVideoRecorder(context: Any? = null): VideoRecorder

@Composable
expect fun CameraPreview(recorder: VideoRecorder, context: Any?)



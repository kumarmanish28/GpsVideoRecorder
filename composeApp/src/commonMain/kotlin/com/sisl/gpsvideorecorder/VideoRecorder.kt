package com.sisl.gpsvideorecorder

interface VideoRecorder {
    fun startRecording()
    fun stopRecording()
    fun isRecording(): Boolean
}

expect fun getVideoRecorder(context: Any? = null): VideoRecorder


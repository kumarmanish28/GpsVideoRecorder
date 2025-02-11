package com.sisl.gpsvideorecorder

import com.sisl.gpsvideorecorder.videorecorder.IOSVideoRecorder

actual fun getVideoRecorder(context: Any?): VideoRecorder = IOSVideoRecorder()
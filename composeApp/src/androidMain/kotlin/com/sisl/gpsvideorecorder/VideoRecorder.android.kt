package com.sisl.gpsvideorecorder

import android.content.Context
import com.sisl.gpsvideorecorder.videorecorder.AndroidVideoRecorder

actual fun getVideoRecorder(context: Any?): VideoRecorder = AndroidVideoRecorder(context as Context)
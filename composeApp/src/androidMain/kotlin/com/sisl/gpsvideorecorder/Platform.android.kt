package com.sisl.gpsvideorecorder

class AndroidPlatform : Platform {
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()
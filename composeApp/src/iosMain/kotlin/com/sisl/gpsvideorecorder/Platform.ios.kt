package com.sisl.gpsvideorecorder

class IOSPlatform: Platform {
    override val name: String = "IOs"
}

actual fun getPlatform(): Platform = IOSPlatform()
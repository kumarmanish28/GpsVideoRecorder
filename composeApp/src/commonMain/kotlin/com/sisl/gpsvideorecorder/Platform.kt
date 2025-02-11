package com.sisl.gpsvideorecorder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
package com.sisl.gpsvideorecorder

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

// commonMain
expect fun getAppVersion(): String

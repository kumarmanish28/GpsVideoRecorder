package com.sisl.gpsvideorecorder

import platform.Foundation.NSBundle

class IOSPlatform: Platform {
//    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IOSPlatform()
actual fun getAppVersion(): String {
    return try {
        val mainBundle = NSBundle.mainBundle
        val infoDictionary = mainBundle.infoDictionary

        // Get version number (e.g., "1.0.0")
        val version = infoDictionary?.get("CFBundleShortVersionString") as? String
        // Get build number (e.g., "123")
        val build = infoDictionary?.get("CFBundleVersion") as? String

        when {
            version != null && build != null -> "$version ($build)"
            version != null -> version
            build != null -> build
            else -> "Unknown"
        }
    } catch (e: Exception) {
        "Unknown"
    }
}
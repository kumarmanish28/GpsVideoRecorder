package com.sisl.gpsvideorecorder

import com.sisl.gpsvideorecorder.di.AndroidContextHolder


class AndroidPlatform : Platform {
    //    override val name: String = "Android ${Build.VERSION.SDK_INT}"
    override val name: String = "Android"
}

actual fun getPlatform(): Platform = AndroidPlatform()
actual fun getAppVersion(): String {
    val androidContext = AndroidContextHolder.applicationContext
    return try {
        val pInfo = androidContext.packageManager.getPackageInfo(androidContext.packageName, 0)
        pInfo.versionName ?: "1.0.0"
    } catch (e: Exception) {
        "1.0.0"
    }
}
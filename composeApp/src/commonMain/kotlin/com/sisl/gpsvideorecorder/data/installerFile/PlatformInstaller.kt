package com.sisl.gpsvideorecorder.data.installerFile

import io.ktor.utils.io.ByteReadChannel

expect class PlatformInstaller(context: Any?) {
    suspend fun saveAndInstallApp(bytes: ByteArray)
}

expect suspend fun saveToFile(fileName: String, channel: ByteReadChannel): String

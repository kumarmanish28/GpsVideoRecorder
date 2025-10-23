package com.sisl.gpsvideorecorder.data.installerFile

expect class PlatformInstaller(context: Any?) {
    suspend fun saveAndInstallApp(bytes: ByteArray)
}
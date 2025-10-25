package com.sisl.gpsvideorecorder.data.datasources

// commonMain
expect class PlatformFile {
    val path: String
    val name: String
    suspend fun readBytes(): ByteArray
    suspend fun getFileSize(): Long
    fun exists(): Boolean
}

expect fun getPlatformFile(filePath: String): PlatformFile
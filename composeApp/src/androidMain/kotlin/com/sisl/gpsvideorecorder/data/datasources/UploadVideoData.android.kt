package com.sisl.gpsvideorecorder.data.datasources

import java.io.File

actual class PlatformFile(actual val path: String) {
    private val file = File(path)

    actual val name: String
        get() = file.name

    actual suspend fun readBytes(): ByteArray {
        return file.readBytes()
    }

    actual suspend fun getFileSize(): Long {
        return file.length()
    }

    actual fun exists(): Boolean {
        return file.exists()
    }
}

actual fun getPlatformFile(filePath: String): PlatformFile {
    return PlatformFile(filePath)
}

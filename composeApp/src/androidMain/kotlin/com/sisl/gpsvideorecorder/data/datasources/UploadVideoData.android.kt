package com.sisl.gpsvideorecorder.data.datasources

import io.ktor.util.cio.readChannel
import java.io.File
import java.io.FileInputStream

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

    actual fun inputStream(): PlatformInputStream {
        return PlatformInputStream(FileInputStream(file))
    }

    fun getFile(): File = file
}

actual class PlatformInputStream(private val inputStream: FileInputStream) : AutoCloseable {
    actual suspend fun read(buffer: ByteArray): Int {
        return inputStream.read(buffer)
    }

    actual override fun close() {
        inputStream.close()
    }
}

actual fun getPlatformFile(filePath: String): PlatformFile {
    return PlatformFile(filePath)
}

actual fun PlatformFile.asKtorFile(): Any = this.getFile()

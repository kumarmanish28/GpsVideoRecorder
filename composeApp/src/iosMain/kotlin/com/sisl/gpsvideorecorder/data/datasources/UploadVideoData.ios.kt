package com.sisl.gpsvideorecorder.data.datasources

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.usePinned
import kotlinx.io.bytestring.toByteString
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSInputStream
import platform.Foundation.NSString
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.inputStreamWithFileAtPath
import platform.Foundation.lastPathComponent


actual class PlatformFile(actual val path: String) {
    private val fileManager = NSFileManager.defaultManager

    actual val name: String
        get() = (path as NSString).lastPathComponent

    actual suspend fun readBytes(): ByteArray {
        val nsData = NSData.dataWithContentsOfFile(path)
        return nsData?.toByteString()?.toByteArray() ?: byteArrayOf()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getFileSize(): Long {
        val attributes = fileManager.attributesOfItemAtPath(path, null)
        return (attributes?.get("NSFileSize") as? Long) ?: 0L
    }

    actual fun exists(): Boolean {
        return fileManager.fileExistsAtPath(path)
    }

    actual fun inputStream(): PlatformInputStream {
        return PlatformInputStream(path)
    }
}

actual class PlatformInputStream(private val filePath: String) : AutoCloseable {
    private var inputStream: NSInputStream? = null
    private var isClosed = false

    init {
        inputStream = NSInputStream.inputStreamWithFileAtPath(filePath)
        inputStream?.open()
    }

    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun read(buffer: ByteArray): Int {
        if (isClosed) return -1
        val stream = inputStream ?: return -1

        if (!stream.hasBytesAvailable()) {
            return -1
        }

        return buffer.usePinned { pinned ->
            val bytesRead = stream.read(
                buffer = pinned.addressOf(0).reinterpret(),
                maxLength = buffer.size.toULong()
            )
            // Convert Long to Int and handle error cases
            when {
                bytesRead < 0 -> -1 // Error
                bytesRead > Int.MAX_VALUE -> Int.MAX_VALUE // Shouldn't happen with normal buffer sizes
                else -> bytesRead.toInt()
            }
        }
    }

    actual override fun close() {
        if (!isClosed) {
            inputStream?.close()
            inputStream = null
            isClosed = true
        }
    }
}

actual fun getPlatformFile(filePath: String): PlatformFile {
    return PlatformFile(filePath)
}
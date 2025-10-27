package com.sisl.gpsvideorecorder.data.datasources

// iosMain
import kotlinx.cinterop.ExperimentalForeignApi
import okio.ByteString.Companion.toByteString
import platform.Foundation.NSData
import platform.Foundation.NSFileManager
import platform.Foundation.NSString
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.lastPathComponent
import platform.Foundation.stringWithContentsOfFile

actual class PlatformFile(actual val path: String) {
    private val fileManager = NSFileManager.defaultManager

    actual val name: String
        get() = (path as NSString).lastPathComponent

    actual suspend fun readBytes(): ByteArray {
        val nsData = NSData.dataWithContentsOfFile(path)
        nsData?.toByteString()?.toByteArray()
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
        TODO("Not yet implemented")
    }
}

actual fun getPlatformFile(filePath: String): PlatformFile {
    return PlatformFile(filePath)
}


actual fun PlatformFile.asKtorFile(): Any {
    TODO("Not yet implemented")
}

actual class PlatformInputStream : AutoCloseable {
    actual suspend fun read(buffer: ByteArray): Int {
        TODO("Not yet implemented")
    }

    actual override fun close() {
    }
}
package com.sisl.gpsvideorecorder.data.remote.utils

import kotlinx.cinterop.ExperimentalForeignApi
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getCacheDirectory(): Path {
    return try {
        val fileManager = NSFileManager.defaultManager
        val cacheURL = fileManager.URLForDirectory(
            directory = NSCachesDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = true,
            error = null
        )

        val cachePath = cacheURL?.path ?: "/tmp"
        println("✅ iOS Cache Directory: $cachePath")
        cachePath.toPath()
    } catch (e: Exception) {
        println("❌ Error getting cache directory: ${e.message}")
        "/tmp".toPath() // Fallback to temp directory
    }
}

actual fun isFileExists(filePath: Path): Boolean {
    return try {
        FileSystem.SYSTEM.exists(filePath)
    } catch (e: Exception) {
        false
    }
}

actual fun getFileSize(filePath: Path): Long {
    return try {
        val metadata = FileSystem.SYSTEM.metadata(filePath)
        metadata.size ?: 0L
    } catch (e: Exception) {
        0L
    }
}
package com.sisl.gpsvideorecorder.data.remote.utils

import com.sisl.gpsvideorecorder.di.provideContext
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

actual fun getFileSize(filePath: Path): Long {
    return try {
        FileSystem.SYSTEM.metadata(filePath).size ?: 0L
    } catch (e: Exception) {
        0L
    }
}

actual fun isFileExists(filePath: Path): Boolean {
    return FileSystem.SYSTEM.exists(filePath)
}

actual suspend fun getCacheDirectory(): Path {
    val context = provideContext() // Get from your DI or application
    return context.cacheDir.absolutePath.toPath()
}
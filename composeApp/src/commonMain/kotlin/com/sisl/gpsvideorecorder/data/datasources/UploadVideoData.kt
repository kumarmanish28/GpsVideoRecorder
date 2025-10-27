package com.sisl.gpsvideorecorder.data.datasources

// commonMain
expect class PlatformFile {
    val path: String
    val name: String
    suspend fun readBytes(): ByteArray
    suspend fun getFileSize(): Long
    fun exists(): Boolean
    fun inputStream(): PlatformInputStream // Add this
}

//expect class PlatformInputStream {
//    suspend fun read(buffer: ByteArray): Int
//    suspend fun close()
//}
expect class PlatformInputStream : AutoCloseable {
    suspend fun read(buffer: ByteArray): Int
    override fun close()
}

expect fun getPlatformFile(filePath: String): PlatformFile

expect fun PlatformFile.asKtorFile(): Any
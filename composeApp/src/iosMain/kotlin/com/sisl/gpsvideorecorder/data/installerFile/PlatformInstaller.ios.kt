package com.sisl.gpsvideorecorder.data.installerFile

import com.sisl.gpsvideorecorder.utils.Utils
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.core.readBytes
import io.ktor.utils.io.readRemaining
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.io.readByteArray
import platform.Foundation.*
import platform.UIKit.UIApplication
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

actual class PlatformInstaller actual constructor(private val context: Any?) {

    actual suspend fun saveAndInstallApp(bytes: ByteArray) {
        val fileManager = NSFileManager.defaultManager
        val documents = fileManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ).first() as NSURL

        val fileUrl = documents.URLByAppendingPathComponent(Utils.IOS_IPA_NAME)

        // ✅ Convert ByteArray → NSData safely
        val data = bytes.toNSData()

        fileUrl?.let {
            // ✅ Write IPA file to Documents
            data.writeToURL(fileUrl, true)

            // ✅ Open the saved file URL
            UIApplication.sharedApplication.openURL(fileUrl)
        }
    }
}


@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun ByteArray.toNSData(): NSData = usePinned { pinned ->
    NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
}


actual suspend fun saveToFile(fileName: String, channel: ByteReadChannel): String {
    val fileManager = NSFileManager.defaultManager
    val documentsDir = fileManager.URLsForDirectory(
        NSDocumentDirectory, NSUserDomainMask
    ).first() as NSURL

    val fileUrl = documentsDir.URLByAppendingPathComponent(fileName)
    val data = channel.readRemaining().readByteArray().toNSData()
    fileUrl?.let {
        data.writeToURL(fileUrl, true)
    }
    return fileUrl?.path!!
}


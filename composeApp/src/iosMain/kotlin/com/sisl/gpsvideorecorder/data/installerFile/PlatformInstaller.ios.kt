package com.sisl.gpsvideorecorder.data.installerFile

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
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

        val fileUrl = documents.URLByAppendingPathComponent("update.ipa")

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
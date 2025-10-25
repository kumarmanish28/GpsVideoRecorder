package com.sisl.gpsvideorecorder.data.installerFile

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.content.FileProvider
import com.sisl.gpsvideorecorder.data.datasources.createDataStore
import com.sisl.gpsvideorecorder.di.provideContext
import com.sisl.gpsvideorecorder.utils.Utils
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import java.io.File

//actual class PlatformInstaller actual constructor(private val context: Any?) {
//    actual suspend fun saveAndInstallApp(bytes: ByteArray) {
//        try {
//            val file = File((context as Context).getExternalFilesDir(null), Utils.ANDROID_APK_NAME)
//            file.writeBytes(bytes)
//
//            val uri = FileProvider.getUriForFile(
//                context,
//                "${context.packageName}.provider",
//                file
//            )
//
//            val intent = Intent(Intent.ACTION_VIEW).apply {
//                setDataAndType(uri, "application/vnd.android.package-archive")
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//            }
//            context.startActivity(intent)
//        } catch (e: Exception) {
//            print("Exception in installing apk: ${e.message}")
//        }
//    }
//}
//
//fun createPlatformInstallerInstance(context: Context): PlatformInstaller {
//    return PlatformInstaller(context)
//}

// Android Implementation
actual class PlatformInstaller actual constructor(private val context: Any?) {
    actual suspend fun saveAndInstallApp(bytes: ByteArray) {
        try {
            val androidContext = context as Context

            // Create file in external storage with proper permissions
            val file = File(androidContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), Utils.ANDROID_APK_NAME)

            // Write bytes to file
            file.outputStream().use { output ->
                output.write(bytes)
                output.flush()
            }

            // Verify file was written
            if (!file.exists() || file.length() == 0L) {
                throw Exception("Failed to write APK file")
            }

            println("📱 APK saved: ${file.absolutePath}, Size: ${file.length()} bytes")

            // Get URI using FileProvider
            val uri = FileProvider.getUriForFile(
                androidContext,
                "${androidContext.packageName}.provider",
                file
            )

            // Create install intent
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Start installation
            androidContext.startActivity(intent)
            println("✅ Installation started successfully")

        } catch (e: Exception) {
            println("❌ Exception in installing APK: ${e.message}")
            e.printStackTrace()
            throw e // Re-throw to handle in ViewModel
        }
    }
}

// Factory function
fun createPlatformInstallerInstance(context: Context): PlatformInstaller {
    return PlatformInstaller(context)
}

actual suspend fun saveToFile(fileName: String, channel: ByteReadChannel): String {
    val context = provideContext() // your injected or static context
    val outputFile = File(context.cacheDir, fileName)
    val buffer = ByteArray(8192)
    outputFile.outputStream().use { output ->
        while (!channel.isClosedForRead) {
            val bytesRead = channel.readAvailable(buffer)
            if (bytesRead > 0) {
                output.write(buffer, 0, bytesRead)
            }
        }
    }
    return outputFile.absolutePath
}

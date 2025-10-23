package com.sisl.gpsvideorecorder.data.installerFile

import android.content.Context
import android.content.Intent
import androidx.camera.core.impl.utils.ContextUtil.getApplicationContext
import androidx.core.content.FileProvider
import com.sisl.gpsvideorecorder.data.datasources.createDataStore
import com.sisl.gpsvideorecorder.di.provideContext
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.readAvailable
import java.io.File

actual class PlatformInstaller actual constructor(private val context: Any?) {
    actual suspend fun saveAndInstallApp(bytes: ByteArray) {
        try {
            val file = File((context as Context).getExternalFilesDir(null), "update.apk")
            file.writeBytes(bytes)

            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            print("Exception in installing apk: ${e.message}")
        }
    }
}

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

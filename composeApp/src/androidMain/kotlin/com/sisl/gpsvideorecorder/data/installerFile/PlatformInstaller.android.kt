package com.sisl.gpsvideorecorder.data.installerFile

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import java.io.File

actual class PlatformInstaller actual constructor(private val context: Any?) {
    actual suspend fun saveAndInstallApp(bytes: ByteArray) {
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
    }
}
package com.sisl.gpsvideorecorder

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.sisl.gpsvideorecorder.platform.permission.AndroidPermissionHandler

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: AndroidPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        permissionHandler = AndroidPermissionHandler(this)
        val permission = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            // Required for Android 9 (API 28) and below
            permission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            permission.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Only READ permission is needed in Android 10 (API 29)
            permission.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Required for Android 13+ (API 33) for notifications
            permission.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }


        setContent {
            App(permissionHandler, permission, this)
        }
    }
}




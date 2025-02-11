package com.sisl.gpsvideorecorder

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.sisl.gpsvideorecorder.permission.AndroidPermissionHandler

class MainActivity : ComponentActivity() {

    private lateinit var permissionHandler: AndroidPermissionHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionHandler = AndroidPermissionHandler(this)
        val permission = mutableListOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission.add(android.Manifest.permission.POST_NOTIFICATIONS)
        }
        setContent {
            App(permissionHandler, permission, this)
        }
    }
}




package com.sisl.gpsvideorecorder

import androidx.compose.ui.window.ComposeUIViewController
import com.sisl.gpsvideorecorder.permission.IOSPermissionHandler
import com.sisl.gpsvideorecorder.viewmodel.LocationViewModel

fun MainViewController() = ComposeUIViewController {
    val listOfPermission = mutableListOf<String>()
    listOfPermission.add("camera")
    listOfPermission.add("microphone")
    listOfPermission.add("location")
    App(IOSPermissionHandler(), listOfPermission)
}
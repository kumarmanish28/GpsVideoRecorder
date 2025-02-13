package com.sisl.gpsvideorecorder

import androidx.compose.ui.window.ComposeUIViewController
import com.sisl.gpsvideorecorder.di.initKoin
import com.sisl.gpsvideorecorder.platform.permission.IOSPermissionHandler
import com.sisl.gpsvideorecorder.presentation.viewmodel.LocationViewModel

fun MainViewController() = ComposeUIViewController(
    configure = {
        initKoin()
    }
) {
    val listOfPermission = mutableListOf<String>()
    listOfPermission.add("camera")
    listOfPermission.add("microphone")
    listOfPermission.add("location")
    App(IOSPermissionHandler(), listOfPermission)
}
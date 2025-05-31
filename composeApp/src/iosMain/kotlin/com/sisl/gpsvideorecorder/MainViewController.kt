package com.sisl.gpsvideorecorder

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.coroutines.launch

fun MainViewController() = ComposeUIViewController {
    val permissionController = remember { MultiPermissionController(null) }
    var permissionsGranted by remember { mutableStateOf<Boolean?>(null) }

    // Create a CoroutineScope tied to this composition
    val coroutineScope = rememberCoroutineScope()

    // Launch permission request coroutine once when composition enters
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val result = permissionController.requestPermissions(
                MultiPermissionController.Permission.STORAGE,
                MultiPermissionController.Permission.CAMERA,
                MultiPermissionController.Permission.LOCATION,
                MultiPermissionController.Permission.NOTIFICATION,
                MultiPermissionController.Permission.AUDIO,
            )
            permissionsGranted = result.values.all { it }
        }
    }

    when (permissionsGranted) {
        true -> println("Permissions granted!")
        false -> println("Permissions denied!")
        null -> println("Requesting permissions...")
    }
    App()
}
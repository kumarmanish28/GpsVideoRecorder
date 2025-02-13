package com.sisl.gpsvideorecorder

import androidx.compose.runtime.Composable
import com.sisl.gpsvideorecorder.platform.permission.PermissionHandler
import com.sisl.gpsvideorecorder.presentation.screen.HomeScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(
    permissionHandler: PermissionHandler,
    permission: List<String>,
    context: Any? = null
) {
    HomeScreen(context, permissionHandler, permission)
}


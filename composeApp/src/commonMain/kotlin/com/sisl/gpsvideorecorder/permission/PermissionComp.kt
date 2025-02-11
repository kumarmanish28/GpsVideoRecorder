package com.sisl.gpsvideorecorder.permission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun PermissionScreen(permissionHandler: PermissionHandler, permission: List<String>) {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = {
            permissionHandler.requestPermissions(
                permissions = permission
            ) { granted, denied ->

            }

        }) {
            Text("Request Camera Permission")
        }
    }
}
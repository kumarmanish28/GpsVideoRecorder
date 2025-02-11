package com.sisl.gpsvideorecorder.permission

interface PermissionHandler {
    fun requestPermissions(
        permissions: List<String>,
        onResult: (granted: Boolean, deniedPermissions: List<String>) -> Unit
    )

    fun checkPermission(permission: String): Boolean
}
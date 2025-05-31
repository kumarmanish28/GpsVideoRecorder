package com.sisl.gpsvideorecorder

expect class MultiPermissionController(context: Any? = null) {
    enum class Permission {
        LOCATION,
        CAMERA,
        AUDIO,
        STORAGE,
        NOTIFICATION
    }

    suspend fun requestPermissions(vararg permissions: Permission): Map<Permission, Boolean>

    fun hasAllPermissions(vararg permissions: Permission): Boolean
}
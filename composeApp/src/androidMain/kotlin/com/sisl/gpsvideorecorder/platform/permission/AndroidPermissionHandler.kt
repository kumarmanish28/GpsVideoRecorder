package com.sisl.gpsvideorecorder.platform.permission

import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.sisl.gpsvideorecorder.platform.permission.PermissionHandler

class AndroidPermissionHandler(
    private val activity: ComponentActivity
) : PermissionHandler {

    private val permissionLauncher =
        activity.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val deniedPermissions = result.filterValues { !it }.keys.toList()
            permissionResultCallback?.invoke(deniedPermissions.isEmpty(), deniedPermissions)
        }

    private var permissionResultCallback: ((Boolean, List<String>) -> Unit)? = null

    override fun requestPermissions(
        permissions: List<String>,
        onResult: (granted: Boolean, deniedPermissions: List<String>) -> Unit
    ) {
        val deniedPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }

        if (deniedPermissions.isEmpty()) {
            onResult(true, emptyList())
        } else {
            permissionResultCallback = onResult
            permissionLauncher.launch(deniedPermissions.toTypedArray())
        }
    }

    override fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            activity,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
}

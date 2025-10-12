package com.sisl.gpsvideorecorder

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Suppress("-Xexpect-actual-classes")
actual class MultiPermissionController  actual constructor(private val context: Any?) {
    private val activity
        get() = context as? Activity ?: throw IllegalStateException("Context must be an Activity")

    private companion object {
        const val PERMISSION_REQUEST_CODE = 1001
    }

    actual enum class Permission {
        LOCATION, CAMERA, AUDIO, STORAGE, NOTIFICATION
    }

    private val permissionMap = mapOf(
        Permission.LOCATION to listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ),
        Permission.CAMERA to listOf(Manifest.permission.CAMERA),
        Permission.AUDIO to listOf(Manifest.permission.RECORD_AUDIO),
        Permission.STORAGE to if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            listOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        } else emptyList(),
        Permission.NOTIFICATION to if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(Manifest.permission.POST_NOTIFICATIONS)
        } else emptyList()
    )

    private var permissionContinuation: (Map<Permission, Boolean>) -> Unit = {}

    private val permissionLauncher = (activity as ComponentActivity).registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val mappedResults = results.mapNotNull { (androidPerm, granted) ->
            permissionMap.entries.find { it.value.contains(androidPerm) }?.key?.let { it to granted }
        }.toMap()
        permissionContinuation(mappedResults)
    }


    actual suspend fun requestPermissions(vararg permissions: Permission): Map<Permission, Boolean> {
        val androidPermissions = permissions.flatMap { permission ->
            permissionMap[permission] ?: emptyList()
        }.toTypedArray()

        if (androidPermissions.isEmpty()) {
            return permissions.associateWith { true }
        }

        return suspendCancellableCoroutine { continuation ->
            permissionContinuation = { results ->
                continuation.resume(results)
            }

            try {
                permissionLauncher.launch(androidPermissions)
            } catch (e: Exception) {
                continuation.resumeWithException(e)
            }
        }
    }


    actual fun hasAllPermissions(vararg permissions: Permission): Boolean {
        return permissions.all { permission ->
            permissionMap[permission]?.all { androidPerm ->
                ContextCompat.checkSelfPermission(
                    context as Context,
                    androidPerm
                ) == PackageManager.PERMISSION_GRANTED
            } ?: true
        }
    }

}
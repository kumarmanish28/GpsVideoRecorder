package com.sisl.gpsvideorecorder

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

actual class MultiPermissionController actual constructor(context: Any?) {

    actual enum class Permission {
        LOCATION, CAMERA, AUDIO, STORAGE, NOTIFICATION
    }

    private val locationManager = CLLocationManager()
    private val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()

    actual suspend fun requestPermissions(vararg permissions: Permission): Map<Permission, Boolean> {
        return coroutineScope {
            permissions.map { permission ->
                async { permission to requestPermission(permission) }
            }.awaitAll().toMap()
        }
    }

    private suspend fun requestPermission(permission: Permission): Boolean {
        return when (permission) {
            Permission.CAMERA -> requestCameraPermission()
            Permission.AUDIO -> requestMicrophonePermission()
            Permission.LOCATION -> requestLocationPermission()
            Permission.NOTIFICATION -> requestNotificationPermission()
            Permission.STORAGE -> true // iOS doesn't require permission for general storage
        }
    }

    private suspend fun requestCameraPermission(): Boolean {
        return suspendCoroutine { continuation ->
            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                continuation.resume(granted)
            }
        }
    }

    private suspend fun requestMicrophonePermission(): Boolean {
        return suspendCoroutine { continuation ->
            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                continuation.resume(granted)
            }
        }
    }

    private suspend fun requestLocationPermission(): Boolean {
        // NOTE: Proper permission handling for CLLocationManager needs delegation.
        // This is a naive implementation that assumes immediate access.
        locationManager.requestWhenInUseAuthorization()
        return suspendCoroutine { continuation ->
            // Fake delay/response – in real code, you'd observe delegate callbacks
            continuation.resume(
                CLLocationManager.authorizationStatus() ==
                        kCLAuthorizationStatusAuthorizedWhenInUse ||
                        CLLocationManager.authorizationStatus() ==
                        kCLAuthorizationStatusAuthorizedAlways
            )
        }
    }

    private suspend fun requestNotificationPermission(): Boolean {
        return suspendCoroutine { continuation ->
            notificationCenter.requestAuthorizationWithOptions(
                options = UNAuthorizationOptionAlert or
                        UNAuthorizationOptionSound or
                        UNAuthorizationOptionBadge
            ) { granted, _ ->
                continuation.resume(granted)
            }
        }
    }

    private fun hasPermission(permission: Permission): Boolean {
        return when (permission) {
            Permission.CAMERA ->
                AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) ==
                        AVAuthorizationStatusAuthorized

            Permission.AUDIO ->
                AVAudioSession.sharedInstance().recordPermission ==
                        AVAudioSessionRecordPermissionGranted

            Permission.LOCATION ->
                CLLocationManager.authorizationStatus() ==
                        kCLAuthorizationStatusAuthorizedWhenInUse ||
                        CLLocationManager.authorizationStatus() ==
                        kCLAuthorizationStatusAuthorizedAlways

            Permission.NOTIFICATION -> {
                // Cannot synchronously get notification permission in KMP iOS
                // Needs suspend/callback (handled above)
                false
            }

            Permission.STORAGE -> true
        }
    }

    actual fun hasAllPermissions(vararg permissions: Permission): Boolean {
        return permissions.all { hasPermission(it) }
    }

}

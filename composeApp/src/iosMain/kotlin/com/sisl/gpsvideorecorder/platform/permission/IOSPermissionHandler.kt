package com.sisl.gpsvideorecorder.platform.permission

import com.sisl.gpsvideorecorder.platform.permission.PermissionHandler
import platform.AVFAudio.*
import platform.AVFoundation.*
import platform.CoreLocation.*
import platform.darwin.NSObject

class IOSPermissionHandler : PermissionHandler {

    private val locationManager = CLLocationManager()
    private val locationDelegate = LocationDelegate()

    override fun requestPermissions(
        permissions: List<String>,
        onResult: (granted: Boolean, deniedPermissions: List<String>) -> Unit
    ) {
        val deniedPermissions = mutableListOf<String>()

        permissions.forEach { permission ->
            when (permission) {
                "location" -> {
                    locationManager.delegate = locationDelegate
                    locationManager.requestWhenInUseAuthorization()
                }

                "camera" -> {
                    when (AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)) {
                        AVAuthorizationStatusAuthorized -> {}
                        AVAuthorizationStatusNotDetermined -> {
                            AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                                if (!granted) deniedPermissions.add(permission)
                                onResult(granted, deniedPermissions)
                            }
                        }
                        else -> deniedPermissions.add(permission)
                    }
                }

                "microphone" -> {
                    when (AVAudioSession.sharedInstance().recordPermission) {
                        AVAudioSessionRecordPermissionGranted -> {}
                        AVAudioSessionRecordPermissionUndetermined -> {
                            AVAudioSession.sharedInstance().requestRecordPermission { granted ->
                                if (!granted) deniedPermissions.add(permission)
                                onResult(granted, deniedPermissions)
                            }
                        }
                        else -> deniedPermissions.add(permission)
                    }
                }
            }
        }

        if (deniedPermissions.isNotEmpty()) {
            onResult(false, deniedPermissions)
        }
    }

    override fun checkPermission(permission: String): Boolean {
        return when (permission) {
            "location" -> CLLocationManager.authorizationStatus() == kCLAuthorizationStatusAuthorizedWhenInUse ||
                    CLLocationManager.authorizationStatus() == kCLAuthorizationStatusAuthorizedAlways
            "camera" -> AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo) == AVAuthorizationStatusAuthorized
            "microphone" -> AVAudioSession.sharedInstance().recordPermission == AVAudioSessionRecordPermissionGranted
            else -> false
        }
    }
}


class LocationDelegate : NSObject(), CLLocationManagerDelegateProtocol

package com.sisl.gpsvideorecorder

import platform.UIKit.UIDevice

class IOSPlatform: Platform {
//    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
    override val name: String = "iOS"
}

actual fun getPlatform(): Platform = IOSPlatform()
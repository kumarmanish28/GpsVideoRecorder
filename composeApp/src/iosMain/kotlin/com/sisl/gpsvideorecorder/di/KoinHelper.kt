package com.sisl.gpsvideorecorder.di

import org.koin.core.context.startKoin
import kotlin.experimental.ExperimentalObjCName

fun initKoin() {
    startKoin {
        modules(
            locationModule,  // Your common/shared module
            iosModule      // iOS-specific module
        )
    }
}

// Helper object for Swift interop
class KoinHelper {
        @OptIn(ExperimentalObjCName::class)
        @ObjCName("doInitKoin")
        fun doInitKoin() {
            initKoin()
        }
}
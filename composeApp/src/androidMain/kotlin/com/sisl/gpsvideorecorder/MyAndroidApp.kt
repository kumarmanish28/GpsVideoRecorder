package com.sisl.gpsvideorecorder

import android.app.Application
import com.sisl.gpsvideorecorder.di.initKoin
import org.koin.android.ext.koin.androidContext

class MyAndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@MyAndroidApp)
        }
    }
}
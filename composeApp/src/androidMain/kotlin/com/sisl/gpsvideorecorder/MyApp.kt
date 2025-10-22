package com.sisl.gpsvideorecorder

import android.app.Application
import com.sisl.gpsvideorecorder.di.AndroidContextHolder
import com.sisl.gpsvideorecorder.di.androidModule
import com.sisl.gpsvideorecorder.di.dataStoreModule
import com.sisl.gpsvideorecorder.di.locationModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(this)
        startKoin {
            androidLogger()
            androidContext(this@MyApp)
            modules(
                androidModule,
                dataStoreModule,
                locationModule
            )
        }
    }
}
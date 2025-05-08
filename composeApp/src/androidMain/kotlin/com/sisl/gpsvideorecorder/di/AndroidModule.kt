package com.sisl.gpsvideorecorder.di

import android.content.Context
import com.sisl.gpsvideorecorder.data.datasources.AndroidLocationDataSource
import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import org.koin.dsl.module

val androidModule = module {
    single<LocationDataSource> { AndroidLocationDataSource() }
    single { provideContext() }
}

fun provideContext(): Context {
    return AndroidContextHolder.applicationContext
}
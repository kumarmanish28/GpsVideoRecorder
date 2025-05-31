package com.sisl.gpsvideorecorder.di

import android.content.Context
import com.sisl.gpsvideorecorder.data.datasources.AndroidLocationDataSource
import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.local.database.getGpsVideoRecorderDb
import org.koin.dsl.module

val androidModule = module {
    single<LocationDataSource> { AndroidLocationDataSource() }
    single { provideContext() }
    single<GpsRecorderDb> { getGpsVideoRecorderDb(get()) }
   /* // Provide DAO from Room database
    single<LocationDao> { get<GpsRecorderDb>().locationDao() }*/
}

fun provideContext(): Context {
    return AndroidContextHolder.applicationContext
}
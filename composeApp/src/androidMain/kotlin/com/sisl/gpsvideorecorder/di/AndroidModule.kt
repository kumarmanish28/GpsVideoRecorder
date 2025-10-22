package com.sisl.gpsvideorecorder.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sisl.gpsvideorecorder.data.PrefDataStoreManager
import com.sisl.gpsvideorecorder.data.datasources.AndroidLocationDataSource
import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.datasources.createDataStore
import com.sisl.gpsvideorecorder.data.datasources.createDataStoreWithContext
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.local.database.getGpsVideoRecorderDb
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val androidModule = module {
    single { provideContext() }
    single<LocationDataSource> { AndroidLocationDataSource() }
    single<GpsRecorderDb> { getGpsVideoRecorderDb(get()) }
    /* // Provide DAO from Room database
     single<LocationDao> { get<GpsRecorderDb>().locationDao() }*/

//    // Provide DataStore instance
    single<DataStore<Preferences>> {
        createDataStoreWithContext(get())
    }
    // PrefDataStoreManager
    single {
        PrefDataStoreManager(get())
    }
}


fun provideContext(): Context {
    return AndroidContextHolder.applicationContext
}
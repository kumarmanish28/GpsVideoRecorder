package com.sisl.gpsvideorecorder.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.sisl.gpsvideorecorder.data.PrefDataStoreManager
import com.sisl.gpsvideorecorder.data.datasources.createDataStore
import org.koin.dsl.module

val dataStoreModule = module {
    // Provide PrefDataStoreManager
    single {
        PrefDataStoreManager(get())
    }
}
package com.sisl.gpsvideorecorder.di

import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.sisl.gpsvideorecorder.data.local.DatabaseFactory
import com.sisl.gpsvideorecorder.data.local.GpsVideoRecorderDb
import com.sisl.gpsvideorecorder.data.network.HttpClientFactory
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module
val sharedModule = module {
    single {
        get<DatabaseFactory>().createDatabase()
            .setDriver(BundledSQLiteDriver())
            .build()
    }
    single { get<GpsVideoRecorderDb>().locationDao }
    single { HttpClientFactory.create(get()) }
}
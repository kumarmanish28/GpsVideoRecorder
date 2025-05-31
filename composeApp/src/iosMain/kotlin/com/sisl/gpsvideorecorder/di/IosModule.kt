package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.datasources.IosLocationDataSource
import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.local.database.getGpsVideoRecorderDb
import org.koin.dsl.module

val iosModule = module {
    single<LocationDataSource> { IosLocationDataSource() }
    single<GpsRecorderDb> { getGpsVideoRecorderDb() }
}
package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.datasources.IosLocationDataSource
import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import org.koin.dsl.module

val iosModule = module {
    single<LocationDataSource> { IosLocationDataSource() }
}
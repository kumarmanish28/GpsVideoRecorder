package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.remote.utils.ApiService
import com.sisl.gpsvideorecorder.data.remote.utils.ApiServiceImpl
import com.sisl.gpsvideorecorder.data.remote.utils.createHttpClient
import com.sisl.gpsvideorecorder.data.repositories.LocationRepositoryImpl
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import com.sisl.gpsvideorecorder.presentation.viewmodels.VideoHistoryScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    single { createHttpClient() }
    single<LocationDao> { get<GpsRecorderDb>().locationDao() }
    single { ApiServiceImpl(get()) }.bind(ApiService::class)
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    viewModel { GpsVideoRecorderViewModel(get()) }
    viewModel { VideoHistoryScreenViewModel(get()) }
}
package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.PrefDataStoreManager
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.remote.utils.ApiService
import com.sisl.gpsvideorecorder.data.remote.utils.ApiServiceImpl
import com.sisl.gpsvideorecorder.data.remote.utils.createBinaryHttpClient
import com.sisl.gpsvideorecorder.data.remote.utils.createHttpClient
import com.sisl.gpsvideorecorder.data.repositories.LocationRepositoryImpl
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import com.sisl.gpsvideorecorder.presentation.viewmodels.LoginScreenViewModel
import com.sisl.gpsvideorecorder.presentation.viewmodels.VideoHistoryScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    single { createHttpClient() }
    single { createBinaryHttpClient() }
    single<LocationDao> { get<GpsRecorderDb>().locationDao() }
    single { ApiServiceImpl(get(), get()) }.bind(ApiService::class)
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single {
        PrefDataStoreManager(get())
    }
    viewModel { GpsVideoRecorderViewModel(get(), get(), get()) }
    viewModel { VideoHistoryScreenViewModel(get()) }
    viewModel { LoginScreenViewModel(get(), get()) }
}
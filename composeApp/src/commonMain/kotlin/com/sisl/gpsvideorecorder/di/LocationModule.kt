package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.remote.createHttpClient
import com.sisl.gpsvideorecorder.data.repositories.LocationRepositoryImpl
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.components.recorder.VideoRecorder
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import com.sisl.gpsvideorecorder.presentation.viewmodels.VideoHistoryScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val locationModule = module {
    single { createHttpClient() }
    single<LocationDao> { get<GpsRecorderDb>().locationDao() }
    single<LocationRepository> { LocationRepositoryImpl(get()) }
    viewModel { GpsVideoRecorderViewModel(get()) }
    viewModel { VideoHistoryScreenViewModel(get()) }
}
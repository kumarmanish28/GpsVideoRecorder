package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.PrefDataStoreManager
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.database.GpsRecorderDb
import com.sisl.gpsvideorecorder.data.remote.api.VideoUploadApiService
import com.sisl.gpsvideorecorder.data.remote.api.VideoUploadApiServiceImpl
import com.sisl.gpsvideorecorder.data.remote.utils.ApiService
import com.sisl.gpsvideorecorder.data.remote.utils.ApiServiceImpl
import com.sisl.gpsvideorecorder.data.remote.utils.createBinaryHttpClient
import com.sisl.gpsvideorecorder.data.remote.utils.createHttpClient
import com.sisl.gpsvideorecorder.data.remote.utils.createUploadHttpClient
import com.sisl.gpsvideorecorder.data.repositories.LocationRepositoryImpl
import com.sisl.gpsvideorecorder.data.repositories.VideoUploadRepositoryImpl
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.domain.repositories.VideoUploadRepository
import com.sisl.gpsvideorecorder.presentation.viewmodels.GpsVideoRecorderViewModel
import com.sisl.gpsvideorecorder.presentation.viewmodels.LoginScreenViewModel
import com.sisl.gpsvideorecorder.presentation.viewmodels.VideoHistoryScreenViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val locationModule = module {
    single(named("jsonClient")) { createHttpClient() }
    single(named("binaryClient")) { createBinaryHttpClient() }
    single(named("jsonUploadClient")) { createUploadHttpClient() }
    single {
        ApiServiceImpl(
            get(named("jsonClient")),
            get(named("binaryClient"))
        )
    }.bind(ApiService::class)
    single<LocationDao> { get<GpsRecorderDb>().locationDao() }
    single<LocationRepository> { LocationRepositoryImpl(get(), get()) }
    single {
        PrefDataStoreManager(get())
    }
    single<VideoUploadApiService> { VideoUploadApiServiceImpl(get(named("jsonUploadClient"))) }
    single<VideoUploadRepository> { VideoUploadRepositoryImpl(get(), get()) }

    viewModel { GpsVideoRecorderViewModel(get(), get(), get()) }
    viewModel { VideoHistoryScreenViewModel(get(), get()) }
    viewModel { LoginScreenViewModel(get(), get()) }
}
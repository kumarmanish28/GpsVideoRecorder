package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.local.DatabaseFactory
import com.sisl.gpsvideorecorder.data.network.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory(androidApplication()) }
        single<HttpClientEngine>{OkHttp.create()}
    }
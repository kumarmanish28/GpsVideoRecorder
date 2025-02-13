package com.sisl.gpsvideorecorder.di

import com.sisl.gpsvideorecorder.data.local.DatabaseFactory
import com.sisl.gpsvideorecorder.data.network.HttpClientFactory
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory() }
        single<HttpClientEngine>{ Darwin.create() }
    }
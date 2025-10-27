package com.sisl.gpsvideorecorder.data.remote.utils

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.ALL
        }

        install(HttpTimeout) {
            requestTimeoutMillis = 30000L
            connectTimeoutMillis = 30000L
            socketTimeoutMillis = 30000L
        }

        expectSuccess = true
    }
}

fun createBinaryHttpClient(): HttpClient {
    return HttpClient {
        install(Logging) {
            level = LogLevel.INFO
        }

        install(HttpTimeout) {
            // Disable all timeouts (set to infinite)
            requestTimeoutMillis = Long.MAX_VALUE
            connectTimeoutMillis = Long.MAX_VALUE
            socketTimeoutMillis = Long.MAX_VALUE
        }

        expectSuccess = false // Don't throw on 400/500
    }
}

fun createUploadHttpClient(): HttpClient {
    return HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                prettyPrint = true
                isLenient = true
            })
        }

        // CRITICAL: NO LOGGING for uploads to prevent OOM
        // install(Logging) {
        //     logger = Logger.DEFAULT
        //     level = LogLevel.ALL
        // }

        // Longer timeouts for large files
        install(HttpTimeout) {
            requestTimeoutMillis = 30 * 60 * 1000L // 30 minutes for large files
            connectTimeoutMillis = 30 * 60 * 1000L
            socketTimeoutMillis = 30 * 60 * 1000L
        }

        expectSuccess = false
    }
}


package com.sisl.gpsvideorecorder.di

import android.app.Application
import android.content.Context

object AndroidContextHolder {
    lateinit var applicationContext: Context
        private set

    fun init(application: Application) {
        applicationContext = application.applicationContext
    }
}
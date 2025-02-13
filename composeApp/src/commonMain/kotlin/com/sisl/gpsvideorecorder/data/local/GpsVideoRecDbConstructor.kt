package com.sisl.gpsvideorecorder.data.local

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object GpsVideoRecDbConstructor : RoomDatabaseConstructor<GpsVideoRecorderDb> {
    override fun initialize(): GpsVideoRecorderDb
}
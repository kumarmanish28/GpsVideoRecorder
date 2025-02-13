package com.sisl.gpsvideorecorder.data.local

import androidx.room.RoomDatabase

expect class DatabaseFactory {
    fun createDatabase(): RoomDatabase.Builder<GpsVideoRecorderDb>
}
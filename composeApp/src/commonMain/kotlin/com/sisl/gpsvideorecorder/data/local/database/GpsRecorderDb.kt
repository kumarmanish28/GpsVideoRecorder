package com.sisl.gpsvideorecorder.data.local.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class GpsRecorderDb : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<GpsRecorderDb> {
    override fun initialize(): GpsRecorderDb
}
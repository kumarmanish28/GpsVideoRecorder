package com.sisl.gpsvideorecorder.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sisl.gpsvideorecorder.data.model.LocationEntity

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class GpsVideoRecorderDb : RoomDatabase(){
    companion object {
        const val DATABASE_NAME = "gps_video_recorder_db.db"
    }
    abstract val locationDao: LocationDao
}
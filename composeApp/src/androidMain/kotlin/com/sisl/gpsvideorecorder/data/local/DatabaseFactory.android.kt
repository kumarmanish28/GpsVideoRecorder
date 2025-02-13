package com.sisl.gpsvideorecorder.data.local

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(private val mContext: Context) {
    actual fun createDatabase(): RoomDatabase.Builder<GpsVideoRecorderDb> {
        val appContext = mContext.applicationContext
        val dbFile = appContext.getDatabasePath(GpsVideoRecorderDb.DATABASE_NAME)
        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
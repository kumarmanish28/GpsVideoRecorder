package com.sisl.gpsvideorecorder.data.local.database

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

fun getGpsVideoRecorderDb(context: Context): GpsRecorderDb {
    val dbFile = context.getDatabasePath("gps_recorder_db.db")
    return Room.databaseBuilder<GpsRecorderDb>(
        context = context,
        name = dbFile.absolutePath
    )
        .setDriver(BundledSQLiteDriver())
        .build()
}
package com.sisl.gpsvideorecorder.data.local.database

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

fun getGpsVideoRecorderDb(): GpsRecorderDb {

    val dbFile = documentDirectory() + "/gps_recorder_db.db"

    return Room.databaseBuilder<GpsRecorderDb>(
        name = dbFile
    )
        .setDriver(BundledSQLiteDriver())
        .build()

}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(documentDirectory?.path)
}
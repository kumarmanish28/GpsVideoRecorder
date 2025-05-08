package com.sisl.gpsvideorecorder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.Clock

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    var isUploaded: Int,
    var speed: Int = 0,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val videoId: Long = 0
)

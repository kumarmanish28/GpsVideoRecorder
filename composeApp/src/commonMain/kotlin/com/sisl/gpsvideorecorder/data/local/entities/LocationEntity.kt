package com.sisl.gpsvideorecorder.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sisl.gpsvideorecorder.domain.models.LocationData
import kotlinx.datetime.Clock

@Entity(tableName = "locations")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    var isUploaded: Int,
    var isDeleted: Int,
    var speed: Int = 0,
    val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
    val videoId: Long = 0,
    val videoName: String = "",
    val videoPath: String = ""
)

fun LocationData.toEntity() = LocationEntity(
    videoId = videoId,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    speed = speed,
    time = time,
    isUploaded = isUploaded,
    isDeleted = isUploaded
)

fun LocationEntity.toDomain() = LocationData(
    videoId = videoId,
    latitude = latitude,
    longitude = longitude,
    timestamp = timestamp,
    speed = speed,
    time = time,
    isUploaded = isUploaded,
    isDeleted = isDeleted
)
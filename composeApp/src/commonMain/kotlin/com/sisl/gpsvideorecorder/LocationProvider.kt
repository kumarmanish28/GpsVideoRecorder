package com.sisl.gpsvideorecorder

interface LocationProvider {
    suspend fun getLocation(): LocationData?
}

data class LocationData(
    val latitude: Double,
    val longitude: Double,
    val speed: Int,
    val time: Long,
    val isUploaded: Int?=0,
    val videoId: Int?=1
)


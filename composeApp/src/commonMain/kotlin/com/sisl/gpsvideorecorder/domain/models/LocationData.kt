package com.sisl.gpsvideorecorder.domain.models

data class LocationData(
    val time: String,
    val latitude: Double,
    val longitude: Double,
    var isUploaded: Int,
    var speed: Int = 0,
    val timestamp: Long,
    val videoId: Long = 0
)
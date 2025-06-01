package com.sisl.gpsvideorecorder.domain.models

data class LocationData(
    val time: String,
    val latitude: Double,
    val longitude: Double,
    var isUploaded: Int,
    var isDeleted: Int,
    var speed: Int = 0,
    val timestamp: Long,
    var videoId: Long = 0
)
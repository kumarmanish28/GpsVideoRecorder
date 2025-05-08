package com.sisl.gpsvideorecorder.data.local.models

data class LocationEntityWithVideoDto(
    val id: Int = 0,
    val time: String,
    val latitude: Double,
    val longitude: Double,
    var isUploaded: Int,
    var speed: Int = 0,
    val timestamp: Long,
    val videoId: Long = 0
)

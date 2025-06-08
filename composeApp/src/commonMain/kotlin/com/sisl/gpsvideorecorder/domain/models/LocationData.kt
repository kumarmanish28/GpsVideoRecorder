package com.sisl.gpsvideorecorder.domain.models

import kotlinx.serialization.Serializable


@Serializable
data class LocationData(
    val time: String,
    val latitude: Double,
    val longitude: Double,
    var isUploaded: Int,
    var isDeleted: Int,
    var speed: Int = 0,
    val timestamp: Long,
    var videoId: Long = 0,
    val videoName: String = "",
    val videoPath: String = ""
)
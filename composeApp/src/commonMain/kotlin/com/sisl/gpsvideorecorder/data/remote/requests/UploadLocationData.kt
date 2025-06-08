package com.sisl.gpsvideorecorder.data.remote.requests

import com.sisl.gpsvideorecorder.domain.models.LocationData
import kotlinx.serialization.Serializable

@Serializable
data class RequestLocationData(
    val time: String, // Format "00:01"
    val latitude: Double,
    val longitude: Double,
    val speed: Int = 0
)

@Serializable
data class RequestVideoLocationData(
    val videoname: String,
    val locations: List<RequestLocationData>
)
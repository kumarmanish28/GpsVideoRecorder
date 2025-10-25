package com.sisl.gpsvideorecorder.data.remote.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestLocationData(
    @SerialName("time")
    val time: String, // Format "00:01"
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double,
    @SerialName("speed")
    val speed: Int? = 0
)

@Serializable
data class RequestVideoLocationData(
    @SerialName("videoname")
    val videoname: String?="",
    @SerialName("locations")
    val locations: List<RequestLocationData>?=emptyList()
)
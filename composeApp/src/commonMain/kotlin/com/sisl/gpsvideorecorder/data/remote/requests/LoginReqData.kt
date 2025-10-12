package com.sisl.gpsvideorecorder.data.remote.requests

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginReqData(
    @SerialName("username")
    val userId: String,
    @SerialName("password")
    val password: String
)

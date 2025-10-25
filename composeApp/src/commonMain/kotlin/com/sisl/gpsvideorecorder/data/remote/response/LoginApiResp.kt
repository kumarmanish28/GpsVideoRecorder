package com.sisl.gpsvideorecorder.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginApiResp(
    @SerialName("user")
    val user: String?=null,
    @SerialName("version")
    val version: String?=null,
)
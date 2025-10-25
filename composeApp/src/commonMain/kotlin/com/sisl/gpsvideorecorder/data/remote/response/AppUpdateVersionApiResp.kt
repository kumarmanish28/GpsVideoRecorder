package com.sisl.gpsvideorecorder.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AppUpdateVersionApiResp(
    @SerialName("version")
    val version: String? = null,
)
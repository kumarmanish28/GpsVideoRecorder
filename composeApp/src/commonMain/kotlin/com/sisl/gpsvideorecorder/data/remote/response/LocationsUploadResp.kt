package com.sisl.gpsvideorecorder.data.remote.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class LocationsUploadResp(
    @SerialName("locations_count")
    val locationsCount: Int,
    val message: String,
    @SerialName("stored_file")
    val storedFile: String,
    val videoname: String
)


@Serializable
data class UploadVideoResp(
    @SerialName("filename")
    val fileName: String? = null,
    val message: String? = null,
    @SerialName("saved_path")
    val savedPath: String? = null,
)
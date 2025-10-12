package com.sisl.gpsvideorecorder.domain.models

data class LoginResponse(
    val user: String,
    val code: Int,
    val message: String
)

package com.sisl.gpsvideorecorder.data.remote.response

sealed class ApiResponse<out T> {
    data object Loading : ApiResponse<Nothing>()
    data class Success<T>(val data: T) : ApiResponse<T>()
    data class Error(val message: String, val code: Int? = null) : ApiResponse<Nothing>()
}
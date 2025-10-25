package com.sisl.gpsvideorecorder.data.remote.api

import com.sisl.gpsvideorecorder.presentation.state.VideoUploadResponse

// commonMain
interface VideoUploadApiService {
    suspend fun uploadVideo(
        videoBytes: ByteArray,
        fileName: String,
        onProgress: (percentage: Float) -> Unit
    ): VideoUploadResponse
}
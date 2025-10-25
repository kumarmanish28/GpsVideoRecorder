package com.sisl.gpsvideorecorder.domain.repositories

import com.sisl.gpsvideorecorder.presentation.state.UploadVideoState
import kotlinx.coroutines.flow.Flow

interface VideoUploadRepository {
    suspend fun uploadVideo(videoPath: String): Flow<UploadVideoState>
    suspend fun cancelUpload()
}
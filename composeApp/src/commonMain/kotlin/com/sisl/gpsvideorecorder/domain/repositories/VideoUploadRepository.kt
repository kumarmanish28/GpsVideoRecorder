package com.sisl.gpsvideorecorder.domain.repositories

import com.sisl.gpsvideorecorder.presentation.state.UploadVideoState
import kotlinx.coroutines.flow.Flow

interface VideoUploadRepository {
    suspend fun uploadVideo(videoId: Long): Flow<UploadVideoState>
    suspend fun cancelUpload()
}
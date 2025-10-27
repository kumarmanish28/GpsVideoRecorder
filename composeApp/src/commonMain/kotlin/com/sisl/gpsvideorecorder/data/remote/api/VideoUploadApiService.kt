package com.sisl.gpsvideorecorder.data.remote.api

import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CancelUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.ChunkUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.CompleteUploadResponse
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadRequest
import com.sisl.gpsvideorecorder.data.remote.response.InitiateUploadResponse
import com.sisl.gpsvideorecorder.presentation.state.VideoUploadResponse

// commonMain
//interface VideoUploadApiService {
//    suspend fun uploadVideo(
//        videoBytes: ByteArray,
//        fileName: String,
//        onProgress: (percentage: Float) -> Unit
//    ): VideoUploadResponse
//}

//interface VideoUploadApiService {
//    suspend fun uploadVideo(
//        videoPath: String, // Change from ByteArray to file path
//        fileName: String,
//        onProgress: (percentage: Float) -> Unit
//    ): VideoUploadResponse
//}

interface VideoUploadApiService {
    suspend fun initiateUpload(request: InitiateUploadRequest): InitiateUploadResponse
    suspend fun uploadChunk(
        uploadId: String,
        chunkNumber: Int,
        chunkData: ByteArray,
        onProgress: (Float) -> Unit
    ): ChunkUploadResponse
    suspend fun completeUpload(request: CompleteUploadRequest): CompleteUploadResponse
    suspend fun cancelUpload(request: CancelUploadRequest): CancelUploadResponse
}
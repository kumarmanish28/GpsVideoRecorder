package com.sisl.gpsvideorecorder.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class InitiateUploadRequest(
    val filename: String,
    val total_size: Long
)

@Serializable
data class InitiateUploadResponse(
    val upload_id: String,
    val chunk_size: Int
)

@Serializable
data class ChunkUploadResponse(
    val message: String,
    val chunk_number: Int,
    val uploaded: Long,
    val total: Long,
    val progress: Double
)

@Serializable
data class CompleteUploadRequest(
    val upload_id: String
)

@Serializable
data class CompleteUploadResponse(
    val message: String,
    val filename: String,
    val saved_path: String,
    val file_size: Long
)

@Serializable
data class CancelUploadRequest(
    val upload_id: String
)

@Serializable
data class CancelUploadResponse(
    val message: String
)

@Serializable
data class ErrorResponse(
    val error: String
)
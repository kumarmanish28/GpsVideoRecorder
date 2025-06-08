package com.sisl.gpsvideorecorder.presentation.state

data class VideoHistoryUiState(
    val videoItemsList: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

data class VideoItem(
    val videoId: Long,
    val dateTime: String,
    val coordinateCount: Int,
    val isUploaded: Boolean = false,
    val isDeleted: Boolean = false
)


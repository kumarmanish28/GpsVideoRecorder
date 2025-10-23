package com.sisl.gpsvideorecorder.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.state.DownloadState
import com.sisl.gpsvideorecorder.presentation.state.VideoHistoryUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VideoHistoryScreenViewModel(private val repository: LocationRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(VideoHistoryUiState())
    val uiState: StateFlow<VideoHistoryUiState> = _uiState.asStateFlow()

    init {
        loadVideoHistoryData()
    }

    private fun loadVideoHistoryData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            try {
                val result = repository.getRecordForVideoHistory()

                _uiState.update { it.copy(videoItemsList = result, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onUploadClicked(videoId: Long) {
        viewModelScope.launch {
            try {
                repository.uploadLocation(videoId).collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    videoItemsList = currentState.videoItemsList.map { videoItem ->
                                        if (videoItem.videoId == videoId) videoItem.copy(isUploaded = false) else videoItem
                                    }, errorMessage = null,
                                    successMessage = "Video Data uploaded successfully"
                                )
                            }
                        }

                        is ApiResponse.Error -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    videoItemsList = currentState.videoItemsList.map { videoItem ->
                                        if (videoItem.videoId == videoId) {
                                            videoItem.copy(isUploaded = false) // Revert the optimistic update
                                        } else {
                                            videoItem
                                        }
                                    },
                                    errorMessage = response.message,
                                    successMessage = null
                                )
                            }
                        }

                        is ApiResponse.Loading -> {
                            _uiState.update { currentState ->
                                currentState.copy(
                                    videoItemsList = currentState.videoItemsList.map { videoItem ->
                                        if (videoItem.videoId == videoId) {
                                            videoItem.copy(isUploaded = true)
                                        } else {
                                            videoItem
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            } catch (ex: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(
                        videoItemsList = currentState.videoItemsList.map { videoItem ->
                            if (videoItem.videoId == videoId) {
                                videoItem.copy(isUploaded = false)
                            } else {
                                videoItem
                            }
                        },
                        errorMessage = ex.message ?: "An unexpected error occurred"
                    )
                }
            }
        }
    }

    fun onDeleteClicked(videoId: Long) {
        viewModelScope.launch {
            repository.deleteLocation(videoId).collect { response ->
                when (response) {
                    is ApiResponse.Loading -> {}

                    is ApiResponse.Success -> {
                        _uiState.update { currentState ->
                            currentState.copy(videoItemsList = currentState.videoItemsList.map { videoItem ->
                                if (videoItem.videoId == videoId) videoItem.copy(isDeleted = true) else videoItem
                            })
                        }
                        loadVideoHistoryData()
                    }

                    is ApiResponse.Error -> {
                        _uiState.update { currentState ->
                            currentState.copy(
                                errorMessage = response.message ?: "An unexpected error occurred"
                            )
                        }
                    }
                }
            }
        }
    }

    fun onUploadVideoClicked(videoId: Long) {
//        viewModelScope.launch {
//            repository.uploadVideo(videoId).collect { response ->
//                when (response) {
//                    is ApiResponse.Loading -> {}
//
//                    is ApiResponse.Success -> {
//                        _uiState.update { currentState ->
//                            currentState.copy(videoItemsList = currentState.videoItemsList.map { videoItem ->
//                                if (videoItem.videoId == videoId) videoItem.copy(isDeleted = true) else videoItem
//                            })
//                        }
//                        loadVideoHistoryData()
//                    }
//
//                    is ApiResponse.Error -> {
//                        _uiState.update { currentState ->
//                            currentState.copy(
//                                errorMessage = response.message ?: "An unexpected error occurred"
//                            )
//                        }
//                    }
//                }
//            }
//        }
    }

    fun clearSuccessMessage() {
        _uiState.update { it.copy(successMessage = null) }
    }


}
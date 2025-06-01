package com.sisl.gpsvideorecorder.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
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

            delay(2000)

            _uiState.update { currentState ->
                currentState.copy(videoItemsList = currentState.videoItemsList.map { videoItem ->
                    if (videoItem.videoId == videoId) videoItem.copy(isUploaded = false) else videoItem
                }, errorMessage = null)
            }

        }
    }

    fun onDeleteClicked(videoId: Long) {
        viewModelScope.launch {
            _uiState.update { currentState ->
                currentState.copy(videoItemsList = currentState.videoItemsList.map { videoItem ->
                    if (videoItem.videoId == videoId) videoItem.copy(isDeleted = true) else videoItem
                })
            }

            delay(1500)
            _uiState.update { currentState ->
                currentState.copy(videoItemsList = currentState.videoItemsList.map { videoItem ->
                    if (videoItem.videoId == videoId) videoItem.copy(isDeleted = false) else videoItem
                }, errorMessage = null)
            }
        }
    }

}
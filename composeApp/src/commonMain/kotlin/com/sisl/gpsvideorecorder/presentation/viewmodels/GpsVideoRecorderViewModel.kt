package com.sisl.gpsvideorecorder.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.data.UploadingState
import com.sisl.gpsvideorecorder.data.local.entities.toEntity
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.data.remote.response.LocationsUploadResp
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.components.recorder.RecordingState
import com.sisl.gpsvideorecorder.presentation.components.recorder.VideoRecInfo
import com.sisl.gpsvideorecorder.presentation.components.recorder.VideoRecorder
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class GpsVideoRecorderViewModel(
    private val locationRepository: LocationRepository,
) : ViewModel() {

    private val _videoSavingProgress = MutableStateFlow(0f)
    val videoSavingProgress: StateFlow<Float> = _videoSavingProgress

    private val _isVideoSaving = MutableStateFlow(false)
    val isVideoSaving: StateFlow<Boolean> = _isVideoSaving
    private val _videoRecordingState = mutableStateOf(RecordingState.STOPPED)
    val videoRecordingState: State<RecordingState> = _videoRecordingState

    private val _locations = MutableStateFlow<List<LocationData>>(emptyList())
    val locations: StateFlow<List<LocationData>> = _locations

    private val _latestLocation = MutableStateFlow<LocationData?>(null)
    val latestLocation: StateFlow<LocationData?> = _latestLocation


    private val _uploadAllPendingCoordinates = mutableStateOf(UploadingState.NULL)
    val uploadAllPendingCoordinates = _uploadAllPendingCoordinates


    private var currentVideoId: Long? = null // To store the ID of the ongoing recording
    private var locationCollectionJob: Job? = null


    init {
        viewModelScope.launch {
            getLastLocation()
        }
    }

    fun startGspVideoRecording(videoRecorder: VideoRecorder) {
        viewModelScope.launch {
            try {
                _videoRecordingState.value = RecordingState.RECORDING
                videoRecorder.startRecording()
                locationRepository.startLocationTracking()

                currentVideoId = (latestLocation.value?.videoId ?: 0) + 1
                locationCollectionJob?.cancel()
                locationCollectionJob = launch {
                    locationRepository.locationUpdates.collect { location ->
                        location.videoId = currentVideoId ?: 1
                        if (_videoRecordingState.value == RecordingState.RECORDING) {
                            locationRepository.insertLocation(location.toEntity())
                        }
                    }
                }

            } catch (e: Exception) {
                // Handle error starting recording (e.g., database error)
                _videoRecordingState.value = RecordingState.STOPPED
                // Optionally show an error message to the user
                println("Error starting GPS video recording: ${e.message}")
            }
        }
    }

    //    fun stopGpsVideoRecording(videoRecorder: VideoRecorder) {
//        _videoRecordingState.value = RecordingState.STOPPED
//        videoRecorder.stopRecording()
//        locationRepository.stopLocationTracking()
//    }
    fun stopGpsVideoRecording(videoRecorder: VideoRecorder) {
        _videoRecordingState.value = RecordingState.STOPPED
        _isVideoSaving.value = true
        _videoSavingProgress.value = 0f
        videoRecorder.stopRecording()
        locationRepository.stopLocationTracking()
    }

    fun updateVideoSavingProgress(progress: Float) {
        _videoSavingProgress.value = progress

        // Auto-hide when progress reaches 100%
        if (progress >= 100f) {
            viewModelScope.launch {
                delay(1000) // Show 100% for 1 second
                _isVideoSaving.value = false
                _videoSavingProgress.value = 0f
            }
        }
    }


    fun getAllRecordedCoordinates() {
        viewModelScope.launch {
            locationRepository.getAllLocation()
                .catch { e ->
                    // Handle errors
                    print("LocationVM Error loading locations")
                }
                .collect { locations ->
                    _locations.value = locations
                }
        }
    }

    private fun getLastLocation() {
        viewModelScope.launch {
            locationRepository.getLastLocation()
                .catch { e ->
                    // Handle errors
                    print("LocationVM Error loading locations")
                }
                .collect { locations ->
                    _latestLocation.value = locations
                }
        }
    }

    fun onRecordingComplete(result: VideoRecInfo) {
        val videoId = currentVideoId
        val videoName = result.videoName
        val videoPath = result.videoUri
        viewModelScope.launch {
            if (videoId != null) {
                locationRepository.updateLocationWithVideoName(
                    videoId,
                    videoName ?: "",
                    videoPath ?: ""
                )
            }
        }

        locationCollectionJob?.cancel()
        currentVideoId = null
        print("Video Location: ${result.videoLocation}")
    }

    fun onUploadClicked() {
        viewModelScope.launch {
            try {
                locationRepository.uploadLocation(null).collect { response ->
                    when (response) {
                        is ApiResponse.Success -> {
                            _uploadAllPendingCoordinates.value = UploadingState.SUCCESS
                        }

                        is ApiResponse.Error -> {
                            _uploadAllPendingCoordinates.value = UploadingState.FAILED
                        }

                        is ApiResponse.Loading -> {
                            _uploadAllPendingCoordinates.value = UploadingState.LOADING
                        }
                    }
                }
            } catch (ex: Exception) {
                _uploadAllPendingCoordinates.value = UploadingState.FAILED
            }
        }
    }

}
package com.sisl.gpsvideorecorder.presentation.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.components.recorder.RecordingState
import com.sisl.gpsvideorecorder.presentation.components.recorder.VideoRecorder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class GpsVideoRecorderViewModel(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _videoRecordingState = mutableStateOf(RecordingState.STOPPED)
    val videoRecordingState: State<RecordingState> = _videoRecordingState

    private val _locations = mutableStateListOf<LocationData>()
    val locations: List<LocationData> = _locations

    private val _latestLocation = MutableStateFlow<LocationData?>(null)
    val latestLocation: StateFlow<LocationData?> = _latestLocation


    init {
        viewModelScope.launch {
            locationRepository.locationUpdates.collect { location ->
                _latestLocation.value = location
                if (_videoRecordingState.value == RecordingState.RECORDING) {
                    _locations.add(location)
                    locationRepository.insertLocation(location)
                }
            }
        }
    }

    fun startGspVideoRecording(videoRecorder: VideoRecorder) {
        _videoRecordingState.value = RecordingState.RECORDING
        videoRecorder.startRecording()
        locationRepository.startLocationTracking()
    }

    fun stopGpsVideoRecording(videoRecorder: VideoRecorder) {
        _videoRecordingState.value = RecordingState.STOPPED
        videoRecorder.stopRecording()
        locationRepository.stopLocationTracking()
//      saveGpsVideoRecordingWithLocation()
    }


}
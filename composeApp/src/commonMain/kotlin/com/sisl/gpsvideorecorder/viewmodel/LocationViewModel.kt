package com.sisl.gpsvideorecorder.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.LocationData
import com.sisl.gpsvideorecorder.LocationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationProvider: LocationProvider) : ViewModel() {
    private val _location = MutableStateFlow<LocationData?>(null)
    val location: StateFlow<LocationData?> = _location

    fun fetchLocation() {
        viewModelScope.launch {
            _location.value = locationProvider.getLocation()
        }
    }
}

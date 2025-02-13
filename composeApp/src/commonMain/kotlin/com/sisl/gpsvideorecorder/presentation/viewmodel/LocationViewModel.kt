package com.sisl.gpsvideorecorder.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sisl.gpsvideorecorder.data.model.LocationEntity
import com.sisl.gpsvideorecorder.platform.locationprovider.LocationProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LocationViewModel(private val locationProvider: LocationProvider) : ViewModel() {
    private val _location = MutableStateFlow<LocationEntity?>(null)
    val location: StateFlow<LocationEntity?> = _location

}

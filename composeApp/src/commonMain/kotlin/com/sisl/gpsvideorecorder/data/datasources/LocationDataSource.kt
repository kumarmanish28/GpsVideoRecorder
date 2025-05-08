package com.sisl.gpsvideorecorder.data.datasources

import com.sisl.gpsvideorecorder.domain.models.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    fun startLocationTracking()
    fun stopLocationTracking()
    val locationUpdates: Flow<LocationData>
}
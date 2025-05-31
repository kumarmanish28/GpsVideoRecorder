package com.sisl.gpsvideorecorder.domain.repositories

import com.sisl.gpsvideorecorder.domain.models.LocationData
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun startLocationTracking()
    fun stopLocationTracking()
    val locationUpdates: Flow<LocationData>


    // Add database operations
    suspend fun insertLocation(location: LocationData)
}
package com.sisl.gpsvideorecorder.domain.repositories

import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun startLocationTracking()
    fun stopLocationTracking()
    val locationUpdates: Flow<LocationData>


    // Add database operations
    suspend fun insertLocation(location: LocationEntity)
    suspend fun getAllLocation(): Flow<List<LocationData>>
    suspend fun getLastLocation(): Flow<LocationData?>
    suspend fun getRecordForVideoHistory(): List<VideoItem>

    // Networking Operation
//    suspend fun uploadLocation(videoId: Long): Result<Unit>
//    suspend fun deleteLocation(videoId: Long): Result<Unit>
}
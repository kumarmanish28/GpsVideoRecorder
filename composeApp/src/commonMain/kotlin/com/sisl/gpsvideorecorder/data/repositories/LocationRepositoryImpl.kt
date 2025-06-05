package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity
import com.sisl.gpsvideorecorder.data.local.entities.toDomain
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationRepositoryImpl(private val dao: LocationDao) : LocationRepository, KoinComponent {
    private val locationDataSource: LocationDataSource by inject()

    override fun startLocationTracking() {
        locationDataSource.startLocationTracking()
    }

    override fun stopLocationTracking() {
        locationDataSource.stopLocationTracking()
    }

    override val locationUpdates: Flow<LocationData>
        get() = locationDataSource.locationUpdates

    override suspend fun insertLocation(location: LocationEntity) {
        dao.insertLocation(location)
    }

    override suspend fun getAllLocation(): Flow<List<LocationData>> {
        return dao.getAllLocations().map { locationEntity ->
            locationEntity.map {
                it.toDomain()
            }
        }
    }

    override suspend fun getLastLocation(): Flow<LocationData?> {
        return dao.getLastLocation().map {
            it?.toDomain()
        }
    }

    override suspend fun getRecordForVideoHistory(): List<VideoItem> {
        return dao.getDateWisePendingLocationData()
    }

    override suspend fun updateLocationWithVideoName(
        videoId: Long,
        videoName: String,
        videoPath: String
    ) {
        dao.updateLocationWithVideoName(videoId, videoName, videoPath)
    }

//    // Networking Operations
//    override suspend fun uploadLocation(videoId: Long): Result<Unit> {
////        TODO("Not yet implemented")
//    }
//
//    override suspend fun deleteLocation(videoId: Long): Result<Unit> {
////        TODO("Not yet implemented")
//    }
}
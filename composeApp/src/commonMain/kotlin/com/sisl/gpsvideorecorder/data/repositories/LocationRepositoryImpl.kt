package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.entities.toEntity
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import kotlinx.coroutines.flow.Flow
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

    override suspend fun insertLocation(location: LocationData) {
        dao.insertLocation(location.toEntity())
    }
}
package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationRepositoryImpl : LocationRepository, KoinComponent {
    private val locationDataSource: LocationDataSource by inject()

    override fun startLocationTracking() {
        locationDataSource.startLocationTracking()
    }

    override fun stopLocationTracking() {
        locationDataSource.stopLocationTracking()
    }

    override val locationUpdates: Flow<LocationData>
        get() = locationDataSource.locationUpdates
}
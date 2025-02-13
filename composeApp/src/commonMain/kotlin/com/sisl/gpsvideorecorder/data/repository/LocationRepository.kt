package com.sisl.gpsvideorecorder.data.repository

import com.sisl.gpsvideorecorder.data.local.LocationDao
import com.sisl.gpsvideorecorder.data.model.LocationEntity
import com.sisl.gpsvideorecorder.platform.locationprovider.LocationProvider
import kotlinx.coroutines.flow.Flow

class LocationRepository(
    private val locationProvider: LocationProvider,
    private val locationDao: LocationDao
) {


}

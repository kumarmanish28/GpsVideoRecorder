package com.sisl.gpsvideorecorder.platform.locationprovider

import com.sisl.gpsvideorecorder.data.model.LocationEntity
import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocationFlow(): Flow<LocationEntity>
}


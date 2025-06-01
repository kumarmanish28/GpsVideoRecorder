package com.sisl.gpsvideorecorder.data.datasources

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.utils.common.DateFormatter
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AndroidLocationDataSource : LocationDataSource, KoinComponent {
    private val context: Context by inject()
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    private var lastLocation: Location? = null

    private val _locationUpdates = MutableSharedFlow<LocationData>(extraBufferCapacity = 10)

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startLocationTracking() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            1000
        ).build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun stopLocationTracking() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override val locationUpdates: SharedFlow<LocationData> = _locationUpdates

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            result.locations.lastOrNull()?.let { newLocation ->
                val previous = lastLocation
//                if (previous == null || previous.distanceTo(newLocation) >= 2f) {
                    lastLocation = newLocation
                    _locationUpdates.tryEmit(newLocation.toLocationData())
//                }
            }
        }
    }


    private fun Location.toLocationData() = LocationData(
        latitude = latitude,
        longitude = longitude,
        timestamp = time,
        time = DateFormatter.dateFormat(Clock.System.now().toEpochMilliseconds()),
        isUploaded = 0,
        isDeleted = 0
    )
}
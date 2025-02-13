package com.sisl.gpsvideorecorder.platform.locationprovider

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.sisl.gpsvideorecorder.data.model.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/*class AndroidLocationProvider(private val context: Context) : LocationProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override suspend fun getLocation(): LocationData? =
        suspendCancellableCoroutine { continuation ->

            // Check if GPS is enabled
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    continuation.resume(
                        LocationData(
                            location.latitude,
                            location.longitude,
                            location.speed.toInt(),
                            location.time,
                            0,
                            1
                        )
                    )
                    locationManager.removeUpdates(this) // Stop updates after first location
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                500L, // Minimum time interval in milliseconds
                0f,    // Minimum distance in meters
                locationListener
            )

            continuation.invokeOnCancellation {
                locationManager.removeUpdates(locationListener)
            }
        }
}*/

class AndroidLocationProvider(private val context: Context) : LocationProvider {

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    @SuppressLint("MissingPermission")
    override fun getLocationFlow(): Flow<LocationEntity> = callbackFlow {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            close(Exception("GPS is disabled"))
            return@callbackFlow
        }

        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                trySend(
                    LocationEntity(
                        time = location.time.toString(),
                        latitude = location.latitude,
                        longitude = location.longitude,
                        speed = location.speed.toInt(),
                        timestamp = location.time,
                        isUploaded = 0,
                        videoId = 0
                    )
                ).isSuccess
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            1000L, // Update interval (1 second)
            1f,   // Minimum distance change (1 meter)
            locationListener
        )

        awaitClose { locationManager.removeUpdates(locationListener) }
    }.flowOn(Dispatchers.IO)
}

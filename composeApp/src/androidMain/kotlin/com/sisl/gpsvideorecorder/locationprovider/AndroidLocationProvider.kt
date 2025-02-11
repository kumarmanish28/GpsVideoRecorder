package com.sisl.gpsvideorecorder.locationprovider

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import com.sisl.gpsvideorecorder.LocationData
import com.sisl.gpsvideorecorder.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidLocationProvider(private val context: Context) : LocationProvider {

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
}

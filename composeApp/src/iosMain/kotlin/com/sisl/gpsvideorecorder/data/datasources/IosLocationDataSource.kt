package com.sisl.gpsvideorecorder.data.datasources

import com.sisl.gpsvideorecorder.domain.models.LocationData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSDateFormatter
import platform.darwin.NSObject

class IosLocationDataSource : LocationDataSource {

    private val _locationUpdates = MutableSharedFlow<LocationData>(extraBufferCapacity = 10)
    override val locationUpdates: SharedFlow<LocationData> = _locationUpdates

    private val locationManagerDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            (didUpdateLocations.lastOrNull() as? CLLocation)?.toLocationData()?.let {
                _locationUpdates.tryEmit(it)
            }
        }
    }

    private val locationManager = CLLocationManager().apply {
        delegate = locationManagerDelegate
    }

    override fun startLocationTracking() {
        locationManager.requestWhenInUseAuthorization()
        locationManager.distanceFilter = 1.0
        locationManager.startUpdatingLocation()
    }

    override fun stopLocationTracking() {
        locationManager.stopUpdatingLocation()
    }

    private fun CLLocation.toLocationData(): LocationData {
//        val latitude = this.coordinate.latitude
//        val longitude = this.coordinate.longitude
        val latitude = 28.343555
        val longitude = 77.342345

        return LocationData(
            latitude = latitude,
            longitude = longitude,
            timestamp = this.timestamp.timeIntervalSinceReferenceDate.toLong(),
            time = NSDateFormatter().apply {
                dateFormat = "dd-MM-yyyy HH:mm:ss" // Added time for better accuracy
            }.stringFromDate(this.timestamp) ?: "N/A",
            isUploaded = 0
        )

    }
}

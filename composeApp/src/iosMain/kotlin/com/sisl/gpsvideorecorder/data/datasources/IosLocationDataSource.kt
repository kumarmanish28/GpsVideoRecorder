package com.sisl.gpsvideorecorder.data.datasources

import com.sisl.gpsvideorecorder.domain.models.LocationData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSLocale
import platform.darwin.NSObject

class IosLocationDataSource : LocationDataSource {

    private val _locationUpdates = MutableSharedFlow<LocationData>(extraBufferCapacity = 10)
    override val locationUpdates: SharedFlow<LocationData> = _locationUpdates
    private var lastLocation: CLLocation? = null


    private val locationManagerDelegate = object : NSObject(), CLLocationManagerDelegateProtocol {
        override fun locationManager(manager: CLLocationManager, didUpdateLocations: List<*>) {
            val current = (didUpdateLocations.lastOrNull() as? CLLocation) ?: return

            val previous = lastLocation
//            if (previous == null || current.distanceFromLocation(previous) >= 2.0) {
            lastLocation = current
            _locationUpdates.tryEmit(current.toLocationData())
//            }
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

    @OptIn(ExperimentalForeignApi::class)
    private fun CLLocation.toLocationData(): LocationData {

        val latitude = coordinate.useContents { latitude }
        val longitude = coordinate.useContents { longitude }
        val dateFormatter = NSDateFormatter().apply {
            // Set a locale that won't interfere with your fixed format
            locale = NSLocale(localeIdentifier = "en_US_POSIX")
            dateFormat = "dd-MM-yyyy HH:mm:ss"
            // Optional: If you want to ensure the time is in a specific timezone,
            // otherwise it will use the device's current timezone.
            // For UTC:
            // timeZone = NSTimeZone.timeZoneForSecondsFromGMT(0)
            // For device's system timezone (usually what you want unless you need consistency across devices):
            // timeZone = NSTimeZone.systemTimeZone
        }

        val formattedTime = dateFormatter.stringFromDate(this.timestamp) ?: "N/A"

        return LocationData(
            latitude = latitude,
            longitude = longitude,
            timestamp = this.timestamp.timeIntervalSinceReferenceDate.toLong(),
            time = formattedTime,
            isUploaded = 0,
            isDeleted = 0
        )

    }
}

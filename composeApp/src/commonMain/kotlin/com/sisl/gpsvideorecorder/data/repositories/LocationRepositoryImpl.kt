package com.sisl.gpsvideorecorder.data.repositories

import com.sisl.gpsvideorecorder.data.datasources.LocationDataSource
import com.sisl.gpsvideorecorder.data.local.dao.LocationDao
import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity
import com.sisl.gpsvideorecorder.data.local.entities.toDomain
import com.sisl.gpsvideorecorder.data.remote.response.ApiResponse
import com.sisl.gpsvideorecorder.data.remote.response.LocationsUploadResp
import com.sisl.gpsvideorecorder.data.remote.utils.ApiService
import com.sisl.gpsvideorecorder.domain.models.LocationData
import com.sisl.gpsvideorecorder.domain.repositories.LocationRepository
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class LocationRepositoryImpl(private val dao: LocationDao, private val apiService: ApiService) :
    LocationRepository, KoinComponent {
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

    // Networking Operations
    override suspend fun uploadLocation(videoId: Long?): Flow<ApiResponse<LocationsUploadResp>> {
        val listOfPendingData: List<LocationEntity> = if (videoId == null) {
            dao.getAllPendingLocations()
        } else {
            dao.getAllLocationsBasedOnVideoId(videoId)
        }

        val locationDataList = listOfPendingData.map { it.toDomain() }
        var videoName = if (videoId == null) "" else listOfPendingData[0].videoName

        // Get current instant
        val now = Clock.System.now()
        // Convert to local date time in system timezone
        val currentDateTime: LocalDateTime = now.toLocalDateTime(TimeZone.currentSystemDefault())

        videoName = if (videoName.isNullOrEmpty()) "video_$currentDateTime.mp4" else videoName
        return apiService.uploadCoordinate(videoName, locationDataList)
    }

    override suspend fun deleteLocation(videoId: Long): Flow<ApiResponse<Boolean>> = flow {
        try {
            emit(ApiResponse.Loading)
            dao.deleteDataBasedOnVideoId(videoId)
            emit(ApiResponse.Success(true))
        } catch (ex: Exception) {
            emit(ApiResponse.Error(ex.message ?: "No data found", 409))
        }
    }
}
package com.sisl.gpsvideorecorder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity
import com.sisl.gpsvideorecorder.data.local.models.DateWisePendingLocation
import com.sisl.gpsvideorecorder.data.local.models.LocationEntityDto
import com.sisl.gpsvideorecorder.data.local.models.LocationEntityWithVideoDto
import com.sisl.gpsvideorecorder.presentation.state.VideoItem
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM locations")
    fun getAllLocations(): Flow<List<LocationEntity>>

    @Query("DELETE FROM locations")
    suspend fun clearLocations()

    @Query("UPDATE locations SET isUploaded = :status WHERE id = :id")
    suspend fun updateUploadedStatus(id: Int, status: Int)

    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    fun getLastLocation(): Flow<LocationEntity?>

    /*  @Query(
          """
          SELECT videoId, strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') AS dateTime,
                 COUNT(*) AS coordinateCount, isUploaded, isDeleted
          FROM locations
          GROUP BY dateTime
          ORDER BY dateTime DESC
      """
      )
      suspend fun getDateWisePendingLocationData(): List<VideoItem>*/
    @Query(
        """
        SELECT videoId, strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') AS dateTime, 
               COUNT(*) AS coordinateCount, isUploaded, isDeleted
        FROM locations 
        GROUP BY videoId 
        ORDER BY videoId ASC
    """
    )
    suspend fun getDateWisePendingLocationData(): List<VideoItem>

    @Query("SELECT * , (SELECT count(*) FROM locations where videoId = (select max(videoId) from locations) ) as record  FROM locations WHERE videoId = (SELECT MAX(videoId) FROM locations) ORDER BY id DESC LIMIT 1")
    fun getLatestLocation(): Flow<LocationEntityDto?>


    @Query("SELECT * FROM locations where strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') = :dateTime")
    suspend fun getAllLocationsBasedOnDate(dateTime: String): List<LocationEntity>

    @Query("SELECT * FROM locations where videoId = :videoId")
    suspend fun getAllLocationsBasedOnVideoId(videoId: Long): List<LocationEntity>

    @Query("SELECT * FROM locations where isUploaded = 0")
    suspend fun getAllPendingLocations(): List<LocationEntity>

    @Query("DELETE FROM locations where strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') = :date")
    suspend fun clearLocationsDateWise(date: String)


    @Query("SELECT * ,  (SELECT IFNULL(MAX(videoId), 0) + 1 FROM locations) AS videoId  FROM locations ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLocationWithVideoId(): LocationEntityWithVideoDto?

    @Query(
        """
        SELECT videoId, strftime('%d-%m-%Y %H:%M:%S', timestamp / 1000, 'unixepoch', 'localtime') AS date, 
               COUNT(*) AS record 
        FROM locations 
        GROUP BY videoId 
        ORDER BY videoId
    """
    )
    suspend fun getLocationDataVideoIdVise(): List<DateWisePendingLocation>

    @Query("SELECT IFNULL(MAX(videoId), 0) AS videoId FROM locations")
    suspend fun getLocationVideoMaxId(): Long?

    @Query("SELECT * FROM locations where videoId = (Select max(videoId) from locations)")
    suspend fun getAllLocationsBasedOnVideoId(): List<LocationEntity>

    @Query("DELETE FROM locations where videoId = :videoId")
    suspend fun deleteDataBasedOnVideoId(videoId: Long)

    @Query("UPDATE locations SET videoName = :videoName, videoPath = :videoPath WHERE videoId = :videoId")
    suspend fun updateLocationWithVideoName(videoId: Long, videoName: String, videoPath: String)


}
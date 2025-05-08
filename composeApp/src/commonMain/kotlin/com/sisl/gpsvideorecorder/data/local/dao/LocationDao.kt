package com.sisl.gpsvideorecorder.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sisl.gpsvideorecorder.data.local.entities.LocationEntity
import com.sisl.gpsvideorecorder.data.local.models.DateWisePendingLocation
import com.sisl.gpsvideorecorder.data.local.models.LocationEntityDto
import com.sisl.gpsvideorecorder.data.local.models.LocationEntityWithVideoDto
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query("SELECT * FROM locations")
    suspend fun getAllLocations(): List<LocationEntity>

    @Query("DELETE FROM locations")
    suspend fun clearLocations()

    @Query("UPDATE locations SET isUploaded = :status WHERE id = :id")
    suspend fun updateUploadedStatus(id: Int, status: Int)

    /*@Query("SELECT * FROM locations ORDER BY id DESC LIMIT 1")
    fun getLatestLocation(): Flow<LocationEntity?>
*/
    @Query("SELECT * FROM locations ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLastLocation(): LocationEntity?

    /* @Query("""
         SELECT strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') AS date,
                COUNT(*) AS record
         FROM locations where isUploaded = 0
         GROUP BY date
         ORDER BY date DESC
     """)
     suspend fun getDateWisePendingLocationData(): List<DateWisePendingLocation>*/
    @Query(
        """
        SELECT videoId, strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') AS date, 
               COUNT(*) AS record 
        FROM locations 
        GROUP BY date 
        ORDER BY date DESC
    """
    )
    suspend fun getDateWisePendingLocationData(): List<DateWisePendingLocation>


    /*@Query("SELECT * , (SELECT count(*) FROM locations) AS record FROM locations ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLocation(): Flow<LocationEntityDto?>*/
    @Query("SELECT * , (SELECT count(*) FROM locations where videoId = (select max(videoId) from locations) ) as record  FROM locations WHERE videoId = (SELECT MAX(videoId) FROM locations) ORDER BY id DESC LIMIT 1")
    fun getLatestLocation(): Flow<LocationEntityDto?>


    @Query("SELECT * FROM locations where strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') = :dateTime")
    suspend fun getAllLocationsBasedOnDate(dateTime: String): List<LocationEntity>

    @Query("SELECT * FROM locations where videoId = :videoId")
    suspend fun getAllLocationsBasedOnVideoId(videoId: Long): List<LocationEntity>

    @Query("DELETE FROM locations where strftime('%d-%m-%Y', timestamp / 1000, 'unixepoch', 'localtime') = :date")
    suspend fun clearLocationsDateWise(date: String)


    @Query("SELECT * ,  (SELECT IFNULL(MAX(videoId), 0) + 1 FROM locations) AS videoId  FROM locations ORDER BY timestamp DESC LIMIT 1")
    fun getLatestLocationWithVideoId(): LocationEntityWithVideoDto?

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


}
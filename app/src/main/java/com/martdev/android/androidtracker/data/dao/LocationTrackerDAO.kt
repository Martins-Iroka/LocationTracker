package com.martdev.android.androidtracker.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.martdev.android.androidtracker.data.model.LocationTracker

@Dao
interface LocationTrackerDAO {

    @get:Query("SELECT * FROM location_tracker")
    val locations: List<LocationTracker>

    @Query("SELECT * FROM location_tracker WHERE id = :id")
    fun location(id: String): LocationTracker

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLocation(location: LocationTracker)

    @Query("DELETE FROM location_tracker")
    fun deleteLocations()

    @Query("DELETE FROM location_tracker WHERE id NOT IN (SELECT MIN(id) FROM location_tracker GROUP BY latitude, longitude, address)")
    fun deleteDuplicates()
}
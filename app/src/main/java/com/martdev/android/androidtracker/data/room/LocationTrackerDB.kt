package com.martdev.android.androidtracker.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.martdev.android.androidtracker.data.dao.LocationTrackerDAO
import com.martdev.android.androidtracker.data.model.LocationTracker

@Database(entities = [LocationTracker::class], version = 1)
abstract class LocationTrackerDB : RoomDatabase() {

    abstract fun locationTrackerDao(): LocationTrackerDAO

    companion object {

        private var INSTANCE: LocationTrackerDB? = null

        private val sLock = Any()

        @Synchronized
        fun getInstance(context: Context): LocationTrackerDB {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    LocationTrackerDB::class.java, "LocationTracker.db"
                )
                    .build()
            }
            return INSTANCE!!
        }
    }
}
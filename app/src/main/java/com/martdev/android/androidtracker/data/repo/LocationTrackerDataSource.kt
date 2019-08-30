package com.martdev.android.androidtracker.data.repo

import android.content.Context
import android.location.Location
import com.martdev.android.androidtracker.data.model.LocationTracker

interface LocationTrackerDataSource {

    interface LoadLocations {

        fun locationsLoaded(locationList: List<LocationTracker>)
    }

    interface GetLocation {

        fun locationLoaded(tracker: LocationTracker)
    }

    fun getLocations(locations: LoadLocations)

    fun getLocation(id: String, locate: GetLocation)

    fun saveLocation(location: LocationTracker)

    fun deleteDuplicates()

    fun deleteLocations()

    fun loadLocation(context: Context, location: Location, result: GetLocation)
}
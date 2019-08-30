package com.martdev.android.androidtracker.data.repo

import android.content.Context
import android.location.Location
import com.martdev.android.androidtracker.data.model.LocationTracker

class LocationTrackerRepo private constructor(private val dataSource: LocationTrackerDataSource) : LocationTrackerDataSource {

    override fun getLocations(locations: LocationTrackerDataSource.LoadLocations) {
        dataSource.getLocations(object : LocationTrackerDataSource.LoadLocations {
            override fun locationsLoaded(locationList: List<LocationTracker>) {
                locations.locationsLoaded(locationList)
            }
        })
    }

    override fun getLocation(id: String, locate: LocationTrackerDataSource.GetLocation) {
        dataSource.getLocation(id, object : LocationTrackerDataSource.GetLocation {
            override fun locationLoaded(tracker: LocationTracker) {
                locate.locationLoaded(tracker)
            }
        })
    }

    override fun saveLocation(location: LocationTracker) {
        dataSource.saveLocation(location)
    }

    override fun deleteDuplicates() {
        dataSource.deleteDuplicates()
    }

    override fun deleteLocations() {
        dataSource.deleteLocations()
    }

    override fun loadLocation(context: Context, location: Location, result: LocationTrackerDataSource.GetLocation) {
        dataSource.loadLocation(context, location, object : LocationTrackerDataSource.GetLocation {
            override fun locationLoaded(tracker: LocationTracker) {
                result.locationLoaded(tracker)
            }
        })
    }

    companion object {

        private var sRepo: LocationTrackerRepo? = null

        fun getRepository(dataSource: LocationTrackerDataSource): LocationTrackerRepo {
            if (sRepo == null) sRepo = LocationTrackerRepo(dataSource)

            return sRepo!!
        }
    }
}
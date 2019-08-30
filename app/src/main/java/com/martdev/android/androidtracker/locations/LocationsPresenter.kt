package com.martdev.android.androidtracker.locations

import com.martdev.android.androidtracker.data.model.LocationTracker
import com.martdev.android.androidtracker.data.repo.LocationTrackerDataSource

class LocationsPresenter(
    private val dataSource: LocationTrackerDataSource,
    private val view: LocationsContract.View
) : LocationsContract.Presenter {

    override fun loadLocations() {
        dataSource.getLocations(object : LocationTrackerDataSource.LoadLocations {
            override fun locationsLoaded(locationList: List<LocationTracker>) {
                if (locationList.isNullOrEmpty()) view.showNoLocation()
                else view.showLocations(locationList)
            }
        })
    }

    override fun deleteLocations() {
        dataSource.deleteLocations()
        view.showNoLocation()
    }

    override fun deleteDuplicates() {
        dataSource.deleteDuplicates()
    }
}
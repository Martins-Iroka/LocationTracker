package com.martdev.android.androidtracker.locations

import com.martdev.android.androidtracker.data.model.LocationTracker

interface LocationsContract {

    interface View {

        fun showLocations(locations: List<LocationTracker>)

        fun showNoLocation()

    }

    interface Presenter {

        fun loadLocations()

        fun deleteLocations()

        fun deleteDuplicates()

    }
}
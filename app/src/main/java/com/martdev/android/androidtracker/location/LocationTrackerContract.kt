package com.martdev.android.androidtracker.location

import android.location.Location
import com.google.android.gms.location.LocationCallback

interface LocationTrackerContract {

    interface View {

        fun showHistory()

        fun showRequestPermission()

        fun showLoadingIcon()

        fun stopLoadingIcon()

        fun showResult(address: String)
    }

    interface Presenter {

        fun startTracker(isTracking: Boolean, locationCallback: LocationCallback)

        fun stopTracker()

        fun loadResult(location: Location)
    }
}
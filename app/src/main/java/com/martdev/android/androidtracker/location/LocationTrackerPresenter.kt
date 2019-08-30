package com.martdev.android.androidtracker.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.martdev.android.androidtracker.data.model.LocationTracker
import com.martdev.android.androidtracker.data.repo.LocationTrackerDataSource

class LocationTrackerPresenter internal constructor(
    private val context: Context, private val dataSource: LocationTrackerDataSource,
    private val view: LocationTrackerContract.View
) : LocationTrackerContract.Presenter {

    private var fusedLocationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)



    override fun startTracker(isTracking: Boolean, locationCallback: LocationCallback) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            view.showRequestPermission()
        } else {
            if (isTracking) {
                fusedLocationProviderClient.requestLocationUpdates(
                    getLocationRequest(),
                    locationCallback,
                    null
                )

                view.showLoadingIcon()
            }
        }
    }

    override fun stopTracker() {
        view.stopLoadingIcon()
    }

    override fun loadResult(location: Location) {
        dataSource.loadLocation(context, location, object : LocationTrackerDataSource.GetLocation {
            override fun locationLoaded(tracker: LocationTracker) {
                dataSource.saveLocation(tracker)
                view.showResult(tracker.address)
            }
        })
    }

    private fun getLocationRequest(): LocationRequest {
        val locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        return locationRequest
    }
}
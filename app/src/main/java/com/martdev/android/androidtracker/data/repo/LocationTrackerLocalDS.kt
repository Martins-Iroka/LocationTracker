package com.martdev.android.androidtracker.data.repo

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.text.TextUtils
import android.util.Log
import com.martdev.android.androidtracker.AppExecutors
import com.martdev.android.androidtracker.R
import com.martdev.android.androidtracker.data.dao.LocationTrackerDAO
import com.martdev.android.androidtracker.data.model.LocationTracker
import java.io.IOException
import java.lang.IllegalArgumentException
import java.util.*

class LocationTrackerLocalDS(
    private val executor: AppExecutors,
    private val dao: LocationTrackerDAO
) : LocationTrackerDataSource {

    private var locationList: List<LocationTracker>? = null

    override fun getLocations(locations: LocationTrackerDataSource.LoadLocations) {
        executor.backgroundThread.execute {
            locationList = dao.locations
            executor.mainThread.execute {
                locations.locationsLoaded(locationList!!)
            }
        }
    }

    override fun getLocation(id: String, locate: LocationTrackerDataSource.GetLocation) {
        executor.backgroundThread.execute {
            val location = dao.location(id)
            executor.mainThread.execute { locate.locationLoaded(location) }
        }
    }

    override fun saveLocation(location: LocationTracker) {
        executor.backgroundThread.execute { dao.insertLocation(location) }
    }

    override fun deleteDuplicates() {
        executor.backgroundThread.execute { dao.deleteDuplicates() }
    }

    override fun deleteLocations() {
        executor.backgroundThread.execute {
            dao.deleteLocations()
        }
    }

    override fun loadLocation(context: Context, location: Location, result: LocationTrackerDataSource.GetLocation) {
        executor.backgroundThread.execute {

            val locationTracker = LocationTracker(latitude = location.latitude, longitude = location.longitude)

            val geoCoder = Geocoder(
                context,
                Locale.getDefault()
            )

            var addressList = mutableListOf<Address>()
            var resultMessage = ""

            try {
                addressList = geoCoder.getFromLocation(
                    location.latitude,
                    location.longitude,
                    1
                )

                Log.i(
                    TAG, "Latitude = " + location.latitude +
                            ", Longitude = " +
                            location.longitude
                )
            } catch (ioe: IOException) {

                resultMessage = context.getString(R.string.service_not_available)
                Log.e(TAG, resultMessage, ioe)
            } catch (iae: IllegalArgumentException) {
                resultMessage = context.getString(R.string.invalid_lat_long_used)
                Log.e(
                    TAG, resultMessage + ". " +
                            "Latitude = " + location.latitude +
                            ", Longitude = " +
                            location.longitude, iae
                )
            }

            if (addressList.isNullOrEmpty()) {
                if (resultMessage.isEmpty()) {
                    resultMessage = context.getString(R.string.no_address_found)
                    Log.e(TAG, resultMessage)
                }
            } else {

                val address = addressList[0]
                val addressParts = arrayListOf<String>()

                for (i in 0..address.maxAddressLineIndex)
                    addressParts.add(address.getAddressLine(i))

                resultMessage = TextUtils.join(
                    "\n",
                    addressParts
                )

                locationTracker.address = resultMessage

            }

            executor.mainThread.execute {
                result.locationLoaded(locationTracker)
            }
        }
    }

    companion object {

        private val TAG = LocationTrackerLocalDS::class.java.simpleName
        private var INSTANCE: LocationTrackerLocalDS? = null

        fun getInstance(executor: AppExecutors, dao: LocationTrackerDAO): LocationTrackerLocalDS? {
            if (INSTANCE == null) INSTANCE = LocationTrackerLocalDS(executor, dao)

            return INSTANCE
        }
    }
}
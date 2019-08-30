package com.martdev.android.androidtracker

import android.content.Context
import com.martdev.android.androidtracker.data.room.LocationTrackerDB
import com.martdev.android.androidtracker.data.repo.LocationTrackerLocalDS
import com.martdev.android.androidtracker.data.repo.LocationTrackerRepo

object Injection {

    fun provideRepo(context: Context): LocationTrackerRepo {
        val dataSource = LocationTrackerLocalDS.getInstance(AppExecutors.instance,
            LocationTrackerDB.getInstance(context).locationTrackerDao())
        return LocationTrackerRepo.getRepository(dataSource!!)
    }
}
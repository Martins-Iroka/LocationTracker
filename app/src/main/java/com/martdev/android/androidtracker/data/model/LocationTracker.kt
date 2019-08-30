package com.martdev.android.androidtracker.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "location_tracker")
data class LocationTracker(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "latitude")
    var latitude: Double = 0.0,

    @ColumnInfo(name = "longitude")
    var longitude: Double = 0.0,

    @ColumnInfo(name = "address")
    var address: String = ""
)
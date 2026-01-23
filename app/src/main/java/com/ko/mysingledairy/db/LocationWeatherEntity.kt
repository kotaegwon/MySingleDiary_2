package com.ko.mysingledairy.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationWeatherEntity")
data class LocationWeatherEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val city: String = "",
    val district: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val weatherCondition: String = ""
)

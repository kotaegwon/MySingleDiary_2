package com.ko.mysingledairy.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationWeatherEntity")
data class DiaryListEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,


)
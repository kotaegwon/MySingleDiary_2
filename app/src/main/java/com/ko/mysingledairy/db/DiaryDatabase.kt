package com.ko.mysingledairy.db

import androidx.room.Database

@Database(
    entities = [DiaryDatabase::class, LocationWeatherEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DiaryDatabase {
    abstract fun dairyDao(): DiaryDao
}
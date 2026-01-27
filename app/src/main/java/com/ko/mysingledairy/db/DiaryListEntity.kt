package com.ko.mysingledairy.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary")
data class DiaryListEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "weather")
    var weather: String,

    @ColumnInfo(name = "date")
    var date: String,

    @ColumnInfo(name = "address")
    var address: String,

    @ColumnInfo(name = "content")
    var content: String,

    @ColumnInfo(name = "picture")
    var picture: String,

    @ColumnInfo(name = "mood")
    var mood: Int
)
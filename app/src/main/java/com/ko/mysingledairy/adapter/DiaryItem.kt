package com.ko.mysingledairy.adapter

data class DiaryItem(
    var id: Int,
    var weather: String,
    var address: String,
    var locationX: String,
    var locationY: String,
    var contents: String,
    var mood: String,
    var picture: Boolean,
    var createDateStr: String
)

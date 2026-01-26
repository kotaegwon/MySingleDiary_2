package com.ko.mysingledairy.adapter

sealed class DiaryUiItem {
    data class Text(val diary: DiaryItem) : DiaryUiItem()
    data class Image(val diary: DiaryItem) : DiaryUiItem()
}
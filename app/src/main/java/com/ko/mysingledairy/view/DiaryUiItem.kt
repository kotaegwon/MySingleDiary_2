package com.ko.mysingledairy.view

sealed class DiaryUiItem {
    data class Text(val diary: DiaryItem) : DiaryUiItem()
    data class Image(val diary: DiaryItem) : DiaryUiItem()
}
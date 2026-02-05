package com.ko.mysingledairy.adapter

import com.ko.mysingledairy.db.DiaryListEntity

sealed class DiaryUiItem {
    data class Text(val diary: DiaryListEntity) : DiaryUiItem()
    data class Image(val diary: DiaryListEntity) : DiaryUiItem()
}
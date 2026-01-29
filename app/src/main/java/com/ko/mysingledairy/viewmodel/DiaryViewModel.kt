package com.ko.mysingledairy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ko.mysingledairy.DiarySharedStore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DiaryViewModel : ViewModel() {
    val cityDistrict: StateFlow<String?> =
        DiarySharedStore.cityDistrictFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = null
        )

    val weather: StateFlow<String?> =
        DiarySharedStore.weatherFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = null
        )
}
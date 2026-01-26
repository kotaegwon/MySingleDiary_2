package com.ko.mysingledairy

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ko.mysingledairy.repository.DiaryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class DiaryViewModel : ViewModel() {
    val cityDistrict: StateFlow<String?> =
        DiaryRepository.cityDistrictFlow.stateIn(
            scope = viewModelScope,
            started = SharingStarted.Companion.WhileSubscribed(5_000),
            initialValue = null
        )

    val weather: StateFlow<String?> =
        DiaryRepository.cityDistrictFlow
}
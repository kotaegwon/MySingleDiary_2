package com.ko.mysingledairy.repository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object DiaryRepository {
    private val _cityDistrictFlow = MutableStateFlow<String?>(null)
    val cityDistrictFlow: StateFlow<String?> = _cityDistrictFlow

    private val _weatherFlow = MutableStateFlow<String?>(null)
    val weatherFlow: StateFlow<String?> = _weatherFlow

    fun updateLocation(cityDistrict: String) {
        _cityDistrictFlow.value = cityDistrict
    }

    fun updateWeather(weather: String){
        _weatherFlow.value = weather
    }
}
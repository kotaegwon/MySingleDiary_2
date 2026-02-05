package com.ko.mysingledairy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ko.mysingledairy.repository.DiaryRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

class DiaryViewModel(private val repo: DiaryRepository) : ViewModel() {

    private val _cityDistrict =
        MutableStateFlow<String?>(null)
    val cityDistrict: StateFlow<String?> = _cityDistrict

    private val _weather =
        MutableStateFlow<String?>(null)
    val weather: StateFlow<String?> = _weather

    init {
        startTracking()
    }

    /**
     * 위치 + 날씨 자동 갱신
     */
    private fun startTracking() {

        viewModelScope.launch {

            while (isActive) {

                val location = repo.getLocation()
                val weather = repo.getWeather(location?.split(" ")[0])

                _cityDistrict.value = location
                _weather.value = weather

                Timber.i("startTracking: location:$location, weather:$weather")

                delay(10_000) // 10초
            }
        }
    }
}


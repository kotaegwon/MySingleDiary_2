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

/**
 * View와 Repository 사이에서 데이터를 관리하는 ViewModel 계층
 * 위치 및 날씨 정보를 주기적으로 가져와 UI에 전달하는 역할을 담당
 */
class DiaryViewModel(private val repo: DiaryRepository) : ViewModel() {

    /**
     * 현재 위치(도시/구)를 저장하는 내무 StateFlow
     * 외부에서는 수정할 수 없도록 private으로 선언
     */
    private val _cityDistrict =
        MutableStateFlow<String?>(null)

    // UI에서 관찰하는 공개용 StateFlow
    val cityDistrict: StateFlow<String?> = _cityDistrict

    /**
     * 현재 날씨 정보를 저장하는 내부 StateFlow
     */
    private val _weather =
        MutableStateFlow<String?>(null)

    // UI에서 관찰하는 공개용 StateFlow
    val weather: StateFlow<String?> = _weather

    /**
     * 위치 + 날씨 자동 갱신 함수
     * 
     * 일정 시간마다 위치와 날씨 정보를 갱신하여
     * StateFlow를 통해 UI에 전달
     */
    init {
        startTracking()
    }

    /**
     * 위치 + 날씨 자동 갱신
     * 
     * 일정 시간마다 위치와 날씨 정보를 갱신하여
     * StateFlow를 통해 UI에 전달
     */
    private fun startTracking() {
        
        // ViewModel 생명주기에 맞춰 자동 관리되는 코루틴 스코프
        viewModelScope.launch {
            
            // ViewModel이 살아있는 동안 반복 실행
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


package com.ko.mysingledairy.repository

import com.ko.mysingledairy.manager.LocationManager
import com.ko.mysingledairy.manager.WeatherManager

/**
 * 위치 정보와 날씨 정보를 관리하는 Repository 역할을 담당한다.
 * ViewModel과 실제 데이터 소스(LocationManager, WeatherManager)
 * 사이의 중간 계층이다
 */
class DiaryRepository(
    // 위치 정보를 가져오는 Manager 객체
    private val locationManager: LocationManager,

    // 날씨 정보를 가져오는 Manager 객체
    private val weatherManager: WeatherManager
) {
    /**
     * 현재 사용자의 위치 정보를 기반으로
     * 도시 및 구 이름을 가져오는 함수
     */
    suspend fun getLocation(): String? {
        return locationManager.fetchCityDistrict()
    }

    /**
     * 전달받은 도시 이름을 기반으로
     * 해당 지역으 ㅣ날씨를 가져오는 함수
     *
     * @param city 도시 이름
     * @return 날씨 정보 문자열(없을 경우 null)
     */
    suspend fun getWeather(city: String?): String? {

        if (city == null) return null

        return weatherManager.fetchWeather(city)
    }
}
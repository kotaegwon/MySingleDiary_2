package com.ko.mysingledairy.repository

import com.ko.mysingledairy.manager.LocationManager
import com.ko.mysingledairy.manager.WeatherManager

class DiaryRepository(
    private val locationManager: LocationManager,
    private val weatherManager: WeatherManager
) {

    suspend fun getLocation(): String? {
        return locationManager.fetchCityDistrict()
    }

    suspend fun getWeather(city: String?): String? {

        if (city == null) return null

        return weatherManager.fetchWeather(city)
    }
}
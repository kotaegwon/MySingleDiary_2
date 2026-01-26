package com.ko.mysingledairy

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.ko.mysingledairy.repository.DiaryRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber

class MainService : Service(), CoroutineScope by MainScope() {
    // 위치 -> 주소
    // 날씨
    // 위 정보를 받아 db 저장
    private lateinit var locationManager: LocationManager
    private lateinit var weatherManager: WeatherManager

    override fun onCreate() {
        Timber.d("onCreate +")

        super.onCreate()

        locationManager = LocationManager(this, this)
        weatherManager = WeatherManager(this, this)

        startLocationLoop()

        Timber.d("onCreate -")
    }

    override fun onDestroy() {
        Timber.d("onDestroy +")

        super.onDestroy()
        cancel()

        Timber.d("onDestroy -")

    }

    override fun onBind(p0: Intent?): IBinder? {
        Timber.d("onDestroy +")
        Timber.d("onDestroy -")
        return TODO("Provide the return value")
    }

    private fun startLocationLoop() {
        launch {
            while (true) {
                locationManager.fetchCityDistrict { cityDistrict ->
                    cityDistrict?.let {
                        Timber.d("위치 저장: $it")
                        DiaryRepository.updateLocation(it)
                    }
                }
                kotlinx.coroutines.delay(5 * 60 * 1000)
            }
        }
    }
}
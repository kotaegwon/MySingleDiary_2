package com.ko.mysingledairy

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

class WeatherManager(private val context: Context, private val scope: CoroutineScope) {

    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    /**
     * 도시 이름으로 날씨 정보 가져오기
     */
    fun fetchWeather(city: String, onResult: (String?) -> Unit) {
        if (city.isEmpty()) {
            onResult(null)
            return
        }

        scope.launch {
            val result = getWeatherFromApi(city)
            onResult(result)
        }
    }

    /**
     * API 호출 및 파싱
     */
    private suspend fun getWeatherFromApi(city: String): String? = withContext(Dispatchers.IO) {
        try {
            val response = URL(
                "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey&units=metric"
            ).readText()
            val json = JSONObject(response)

            val main = json.getJSONObject("main")
            val temp = main.getDouble("temp")
            val humidity = main.getInt("humidity")

            val weatherArray = json.getJSONArray("weather")
            val weatherObj = weatherArray.getJSONObject(0)
            val mainWeather = weatherObj.getString("main") // Clear, Clouds, Rain 등

            // 한글 상태로 변환
            val status = when (mainWeather) {
                "Clear" -> "맑음"
                "Clouds" -> "흐림"
                "Rain" -> "비"
                "Snow" -> "눈"
                else -> mainWeather
            }
            status
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

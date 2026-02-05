package com.ko.mysingledairy.manager

import com.ko.mysingledairy.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

/**
 * OpenWeather API를 이용하여
 * 현재 날씨 정보를 가져오는 매니저 클래스
 *
 * - 네트워크 작업은 IO 스레드에서 실행
 * - suspend 함수 기반 구조
 * - ViewModel/Repository에서 호출
 */
class WeatherManager {

    /** OpenWeather API Key */
    private val apiKey = BuildConfig.OPENWEATHER_API_KEY

    /**
     * 도시명을 기준으로
     * 현재 날씨 상태를 반환
     *
     * @param city 도시명 (예: Seoul)
     * @return 한글 날씨 상태 (없으면 null)
     */
    suspend fun fetchWeather(city: String): String? {

        if (city.isEmpty()) return null

        return getWeatherFromApi(city)
    }

    /**
     * OpenWeather API 호출 및 JSON 파싱
     */
    private suspend fun getWeatherFromApi(
        city: String
    ): String? = withContext(Dispatchers.IO) {

        try {

            val response = URL(
                "https://api.openweathermap.org/data/2.5/weather" +
                        "?q=$city" +
                        "&appid=$apiKey" +
                        "&units=metric"
            ).readText()

            val json = JSONObject(response)

            // 온도 / 습도 (확장 가능)
            val main = json.getJSONObject("main")
            val temp = main.getDouble("temp")
            val humidity = main.getInt("humidity")

            // 날씨 상태
            val weatherArray = json.getJSONArray("weather")
            val weatherObj = weatherArray.getJSONObject(0)

            val mainWeather =
                weatherObj.getString("main") // Clear, Clouds 등

            // 영어 → 한글 변환
            convertToKorean(mainWeather)

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    /**
     * 날씨 상태 영문 → 한글 변환
     */
    private fun convertToKorean(status: String): String {

        return when (status) {

            "Clear" -> "맑음"
            "Clouds" -> "흐림"
            "Rain" -> "비"
            "Snow" -> "눈"

            else -> status
        }
    }
}

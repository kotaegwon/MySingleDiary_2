package com.ko.mysingledairy.manager

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Locale
import kotlin.coroutines.resume

/**
 * 현재 위치 정보를 가져와
 * 행정구역(시/구 단위) 문자열로 변환하는 매니저 클래스
 *
 * 역할:
 * - GPS 기반으로 현재 위치 획득
 * - 위도/경도를 주소 문자열로 변환
 * - suspend 함수 형태로 제공
 *
 * 사용 위치:
 * → Repository → ViewModel → UI
 */
class LocationManager(private val context: Context) {

    /**
     * Google Fused Location Provider
     * 여러 위치 소스를 통합해서 가장 정확한 위치 제공
     */
    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    /**
     * 현재 위치 기반으로
     * "서울특별시 영등포구" 형태의 문자열로 반환
     *
     * suspend 함수이므로 코루틴 내부에서만 호출 가능
     *
     * 권한 필요
     * ACCESS_FINE_LOCATION / ACCESS_COARSE_LOCATION
     */
    @SuppressLint("MissingPermission")
    suspend fun fetchCityDistrict(): String? =
        suspendCancellableCoroutine { cont ->

            // 마지막으로 측정된 위치 요청
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    // 위치 정보가 없는 경우
                    if (location == null) {
                        cont.resume(null)
                        return@addOnSuccessListener
                    }

                    // 주소 변환은 네트워크/IO 작업 -> 백그라운드 실행
                    CoroutineScope(Dispatchers.IO).launch {

                        val result = getAddressFromLocation(
                            location.latitude,
                            location.longitude
                        )

                        // suspend 함수 재개 (결과 반환)
                        cont.resume(result)
                    }
                }
                // 위치 획들 실패 시
                .addOnFailureListener {
                    cont.resume(null)
                }
        }


    /**
     * 위도(lat), 경도(lon)를
     * 실제 주소 문자열로 변환하는 함수
     *
     * 내부적으로 Geocoder 사용
     *
     * 예:
     * 37.52, 126.90 → 서울특별시 영등포구
     */
    private suspend fun getAddressFromLocation(
        lat: Double,
        lon: Double
    ): String? = withContext(Dispatchers.IO) {

        // 한국 지역 기준  주소 변환기 생성
        val geocoder = Geocoder(context, Locale.KOREA)

        // 좌표 -> 주소 리스트 변환(최대 1개 요청)
        val addresses = geocoder.getFromLocation(lat, lon, 1)

        // 주소가 존재하면 가공해서 반환
        if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]

            // 예: "서울특별시 영등포구"
            "${addr.adminArea} ${addr.subLocality}"
        } else {
            // 주소 변환 실패
            null
        }
    }
}



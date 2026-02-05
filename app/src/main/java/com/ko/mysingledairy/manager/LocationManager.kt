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
 * - Coroutine 기반 suspend 함수 제공
 * - ViewModel/Repository에서 호출됨
 */
class LocationManager(private val context: Context) {

    private val fusedLocationClient =
        LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun fetchCityDistrict(): String? =
        suspendCancellableCoroutine { cont ->

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->

                    if (location == null) {
                        cont.resume(null, null)
                        return@addOnSuccessListener
                    }

                    CoroutineScope(Dispatchers.IO).launch {

                        val result = getAddressFromLocation(
                            location.latitude,
                            location.longitude
                        )

                        cont.resume(result, null)
                    }
                }
                .addOnFailureListener {
                    cont.resume(null, null)
                }
        }

    private suspend fun getAddressFromLocation(
        lat: Double,
        lon: Double
    ): String? = withContext(Dispatchers.IO) {

        val geocoder = Geocoder(context, Locale.KOREA)

        val addresses = geocoder.getFromLocation(lat, lon, 1)

        if (!addresses.isNullOrEmpty()) {
            val addr = addresses[0]
            "${addr.adminArea} ${addr.subLocality}"
        } else {
            null
        }
    }
}



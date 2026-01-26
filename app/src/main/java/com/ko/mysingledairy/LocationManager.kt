package com.ko.mysingledairy

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class LocationManager(private val context: Context, private val scope: CoroutineScope) {
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)


    @SuppressLint("MissingPermission")
    fun fetchCityDistrict(onResult: (String?) -> Unit) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location == null) {
                onResult(null)
                return@addOnSuccessListener
            }

            scope.launch {
                val result = getAddressFromLocation(
                    location.latitude,
                    location.longitude
                )
                onResult(result)
            }
        }.addOnFailureListener {
            onResult(null)
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
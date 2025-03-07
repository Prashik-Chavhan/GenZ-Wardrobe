package com.pc.genzwardrobe.core.data

import android.annotation.SuppressLint
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.data.remote.LocationRepository
import com.pc.genzwardrobe.data.remote.OpenCageApi
import com.pc.genzwardrobe.utils.Utils
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class LocationRepositoryImpl @Inject constructor(
    private val apiService: OpenCageApi,
    private val fusedLocationProviderClient: FusedLocationProviderClient
): LocationRepository {

    @SuppressLint("MissingPermission")
    override suspend fun getUsersCurrentCoordinates(): Pair<Double, Double>? {
        return suspendCoroutine { continuation->
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location->
                if (location != null) {
                    continuation.resume(Pair(location.latitude, location.longitude))
                    Log.d("Current_Lat_Long", "${location.latitude} / ${location.longitude}")
                } else {
                    continuation.resume(null)
                    Log.d("Current_Lat_Long", "Both are null")
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
        }
    }

    override suspend fun getUsersCurrentLocation(lat: Double, long: Double): UserAddress {
        return try {
            val response = apiService.getUsersGeoCode(Utils.GEO_CODING_API_KEY, lat, long)
            val address = response.address
            Log.d("LocationRepository", "${address.city}")
            UserAddress(
                pincode = address.postcode ?: "Unknown",
                city = address.city.toString(),
                state = address.state ?: "Unknown",
                name = "",
                area = "",
                addressType = "",
                phoneNumber = "",
                houseNo = ""
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("Location", "Failed to get user address")
            UserAddress(
                pincode = "441106",
                city = "Ramtek",
                state = "Maharashtra",
                name = "",
                area = "",
                addressType = "",
                phoneNumber = "",
                houseNo = ""
            )
        }
    }
}
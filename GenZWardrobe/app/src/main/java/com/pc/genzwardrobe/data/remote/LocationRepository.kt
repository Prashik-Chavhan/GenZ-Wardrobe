package com.pc.genzwardrobe.data.remote

import com.pc.genzwardrobe.core.domain.UserAddress
import retrofit2.Response

interface LocationRepository {
    suspend fun getUsersCurrentCoordinates(): Pair<Double, Double>?
    suspend fun getUsersCurrentLocation(lat: Double, long: Double): UserAddress
}
package com.pc.genzwardrobe.data.remote

import com.pc.genzwardrobe.core.domain.OpenCageResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenCageApi {

    @GET("v1/reverse")
    suspend fun getUsersGeoCode(
        @Query("key") apikey: String,
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("format") format: String = "json"
    ): OpenCageResponse
}
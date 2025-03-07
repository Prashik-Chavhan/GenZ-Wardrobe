package com.pc.genzwardrobe.data.remote

interface PreferenceDatastore {
    suspend fun saveSelectedAddressId(addressId: Int)
    suspend fun getSelectedAddressId(): Int
    suspend fun updateSelectedAddressId(newAddressId: Int)
}
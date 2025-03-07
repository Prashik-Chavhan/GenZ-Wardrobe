package com.pc.genzwardrobe.core.data

import com.pc.genzwardrobe.data.remote.InternetConnectivity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class InternetConnectivityImpl @Inject constructor() : InternetConnectivity{
    override suspend fun isConnected(): Flow<Boolean> {
        TODO("Not yet implemented")
    }
}
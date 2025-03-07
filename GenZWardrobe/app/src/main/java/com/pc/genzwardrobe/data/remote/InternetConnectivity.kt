package com.pc.genzwardrobe.data.remote

import kotlinx.coroutines.flow.Flow

interface InternetConnectivity {
    suspend fun isConnected(): Flow<Boolean>
}
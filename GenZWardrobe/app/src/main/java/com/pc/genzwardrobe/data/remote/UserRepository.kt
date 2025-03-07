package com.pc.genzwardrobe.data.remote

import com.pc.genzwardrobe.core.domain.PersonalInfo
import com.pc.genzwardrobe.core.domain.Wallet
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserInfo(): Flow<PersonalInfo>
    suspend fun getWalletData(): Flow<Wallet>
}
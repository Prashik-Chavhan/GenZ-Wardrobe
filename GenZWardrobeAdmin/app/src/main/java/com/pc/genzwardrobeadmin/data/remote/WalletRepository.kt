package com.pc.genzwardrobeadmin.data.remote

import kotlinx.coroutines.flow.Flow

interface WalletRepository {

    suspend fun getUsersWalletAmount(userId: String): Flow<Int>
}
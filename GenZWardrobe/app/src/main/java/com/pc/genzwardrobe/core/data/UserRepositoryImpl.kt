package com.pc.genzwardrobe.core.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pc.genzwardrobe.core.domain.PersonalInfo
import com.pc.genzwardrobe.core.domain.User
import com.pc.genzwardrobe.core.domain.Wallet
import com.pc.genzwardrobe.data.remote.UserRepository
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
): UserRepository {
    override suspend fun getUserInfo(): Flow<PersonalInfo> = callbackFlow {

        val listener = database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("personalInfo")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val personalInfo = snapshot.getValue(PersonalInfo::class.java)

                if (personalInfo != null) {
                    trySend(personalInfo)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun getWalletData(): Flow<Wallet> = callbackFlow {
        val listener = database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("wallet")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val wallet = snapshot.getValue(Wallet::class.java) ?: Wallet(0, emptyMap())

                trySend(wallet)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }
}
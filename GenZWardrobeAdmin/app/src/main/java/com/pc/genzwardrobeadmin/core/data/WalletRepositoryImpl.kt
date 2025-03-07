package com.pc.genzwardrobeadmin.core.data

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pc.genzwardrobeadmin.data.remote.WalletRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class WalletRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : WalletRepository {
    override suspend fun getUsersWalletAmount(userId: String): Flow<Int> = callbackFlow {
        val listener = database.getReference("AllUsers").child(userId).child("wallet")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val amount = snapshot.child("amount").getValue(Int::class.java)
                if (amount != null) {
                    trySend(amount)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }
}
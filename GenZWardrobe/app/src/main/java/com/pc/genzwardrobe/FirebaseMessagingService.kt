package com.pc.genzwardrobe

import android.util.Log
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessagingService
import com.pc.genzwardrobe.utils.Utils

class FirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("Token", "Saved Token: $token")
        saveFcmToken(token)
    }
}

fun saveFcmToken(token: String) {
    FirebaseDatabase.getInstance()
        .getReference("AllUsers")
        .child("${Utils.getCurrentUserId()}")
        .child("fcmToken")
        .setValue(token)
}
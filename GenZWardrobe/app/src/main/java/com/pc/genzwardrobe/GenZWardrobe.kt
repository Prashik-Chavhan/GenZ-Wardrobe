package com.pc.genzwardrobe

import android.app.Application
import android.content.res.Configuration
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.razorpay.PaymentResultListener
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GenZWardrobe: Application()
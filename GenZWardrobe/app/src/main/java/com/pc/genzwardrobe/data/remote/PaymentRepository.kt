package com.pc.genzwardrobe.data.remote

import android.app.Activity

interface PaymentRepository {

    fun startPayment(
        activity: Activity,
        orderAmount: Int,
        email: String,
        phoneNumber: String
    )
}
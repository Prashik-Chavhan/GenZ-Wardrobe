package com.pc.genzwardrobe.core.data

import android.app.Activity
import com.pc.genzwardrobe.BuildConfig
import com.pc.genzwardrobe.data.remote.PaymentRepository
import com.razorpay.Checkout
import org.json.JSONObject
import javax.inject.Inject

class PaymentRepositoryImpl @Inject constructor(): PaymentRepository {
    override fun startPayment(
        activity: Activity,
        orderAmount: Int,
        email: String,
        phoneNumber: String
    ) {
        val razorpayKey = BuildConfig.RAZORPAY_API_KEY
        val checkout = Checkout()
        checkout.setKeyID(razorpayKey)

        val option = JSONObject()

        option.put("name", "GenZ Wardrobe")
        option.put("description", "Order Payment")
        option.put("currency", "INR")
        option.put("amount", orderAmount * 100)

        val prefill = JSONObject()
        prefill.put("email", email)
        prefill.put("phoneNumber", phoneNumber)

        option.put("prefill", prefill)

        checkout.setFullScreenDisable(false)
        checkout.open(activity, option)
    }
}
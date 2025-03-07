package com.pc.genzwardrobe

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.pc.genzwardrobe.ui.presentation.app_navigation.App_Navigation
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.theme.GenZWardrobeTheme
import com.razorpay.PaymentResultListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity(), PaymentResultListener {

    private val viewModel: CartViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkNotificationPermission()
        setContent {
            GenZWardrobeTheme {
                App_Navigation()
            }
        }
    }

    override fun onPaymentSuccess(p0: String?) {
        Log.d("Payment", "Application Payment Successful: Payment Id = $p0")
        if (p0 != null) {
            viewModel.startPaymentSuccess(p0)
        }
    }

    override fun onPaymentError(p0: Int, p1: String?) {
        Log.d("Payment", "Application Payment Unsuccessful: Error code = $p0, Message = $p1")
        if (p1 != null) {
            viewModel.startPaymentError(p1)
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val hasNotificationPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED


            if (!hasNotificationPermission) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    0
                )
            }
        }
    }
}
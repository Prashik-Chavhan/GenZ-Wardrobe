package com.pc.genzwardrobeadmin.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.pc.genzwardrobeadmin.core.domain.Category
import okhttp3.internal.immutableListOf
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Utils {

    fun generateRandomId(): String {
        val randomId = (1..25).map { (('A'..'Z') + ('a'..'z') + (0..9)).random() }.joinToString("")
        return randomId
    }

    fun showToast(context: Context, text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
    }

    fun getCurrentAdminId(): String {
        val adminUId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        return adminUId
    }

    fun getTodaysDate(): String {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }

    fun longTimeToHumanReadable(time: Long): String {
        val date = Date(time)
        val format = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return format.format(date)
    }

    object CategoryList {
        val categoryList = listOf(
            Category(0, "All"),
            Category(1, "Men's"),
            Category(2, "Women's")
        )
    }

    object OrderStatusList {
        val orderStatus = immutableListOf(
            "Placed / processing",
            "Prepared",
            "Confirmed",
            "Packed",
            "On its way",
            "Out for Delivery",
            "Delivered",
            "Failed",
            "Processing return",
            "Processing refund",
            "Refund completed",
            "Canceled"
        )
    }
}
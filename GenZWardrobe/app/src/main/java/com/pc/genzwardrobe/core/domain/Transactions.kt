package com.pc.genzwardrobe.core.domain

data class Transactions(
    val transactionId: String? = "",
    val type: String? = "", //deposit, purchase
    val amount: Int? = 0,
    val date: String? = "",
    val timeStamp: Long? = 0L
)
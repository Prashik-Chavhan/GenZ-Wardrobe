package com.pc.genzwardrobeadmin.core.domain

data class Wallet(
    val amount: Int? = 0,
    val transactions: Map<String, Transactions>? = emptyMap()
)

data class Transactions(
    val transactionId: String? = "",
    val type: String? = "", //deposit, purchase
    val amount: Int? = 0,
    val date: String? = "",
    val timeStamp: Long? = 0L
)

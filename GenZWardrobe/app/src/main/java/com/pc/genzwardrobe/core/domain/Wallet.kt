package com.pc.genzwardrobe.core.domain

data class Wallet(
    val amount: Int? = 0,
    val transactions: Map<String, Transactions>? = emptyMap()
)
package com.pc.genzwardrobe.core.domain

data class User(
    val userId: String? = "",
    val userPhoneNumber: String? = "",
    val fcmToken: String? = "",
    val personalInfo: PersonalInfo? = null,
    val userAddress: List<UserAddress>? = emptyList(),
    val wallet: Wallet? = null
)
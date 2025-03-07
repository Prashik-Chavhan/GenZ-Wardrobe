package com.pc.genzwardrobeadmin.core.domain

data class UserAddress(
    val id: Int? = 0,
    val name: String = "",
    val phoneNumber: String = "",
    val pincode: String = "",
    val state: String = "",
    val city: String = "",
    val houseNo: String = "",
    val area: String = "",
    val addressType: String = ""
)
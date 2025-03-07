package com.pc.genzwardrobeadmin.core.domain


data class OrderedProducts(
    val userId: String? = "",
    val userAddress: UserAddress? = null,
    val orderId: String? = "",
    val products: List<CartProducts>? = emptyList(),
    val orderDate: String? = "",
    val orderTime: Long? = 0L,
    val totalAmount: Int? = 0
)
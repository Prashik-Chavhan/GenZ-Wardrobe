package com.pc.genzwardrobeadmin.core.domain

data class CartProducts(
    val itemId: Int = 0,
    val variantId: String? = "",
    val variantName: String? = "",
    val variantColor: String? = "",
    val productImageUri: String? = "",
    var productQuantity: Int? = 0,
    val size: String = "",
    val originalPrice: Int? = 0,
    val discount: Int? = 0,
    val discountPrice: Double? = 0.0,
    val productGender: String? = "",
    val productCategory: String? = "",
    val productType: String? = "",
    val orderStatus: Int? = 0
)
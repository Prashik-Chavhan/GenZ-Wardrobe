package com.pc.genzwardrobe.data.local.cartProducts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cartProducts")
data class CartProducts(

    @PrimaryKey(autoGenerate = false)
    val itemId: Int,
    val variantId: String? = null,
    val variantName: String? = null,
    val variantColor: String? = null,
    val productImageUri: String? = null,
    var productQuantity: Int? = null,
    val size: String = "",
    val originalPrice: Int? = null,
    val discount: Int? = null,
    val discountPrice: Double? = null,
    val productGender: String? = null,
    val productCategory: String? = null,
    val productType: String? = null,
    val orderStatus: Int? = null
) {
    constructor(): this(
        0,
        null,
        null,
        null,
        null,
        0,
        "",
        0,
        0,
        0.0,
        null,
        null,
        null,
        null
    )
}

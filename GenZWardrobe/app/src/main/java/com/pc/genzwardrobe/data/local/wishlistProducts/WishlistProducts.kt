package com.pc.genzwardrobe.data.local.wishlistProducts

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlistProducts")
data class WishlistProducts(

    @PrimaryKey(autoGenerate = false)
    val itemId: Int,
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

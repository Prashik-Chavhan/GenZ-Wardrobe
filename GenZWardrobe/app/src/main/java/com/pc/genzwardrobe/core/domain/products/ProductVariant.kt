package com.pc.genzwardrobe.core.domain.products

data class ProductVariant(
    val variantName: String? = "",
    val sellCount: Int = 0,
    val color: String = "",
    val originalPrice: Int? = 0,
    val discount: Int? = 0,
    val sizeDetails: Map<String, SizeStock> = emptyMap(),
    var variantImages: List<String?> = emptyList()
)
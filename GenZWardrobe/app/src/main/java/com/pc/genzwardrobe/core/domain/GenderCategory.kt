package com.pc.genzwardrobe.core.domain

data class GenderCategory(
    val id: Int,
    val text: String,
)

data class GenderCategories(
    val name: String,
    val categories: List<ProductCategory>
)

data class ProductCategory(
    val name: String,
    val type: List<String>
)
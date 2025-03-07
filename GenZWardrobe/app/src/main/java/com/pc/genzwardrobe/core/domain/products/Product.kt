package com.pc.genzwardrobe.core.domain.products

data class Product(
    val productId: String? = "",
    val productBrand: String? = "",
    val productVariants: Map<String, ProductVariant> = emptyMap(),
    val productHighlight: Map<String, ProductHighlight> = emptyMap(),
    val productGender: String? = "",
    val productCategory: String? = "",
    val productType: String? = "",
    val dateAdded: Long? = 0L,
    val description: String? = "",
    val returnPolicy: Int? = 0,
    val adminUid: String? = "",

    val fit: String? = "",
    val fabric: String? = "",
    val sleeveType: String? = "",
    val pattern: String? = "",
    val collarType: String? = "",
    val occasion: String? = "",
    val closure: String? = "",
    val faded: String? = "",
    val rise: String? = "",
    val material: String? = "",
    val distressed: String? = "",
    val suitableFor: String? = "",
    val strapMaterial: String? = "",
    val movementType: String? = "",
    val waterResistance: String? = "",
    val compartments: List<String>? = emptyList(),
    val features: List<String>? = emptyList()
)
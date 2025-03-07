package com.pc.genzwardrobe.core.domain

data class OpenCageResponse(
    val address: Components
)

data class Components(
    val postcode: String?,
    val city: String?,
    val state: String?
)
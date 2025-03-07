package com.pc.genzwardrobe.core.domain

data class UserReview(
    val userId: String = "",
    val userName: String = "",
    val images: List<String?> = emptyList(),
    val color: String = "",
    val title: String = "",
    val rating: Int = 0,
    val comment: String = "",
    val timeStamp: Long = 0
)
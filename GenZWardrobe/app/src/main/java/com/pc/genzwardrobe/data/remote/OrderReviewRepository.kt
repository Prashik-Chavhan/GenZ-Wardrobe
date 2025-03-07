package com.pc.genzwardrobe.data.remote

import androidx.paging.PagingData
import com.pc.genzwardrobe.core.domain.products.Product
import com.pc.genzwardrobe.core.domain.UserReview
import kotlinx.coroutines.flow.Flow

interface OrderReviewRepository {
    suspend fun canReviewProduct(productId: String, color: String, onSuccess: (Boolean) -> Unit)
    suspend fun getProductForReview(productId: String): Flow<Product>

    suspend fun getProductReviews(productId: String, sortBy: String): Flow<PagingData<Pair<String, UserReview>>>
}
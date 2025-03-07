package com.pc.genzwardrobeadmin.data.remote

import androidx.paging.PagingData
import com.pc.genzwardrobeadmin.core.domain.OrderedProducts
import com.pc.genzwardrobeadmin.core.domain.product.ProductVariant
import kotlinx.coroutines.flow.Flow

interface ProductRepository {

    suspend fun fetchAllVariants(): Flow<List<ProductVariant>>
    suspend fun getSearchedVariants(searchQuery: String): Flow<List<Pair<String, ProductVariant>>>
    suspend fun fetchVariantsByGender(gender: String): Flow<PagingData<ProductVariant>>
    suspend fun getUserIdsAndOrderIds(): Flow<PagingData<Triple<String, String, List<String>>>>
    suspend fun getOrderedProductsDetails(
        userId: String,
        orderId: String
    ): Flow<List<OrderedProducts>>

    suspend fun updateOrderStatus(
        userId: String,
        orderId: String,
        itemId: Int,
        orderStatus: Int
    ): Result<Unit>
}
package com.pc.genzwardrobe.data.remote

import androidx.paging.PagingData
import com.pc.genzwardrobe.core.domain.OrderedProducts
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.core.domain.products.Product
import com.pc.genzwardrobe.core.domain.products.ProductHighlight
import com.pc.genzwardrobe.core.domain.products.ProductVariant
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun fetchAllProductVariants(): Flow<List<ProductVariant>>
    suspend fun fetchProductsByGender(productGender: String): Flow<List<ProductVariant>>
    suspend fun getByPaging(
        gender: String, category: String, type: String,
        sortBy: String, minPrice: Int?, maxPrice: Int?,
        selectedDiscount: List<String>, selectedFabric: Set<String>,
        selectedOccasion: Set<String>, selectedColor: Set<String>
    ): Flow<PagingData<Triple<String, String, ProductVariant>>>

    suspend fun productDetailsScreen(productId: String): Flow<Product> // Passing entire product by Id
    suspend fun getProductHighlight(productId: String): Flow<List<ProductHighlight>>
    suspend fun getAllUserAddress(): Flow<List<UserAddress>>
    suspend fun getSimilarProducts(
        color: String,
        gender: String,
        category: String,
        type: String
    ): Flow<List<Triple<String, String, ProductVariant>>>
    suspend fun getALLMyOrderProducts(): Flow<List<OrderedProducts>>
    suspend fun getSelectedMyOrder(
        orderId: String,
        itemId: Int
    ): Flow<Pair<CartProducts, UserAddress>>

    suspend fun getOtherMyOrder(orderId: String, itemId: Int): Flow<List<CartProducts>>

    suspend fun getSearchedVariants(searchQuery: String): Flow<List<Pair<String, ProductVariant>>>

    suspend fun getProductsStockQuantity(
        productId: String,
        color: String,
        size: String
    ): Flow<Int>
}
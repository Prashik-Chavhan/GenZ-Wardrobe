package com.pc.genzwardrobeadmin.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobeadmin.core.domain.product.Product
import com.pc.genzwardrobeadmin.core.domain.product.ProductVariant
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAllProductPaging @Inject constructor(
    private val database: FirebaseDatabase,
    private val productGender: String
): PagingSource<String, ProductVariant>() {
    override fun getRefreshKey(state: PagingState<String, ProductVariant>): String? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey
                ?: state.closestPageToPosition(anchorPosition)?.nextKey
        }
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, ProductVariant> {

        return try {
            val pageSize = params.loadSize
            val startKey = params.key ?: ""

            val productRef = when(productGender) {
                "Men's", "Women's" -> database.getReference("Admin").child("ProductByGender").child(productGender)
                else ->  database.getReference("Admin").child("AllProducts")
            }

            val query = productRef.orderByKey().let {
                if (startKey.isNotEmpty()) {
                    it.startAfter(startKey)
                } else {
                    it
                }
            }.limitToFirst(pageSize)

            val productSnapshot = query.get().await()

            val variantList = mutableListOf<ProductVariant>()
            var lastKey: String? = null

            for (products in productSnapshot.children) {
                val product = products.getValue(Product::class.java)

                product?.productVariants?.forEach { (key, variant) ->
                    variantList.add(variant)
                }
                lastKey = products.key
            }

            val nextKey = if (variantList.size == pageSize) lastKey else null

            LoadResult.Page(
                data = variantList,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
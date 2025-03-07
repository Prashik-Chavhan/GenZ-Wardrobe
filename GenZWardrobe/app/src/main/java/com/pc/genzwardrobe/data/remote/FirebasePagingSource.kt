package com.pc.genzwardrobe.data.remote

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobe.core.domain.products.Product
import com.pc.genzwardrobe.core.domain.products.ProductVariant
import kotlinx.coroutines.tasks.await

class FirebasePagingSource(
    private val gender: String,
    private val category: String,
    private val type: String,
    private val sortBy: String,
    private val minPrice: Int?,
    private val maxPrice: Int?,
    private val selectedDiscount: List<String>,
    private val selectedFabric: Set<String>,
    private val selectedOccasion: Set<String>,
    private val selectedColor: Set<String>
) : PagingSource<Int, Triple<String, String, ProductVariant>>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Triple<String, String, ProductVariant>> {

        val currentPage = params.key ?: 1
        val pageSize = params.loadSize

        return try {
            val query = FirebaseDatabase.getInstance()
                .getReference("Admin")
                .child("ProductGenderCategoryType")
                .child(gender)
                .child(category)
                .child(type)
                .limitToFirst(currentPage * pageSize)

            val snapshot = query.get().await()
            Log.d("FirebasePagingSource", "Snapshot: ${snapshot.childrenCount} children fetched")

            val allVariants = mutableListOf<Triple<String, String, ProductVariant>>()

            // Iterate through each products
            for (productSnapshot in snapshot.children) {

                // save productId
                val productId = productSnapshot.key ?: continue

                val products = productSnapshot.getValue(Product::class.java)

                Log.d("FirebasePagingSource", "Processing product: $productId")

//                if (selectedBrand.isNotEmpty() && !selectedBrand.contains(products?.productBrand!!)) continue
                if (selectedFabric.isNotEmpty() && !selectedFabric.any{ fabric -> products?.fabric?.contains(fabric, true) == true }) continue

                if (selectedOccasion.isNotEmpty() && !selectedOccasion.contains(products?.occasion)) continue

                products?.productVariants?.forEach { (_, variants) ->
                    val color = variants.color

                    if (selectedColor.isNotEmpty() && !selectedColor.any { colors -> color.contains(colors, ignoreCase = true) } ) return@forEach

                    val finalPrice = variants.originalPrice?.let { price ->
                        variants.discount?.let { discount ->
                            price.times(100 - discount).div(100)
                        }
                    }

                    if (finalPrice != null) {
                        if (minPrice != null && finalPrice < minPrice) return@forEach
                        if (maxPrice != null && finalPrice > maxPrice) return@forEach
                    }

                    if (selectedDiscount.isNotEmpty()) {
                        val minSelectedDiscount =
                            selectedDiscount.minOfOrNull { it.toInt() } ?: return@forEach
                        if (variants.discount!! < minSelectedDiscount) return@forEach
                    }

//                    if (selectedSizes.isNotEmpty() && !variants.sizeDetails.keys.any { it in selectedSizes }) return@forEach

                    allVariants.add(Triple(productId, color, variants))
                }
            }

            val startIndex = (currentPage - 1) * pageSize

            val variantList = allVariants.sortedWith(
                when (sortBy) {
                    "Price High to Low" -> compareByDescending { it.third.originalPrice }

                    "Price Low to High" -> compareBy { it.third.originalPrice }

                    "Popularity" -> compareByDescending { it.third.sellCount }

                    else -> compareBy { it.second }
                }
            ).drop(startIndex).take(pageSize)

            LoadResult.Page(
                data = variantList,
                prevKey = if (currentPage > 1) currentPage - 1 else null,
                nextKey = if (variantList.size == pageSize) currentPage + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Triple<String, String, ProductVariant>>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
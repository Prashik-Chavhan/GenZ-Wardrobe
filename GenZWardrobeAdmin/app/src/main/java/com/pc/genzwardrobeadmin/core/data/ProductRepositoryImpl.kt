package com.pc.genzwardrobeadmin.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pc.genzwardrobeadmin.core.domain.OrderedProducts
import com.pc.genzwardrobeadmin.core.domain.product.Product
import com.pc.genzwardrobeadmin.core.domain.product.ProductVariant
import com.pc.genzwardrobeadmin.data.remote.FirebaseAllProductPaging
import com.pc.genzwardrobeadmin.data.remote.FirebasePagingSource
import com.pc.genzwardrobeadmin.data.remote.ProductRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : ProductRepository {
    override suspend fun fetchAllVariants(): Flow<List<ProductVariant>> = callbackFlow {
        val listener = database.getReference("Admin").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val variantList = mutableListOf<ProductVariant>()

                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)

                    product?.productVariants?.forEach { (_, variant) ->
                        variantList.add(variant)
                    }
                }
                trySend(variantList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun getSearchedVariants(searchQuery: String): Flow<List<Pair<String, ProductVariant>>> =
        callbackFlow {
            val listener = database.getReference("Admin").child("AllProducts")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val variantList = mutableListOf<Pair<String, ProductVariant>>()

                    for (products in snapshot.children) {
                        val product = products.getValue(Product::class.java)
                        val productId = product?.productId ?: continue

                        product.productVariants.values.forEach { productVariant ->
                            if (productVariant.variantName?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) == true ||
                                productVariant.color.contains(searchQuery, true) ||
                                product.productCategory?.contains(searchQuery, true) == true ||
                                product.productType?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) == true
                            ) {
                                variantList.add(Pair(productId, productVariant))
                            }
                        }
                    }
                    trySend(variantList)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }

            listener.addValueEventListener(eventListener)
            awaitClose { listener.removeEventListener(eventListener) }
        }

    override suspend fun fetchVariantsByGender(
        gender: String
    ): Flow<PagingData<ProductVariant>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FirebaseAllProductPaging(
                    database = database,
                    productGender = gender
                )
            }
        ).flow
    }

    override suspend fun getUserIdsAndOrderIds(): Flow<PagingData<Triple<String, String, List<String>>>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { FirebasePagingSource(database) }
        ).flow
    }

    override suspend fun getOrderedProductsDetails(
        userId: String,
        orderId: String
    ): Flow<List<OrderedProducts>> = callbackFlow {
        val listener =
            database.getReference("Admin").child("OrderedProducts").child(userId).child(orderId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val orderedProducts = snapshot.getValue(OrderedProducts::class.java)
                trySend(listOf(orderedProducts!!))
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun updateOrderStatus(
        userId: String,
        orderId: String,
        itemId: Int,
        orderStatus: Int
    ): Result<Unit> {
        val listener = database.getReference("Admin")
            .child("OrderedProducts")
            .child(userId)
            .child(orderId)
            .child("products")

        return try {
            val snapshot = listener.get().await()

            for (product in snapshot.children) {
                val productItemId = product.child("itemId").getValue(Int::class.java)

                if (productItemId == itemId) {
                    product.ref.child("orderStatus").setValue(orderStatus)
                    return Result.success(Unit)
                }
            }
            Result.failure(Exception("Item with $itemId not found"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
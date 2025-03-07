package com.pc.genzwardrobe.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pc.genzwardrobe.core.domain.products.Product
import com.pc.genzwardrobe.core.domain.UserReview
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.data.remote.OrderReviewRepository
import com.pc.genzwardrobe.data.remote.UserReviewsPagingSource
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class OrderReviewRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : OrderReviewRepository {
    override suspend fun canReviewProduct(
        productId: String,
        color: String,
        onSuccess: (Boolean) -> Unit
    ) {
        val userId = Utils.getCurrentUserId()
        val orderProductRef =
            database.getReference("Admin").child("OrderedProducts").child("$userId")

        orderProductRef.get()
            .addOnSuccessListener { snapshot ->
                var canReview = false

                for (orderIds in snapshot.children) {
                    val productList = orderIds.child("products")

                    for (productSnapshot in productList.children) {
                        val product = productSnapshot.getValue(CartProducts::class.java)

                        if(
                            product?.variantId.toString() == productId &&
                            product?.variantColor.toString() == color &&
                            product?.orderStatus!! >= 6
                        ) {
                            canReview = true
                            break
                        }
                    }
                    if (canReview) break
                }
                onSuccess(canReview)
            }
            .addOnFailureListener {
                onSuccess(false)
            }
    }

    override suspend fun getProductReviews(
        productId: String,
        sortBy: String
    ): Flow<PagingData<Pair<String, UserReview>>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                UserReviewsPagingSource(
                    productId = productId,
                    sortBy = sortBy
                )
            }
        ).flow
    }
    override suspend fun getProductForReview(productId: String): Flow<Product> = callbackFlow {
        val listener = database.getReference("Admin").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val product = snapshot.child(productId).getValue(Product::class.java)

                if (product != null) {
                    trySend(product)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }
}
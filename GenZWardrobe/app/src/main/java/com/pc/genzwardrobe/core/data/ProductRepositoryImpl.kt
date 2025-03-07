package com.pc.genzwardrobe.core.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.pc.genzwardrobe.core.domain.OrderedProducts
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.core.domain.products.Product
import com.pc.genzwardrobe.core.domain.products.ProductHighlight
import com.pc.genzwardrobe.core.domain.products.ProductVariant
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.data.remote.FirebasePagingSource
import com.pc.genzwardrobe.data.remote.ProductRepository
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : ProductRepository {

    override suspend fun fetchAllProductVariants(): Flow<List<ProductVariant>> = callbackFlow {

        val listener = database.getReference("Admin").child("AllProducts")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Create an variant list
                val variantList = mutableListOf<ProductVariant>()

                // Iterate loop to get list of Products
                for (productSnapshot in snapshot.children) {

                    // Storing products
                    val products = productSnapshot.getValue(Product::class.java)

                    // Get the Product Variants from product
                    products?.productVariants?.forEach { (_, variant) ->

                        val sellCount = variant.sellCount

                        if (sellCount > 30) {
                            variantList.add(variant)
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

    override suspend fun fetchProductsByGender(productGender: String): Flow<List<ProductVariant>> =
        callbackFlow {
            val listener =
                database.getReference("Admin").child("ProductByGender").child(productGender)

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val variantList = mutableListOf<ProductVariant>()

                    for (productSnapshot in snapshot.children) {
                        val products = productSnapshot.getValue(Product::class.java)

                        products?.productVariants?.forEach { (_, variant) ->
                            val sellCount = variant.sellCount

                            if (sellCount > 30) {
                                variantList.add(variant)
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

    override suspend fun getByPaging(
        gender: String, category: String, type: String,
        sortBy: String, minPrice: Int?, maxPrice: Int?,
        selectedDiscount: List<String>, selectedFabric: Set<String>,
        selectedOccasion: Set<String>, selectedColor: Set<String>
    ): Flow<PagingData<Triple<String, String, ProductVariant>>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FirebasePagingSource(
                    gender, category, type,
                    sortBy, minPrice, maxPrice,
                    selectedDiscount, selectedFabric,
                    selectedOccasion, selectedColor
                )
            }
        ).flow
    }

    override suspend fun productDetailsScreen(
        productId: String,
    ): Flow<Product> = callbackFlow {
        val listener = database.getReference("Admin").child("AllProducts").child(productId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val product = snapshot.getValue(Product::class.java)

                product?.let {
                    trySend(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun getProductHighlight(productId: String): Flow<List<ProductHighlight>> =
        callbackFlow {
            val listener = database.getReference("Admin").child("AllProducts").child(productId)
                .child("productHighlight")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productHighlight = snapshot.children.mapNotNull {
                        it.getValue(ProductHighlight::class.java)
                    }
                    trySend(productHighlight)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            }

            listener.addValueEventListener(eventListener)
            awaitClose { listener.removeEventListener(eventListener) }
        }

    override suspend fun getAllUserAddress(): Flow<List<UserAddress>> = callbackFlow {
        val listener = database.getReference("AllUsers").child("${Utils.getCurrentUserId()}")
            .child("userAddress")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userAddresses = snapshot.children.mapNotNull {
                    it.getValue(UserAddress::class.java)
                }
                trySend(userAddresses)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun getSimilarProducts(
        color: String,
        gender: String,
        category: String,
        type: String
    ): Flow<List<Triple<String, String, ProductVariant>>> = callbackFlow {
        val listener =
            database.getReference("Admin").child("ProductGenderCategoryType").child(gender)
                .child(category).child(type)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val variantsList = mutableListOf<Triple<String, String, ProductVariant>>()

                for (products in snapshot.children) {

                    val productId = products.key ?: continue

                    val product = products.getValue(Product::class.java)

                    val brandName = product?.productBrand ?: continue

                    val filteredVariants = product.productVariants.filter { (_, variant) ->
                        variant.color != color
                    }
                    filteredVariants.forEach { (_, variant) ->

                        variantsList.add(Triple(productId, brandName, variant))
                    }

                    trySend(variantsList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }


    override suspend fun getALLMyOrderProducts(): Flow<List<OrderedProducts>> = callbackFlow {
        val listener = database.getReference("Admin").child("OrderedProducts")
            .child("${Utils.getCurrentUserId()}")

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orderedProductList = mutableListOf<OrderedProducts>()

                for (orderedProducts in snapshot.children) {
                    val orderedProduct = orderedProducts.getValue(OrderedProducts::class.java)

                    if (orderedProduct != null) {
                        orderedProductList.add(orderedProduct)
                    }
                }
                trySend(orderedProductList)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        }

        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun getSelectedMyOrder(
        orderId: String,
        itemId: Int
    ): Flow<Pair<CartProducts, UserAddress>> = callbackFlow {
        val listener = database
            .getReference("Admin")
            .child("OrderedProducts")
            .child("${Utils.getCurrentUserId()}")
            .child(orderId)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                // Get user address
                val userAddress = snapshot.child("userAddress").getValue(UserAddress::class.java)

                // Get products node
                val products = snapshot.child("products")

                // Iterate each item in products node
                for (product in products.children) {

                    // Get itemId from product
                    val id = product.child("itemId").getValue(Int::class.java)

                    if (id == itemId) {
                        // if id matches then get that product
                        val orderedProduct = product.getValue(CartProducts::class.java)

                        if (userAddress != null && orderedProduct != null) {
                            trySend(Pair(orderedProduct, userAddress))
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                cancel("Database error: ${error.message}", error.toException())
            }

        }
        listener.addValueEventListener(eventListener)
        awaitClose { listener.removeEventListener(eventListener) }
    }

    override suspend fun getOtherMyOrder(orderId: String, itemId: Int): Flow<List<CartProducts>> =
        callbackFlow {
            val listener = database
                .getReference("Admin")
                .child("OrderedProducts")
                .child("${Utils.getCurrentUserId()}")
                .child(orderId)
                .child("products")

            val eventListener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val productList = mutableListOf<CartProducts>()

                    for (product in snapshot.children) {
                        val item = product.getValue(CartProducts::class.java)

                        val id = product.child("itemId").getValue(Int::class.java)

                        if (item != null && id != itemId) {
                            productList.add(item)
                        }
                    }
                    trySend(productList)
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
                            if (
                                productVariant.variantName?.contains(
                                    searchQuery,
                                    ignoreCase = true
                                ) == true ||
                                productVariant.color.contains(searchQuery, ignoreCase = true)
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

    override suspend fun getProductsStockQuantity(
        productId: String,
        color: String,
        size: String
    ): Flow<Int> = callbackFlow {
        val listener = database.getReference("Admin").child("AllProducts").child(productId)
            .child("productVariants").child(color).child("sizeDetails").child(size)

        val eventListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stock = snapshot.child("stock").getValue(Int::class.java)

                if (stock != null) {
                    trySend(stock)
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
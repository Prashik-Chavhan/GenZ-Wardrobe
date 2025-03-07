package com.pc.genzwardrobe.ui.presentation.cart_screen

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.pc.genzwardrobe.core.data.CartProductImpl
import com.pc.genzwardrobe.core.data.PaymentRepositoryImpl
import com.pc.genzwardrobe.core.data.PreferenceDatastoreImpl
import com.pc.genzwardrobe.core.data.ProductRepositoryImpl
import com.pc.genzwardrobe.core.data.WishlistProductImpl
import com.pc.genzwardrobe.core.domain.OrderedProducts
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProducts
import com.pc.genzwardrobe.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

sealed class PaymentState {
    data object Idle : PaymentState()
    data object Loading : PaymentState()
    data class Success(val paymentId: String) : PaymentState()
    data class Error(val message: String) : PaymentState()
}

sealed class WishlistUiState {
    data object Idle : WishlistUiState()
    data object Loading : WishlistUiState()
    data class Success( val items: List<WishlistProducts> ) : WishlistUiState()
    data class Error( val message: String ) : WishlistUiState()
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val cartProductImpl: CartProductImpl,
    private val wishlistProductImpl: WishlistProductImpl,
    private val preferenceDatastoreImpl: PreferenceDatastoreImpl,
    private val paymentRepositoryImpl: PaymentRepositoryImpl,
    private val productRepositoryImpl: ProductRepositoryImpl
) : ViewModel() {

    private val _getAllCartProducts = MutableStateFlow<List<CartProducts>>(emptyList())
    val getAllCartProducts = _getAllCartProducts.asStateFlow()

    private val _getAllWishlistProducts = MutableStateFlow<WishlistUiState>(WishlistUiState.Idle)
    val getAllWishlistProducts = _getAllWishlistProducts.asStateFlow()

    private val _getCartProductById = MutableStateFlow<List<CartProducts>>(emptyList())
    val getCartProductById = _getCartProductById.asStateFlow()

    private val _selectedAddressId = MutableStateFlow(0)
    val selectedAddressId: StateFlow<Int> = _selectedAddressId.asStateFlow()

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState = _paymentState.asStateFlow()

    val getTotalOriginalPrice = cartProductImpl.getTotalOriginalPrice()
    val getTotalDiscountedPrice = cartProductImpl.getTotalDiscountedPrice()

    val getWishlistMaxItemId = wishlistProductImpl.getMaxItemId()
    val getCartMaxItemId = cartProductImpl.getMaxItemId()

    private val _getProductCurrentStock = MutableStateFlow(0)
    val getProductCurrentStock = _getProductCurrentStock.asStateFlow()

    init {
        getAllCartProducts()
    }

    fun getProductStock(
        productId: String,
        color: String,
        size: String
    ) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getProductsStockQuantity(productId, color, size).collect {
                    _getProductCurrentStock.value = it
                }
            } catch (e: Exception) {
                Log.e("Current Stock", "Error: ${e.message}")
            }
        }
    }

    fun deleteWishlistItem(wishlistProducts: WishlistProducts) {
        viewModelScope.launch {
            wishlistProductImpl.deleteItem(wishlistProducts)
        }
    }
    fun getAllWishListProducts() {
        _getAllWishlistProducts.value = WishlistUiState.Loading

        viewModelScope.launch {
            try {
                wishlistProductImpl.getAllWishlistItem().collect {
                    delay(3000L)
                    _getAllWishlistProducts.value = WishlistUiState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getAllWishlistProducts.value = WishlistUiState.Error("Error while fetch all wishlist products")
            }
        }
    }

    init {
        viewModelScope.launch {
            _selectedAddressId.value = preferenceDatastoreImpl.getSelectedAddressId()
        }
    }

    fun getItemId(
        variantId: String,
        variantSize: String,
        variantColor: String
    ): Flow<Int> {
        return cartProductImpl.getItemId(
            variantId = variantId, variantSize = variantSize, variantColor = variantColor
        ).map {
            it ?: -1
        }
    }

    fun startPayment(activity: Activity, orderAmount: Int, email: String, phoneNumber: String) {
        viewModelScope.launch {
            _paymentState.value = PaymentState.Loading
            try {
                paymentRepositoryImpl.startPayment(
                    activity = activity,
                    orderAmount = orderAmount,
                    email = email,
                    phoneNumber = phoneNumber
                )
            } catch (e: Exception) {
                e.printStackTrace()
                _paymentState.value = PaymentState.Error("Failed to start payment")
                Log.d("Payment", "ViewModel Catch Block Payment Unsuccessful: ${e.message}")
            }
        }
    }

    fun startPaymentSuccess(paymentId: String) {
        _paymentState.value = PaymentState.Success(paymentId)
        Log.d("Payment", "ViewModel state updated to Success with ID: $paymentId")
    }

    fun startPaymentError(message: String) {
        _paymentState.value = PaymentState.Error(message)
        Log.d("Payment", "ViewModel state updated to Error with message: $message")
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }

    fun afterSuccessFullPayment(
        itemId: Int?,
        orderedProducts: OrderedProducts
    ) {
        viewModelScope.launch {
            database.getReference("Admin")
                .child("OrderedProducts")
                .child("${Utils.getCurrentUserId()}")
                .child(orderedProducts.orderId?.substring(1)!!)
                .setValue(orderedProducts)


            if (itemId == -1) {
                clearRoomDatabaseAfterPayment()
            } else if (itemId != null) {
                deleteProductFromCartById(itemId)
            }

            for (cartProducts in orderedProducts.products!!) {
                updateStock(cartProducts)
            }
        }
    }

    private fun updateStock(cartProduct: CartProducts) {
        viewModelScope.launch {

            // Paths
            val paths = listOf(
                "Admin/AllProducts/${cartProduct.variantId}/productVariants/${cartProduct.variantColor}/sizeDetails/${cartProduct.size}/stock",
                "Admin/ProductByGender/${cartProduct.productGender}/${cartProduct.variantId}/productVariants/${cartProduct.variantColor}/sizeDetails/${cartProduct.size}/stock",
                "Admin/ProductGenderCategoryType/${cartProduct.productGender}/${cartProduct.productCategory}/${cartProduct.productType}/${cartProduct.variantId}/productVariants/${cartProduct.variantColor}/sizeDetails/${cartProduct.size}/stock"
            )

            // For collecting successfully updated path
            val successfulPaths = mutableListOf<String>()

            try {
                val results = paths.map { path ->
                    // Using async to minus stocks for all paths simultaneously
                    async {
                        val success = updateStockTransaction(path = path, quantity = cartProduct.productQuantity!!)

                        if (success) successfulPaths.add(path)
                        success
                    }
                }.awaitAll()

                // Check if any path fails to update stock
                if (false in results) {
                    // Roll back successful transactions to restore stock
                    Log.e("Update Stock", "Stock update failed for at least one path, rolling back changes...")
                    rollbackStock(successfulPaths, cartProduct.productQuantity!!)
                } else {
                    Log.d("Update Stock", "Stock updated successfully for all paths")
                }
            } catch (e: Exception) {
                Log.e("Update Stock", "Stock update failed: ${e.message}")
                rollbackStock(successfulPaths, cartProduct.productQuantity!!)
            }
        }
    }

    private fun rollbackStock(
        successfulPaths: List<String>,
        quantity: Int
    ) {
        // Iterates through successful paths and runs a firebase transaction to restore stock
        successfulPaths.forEach { path ->
            database.getReference(path).runTransaction(object : Transaction.Handler {
                override fun doTransaction(currentData: MutableData): Transaction.Result {
                    // Get current stock
                    val currentStock = currentData.getValue(Int::class.java) ?: 0
                    // Add deducted productQuantity to current stock for restoring
                    currentData.value = currentStock + quantity // Restore the stock
                    return Transaction.success(currentData)
                }

                override fun onComplete(error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?) {
                    if (error != null) {
                        Log.e("RollbackStock", "Rollback failed at path: $path - ${error.message}")
                    } else {
                        Log.d("RollbackStock", "Rollback successful at path: $path")
                    }
                }
            })
        }
    }


    private suspend fun updateStockTransaction(
        path: String,
        quantity: Int
    ): Boolean {
        val ref = database.getReference(path)

        return suspendCancellableCoroutine { continuation ->
            ref.runTransaction(
                object : Transaction.Handler {
                    override fun doTransaction(currentData: MutableData): Transaction.Result {
                        val currentStock = currentData.getValue(Int::class.java) ?: 0

                        return if (currentStock < quantity) {
                            Log.e("Update Stock", "Insufficient stock at $path")
                            Transaction.success(currentData)
                        } else {
                            currentData.value = currentStock - quantity
                            Transaction.success(currentData)
                        }
                    }

                    override fun onComplete(
                        error: DatabaseError?,
                        committed: Boolean,
                        currentData: DataSnapshot?
                    ) {
                        if (error != null) {
                            Log.e("Update Stock", "Transaction failed: ${error.message}")
                            continuation.resume(false)
                        } else {
                            Log.d("Update Stock", "Transaction successful: $committed")
                            continuation.resume(true)
                        }
                    }
                }
            )
        }
    }

    private suspend fun clearRoomDatabaseAfterPayment() {
        cartProductImpl.deleteAllCartProducts()
    }

    fun saveSelectedAddressId(addressId: Int) {
        _selectedAddressId.value = addressId
        updateSelectedAddressIdInDatastore(addressId)
    }

    fun updateSelectedAddressId(newAddressId: Int) {
        _selectedAddressId.value = newAddressId
        updateSelectedAddressIdInDatastore(newAddressId)
    }

    private fun updateSelectedAddressIdInDatastore(addressId: Int) {
        viewModelScope.launch {
            try {
                preferenceDatastoreImpl.saveSelectedAddressId(addressId)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("DataStore", "Error: ${e.message}")
            }
        }
    }

    private fun getAllCartProducts() {
        viewModelScope.launch {
            try {
                cartProductImpl.getAllCartProducts().collect {
                    _getAllCartProducts.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("CartProducts", "Error while getting cart products ${e.message}")
            }
        }
    }

    fun getCartProduct(itemId: Int) {
        viewModelScope.launch {
            try {
                cartProductImpl.getProductById(itemId).collect {
                    Log.d("ItemId", "Fetching $itemId")
                    _getCartProductById.value = listOfNotNull(it)
                    Log.d("ItemId", "Fetched $it")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("CartProducts", "Error while getting cart product ${e.message}")
            }
        }
    }

    fun isProductInCart(
        productId: String,
        color: String,
        size: String
    ): Boolean {
        return getAllCartProducts.value.any {
            it.variantId == productId &&
                    it.variantColor == color &&
                    it.size == size
        }
    }

    private fun isProductDuplicate(
        cartItems: List<CartProducts>,
        productId: String,
        color: String,
        size: String
    ): Boolean {
        return cartItems.any {
            it.variantId == productId &&
                    it.variantColor == color &&
                    it.size == size
        }
    }

    fun addProductInCart(cartProducts: CartProducts) {

        val isDuplicate = isProductDuplicate(
            _getAllCartProducts.value,
            cartProducts.variantId!!,
            cartProducts.variantColor!!,
            cartProducts.size
        )
        viewModelScope.launch {
            try {
                if (!isDuplicate) {
                    cartProductImpl.insertCartProduct(cartProducts)
                } else {
                    Log.d("CartProduct", "Item is already in cart")
                }
            } catch (e: Exception) {
                Log.d("CartProduct", "error")
            }
        }
    }

    fun addProductInWishlist(wishlistProducts: WishlistProducts) {
        viewModelScope.launch {
            try {
                wishlistProductImpl.insertItem(wishlistProducts)
            } catch (e: Exception) {
                Log.e("Wishlist", "Error: ${e.message}")
            }
        }
    }

    fun updateCartProductQuantity(
        productId: String,
        variantColor: String,
        variantSize: String,
        newQuantity: Int
    ) {
        viewModelScope.launch {
            cartProductImpl.updateCartProduct(productId, variantColor, variantSize, newQuantity)
        }
    }

    fun deleteProductFromCartById(itemId: Int) {
        viewModelScope.launch {
            cartProductImpl.deleteCartProductById(itemId)
        }
    }
}
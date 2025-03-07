package com.pc.genzwardrobeadmin.presentation

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pc.genzwardrobeadmin.core.data.ProductRepositoryImpl
import com.pc.genzwardrobeadmin.core.data.WalletRepositoryImpl
import com.pc.genzwardrobeadmin.core.domain.OrderedProducts
import com.pc.genzwardrobeadmin.core.domain.Transactions
import com.pc.genzwardrobeadmin.core.domain.product.Product
import com.pc.genzwardrobeadmin.core.domain.product.ProductVariant
import com.pc.genzwardrobeadmin.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

sealed class OrderedProductUiState {
    data object Loading : OrderedProductUiState()
    data class Success(val products: List<OrderedProducts>) : OrderedProductUiState()
    data class Error(val message: String) : OrderedProductUiState()
}

sealed class AddProductUiState {
    data object Nothing : AddProductUiState()
    data object Loading : AddProductUiState()
    data object Success : AddProductUiState()
    data class Error(val message: String) : AddProductUiState()
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class MainViewModel @Inject constructor(
    private val database: FirebaseDatabase,
    private val productRepositoryImpl: ProductRepositoryImpl,
    private val walletRepositoryImpl: WalletRepositoryImpl
) : ViewModel() {

    private val _selectedProductImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedProductImages = _selectedProductImages.asStateFlow()

    private val _selectedImage = MutableStateFlow<Uri?>(null)
    val selectedImage = _selectedImage.asStateFlow()

    private val _addProductUiState = MutableStateFlow<AddProductUiState>(AddProductUiState.Nothing)
    val addProductUiState = _addProductUiState.asStateFlow()

    private val _productVariantByCategory = MutableStateFlow<PagingData<ProductVariant>>(PagingData.empty())
    val productVariantByCategory = _productVariantByCategory.asStateFlow().cachedIn(viewModelScope)

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _searchedVariants =
        MutableStateFlow<List<Pair<String, ProductVariant>>>(emptyList())
    val searchVariants = _searchedVariants.asStateFlow()

//    private val _allProductVariants =
//        MutableStateFlow<ProductVariantUiState>(ProductVariantUiState.Loading)
//    val allProductVariants = _allProductVariants.flatMapLatest {
//        productRepositoryImpl.fetchAllVariants().map {
//            ProductVariantUiState.Success(it)
//        }
//    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ProductVariantUiState.Loading)

    private val _orderedProducts =
        MutableStateFlow<PagingData<Triple<String, String, List<String>>>>(PagingData.empty())
    val orderedProducts = _orderedProducts.asStateFlow()

    private val _orderedCartProducts =
        MutableStateFlow<OrderedProductUiState>(OrderedProductUiState.Loading)
    val orderedCartProducts = _orderedCartProducts.asStateFlow()

    private val _userWalletData = MutableStateFlow(0)
    val userWalletData = _userWalletData.asStateFlow()

    private val _productVariants = mutableStateMapOf<String, ProductVariant>()
    val productVariants: SnapshotStateMap<String, ProductVariant> = _productVariants


    private val searchJob: Job? = null

    init {
        getOrderedProducts()
    }

    fun addProductVariant(variantColor: String, productVariant: ProductVariant) {
        _productVariants[variantColor] = productVariant
    }

    fun clearProductVariants() {
        _productVariants.clear()
    }

    fun updateSearchQuery(searchQuery: String) {
        _searchQuery.value = searchQuery

        searchJob?.cancel()

        viewModelScope.launch {
            delay(800)
            fetchSearchedVariants(searchQuery)
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    private fun fetchSearchedVariants(searchQuery: String) {
        viewModelScope.launch {
            productRepositoryImpl.getSearchedVariants(searchQuery).collect {
                _searchedVariants.value = it
            }
        }
    }

    fun updateOrderStatus(
        userId: String,
        orderId: String,
        itemId: Int,
        orderStatus: Int
    ) {
        viewModelScope.launch {
            productRepositoryImpl.updateOrderStatus(userId, orderId, itemId, orderStatus)
        }
    }

    fun getOrderedCartProducts(userId: String, orderId: String) {
        _orderedCartProducts.value = OrderedProductUiState.Loading
        viewModelScope.launch {
            try {
                productRepositoryImpl.getOrderedProductsDetails(userId, orderId).collect {
                    _orderedCartProducts.value = OrderedProductUiState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _orderedCartProducts.value = OrderedProductUiState.Error("Error: ${e.message}")
            }
        }
    }

    private fun getOrderedProducts() {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getUserIdsAndOrderIds().cachedIn(viewModelScope)
                    .collectLatest {
                        _orderedProducts.value = it
                        Log.d("orders", "Successfully loaded: $it")
                    }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("orders", "Error: ${e.message}")
            }
        }
    }

    fun selectImage(uri: Uri?) {
        _selectedImage.value = uri
    }

    fun clearImage() {
        _selectedImage.value = null
    }

    fun selectProductImages(uris: List<Uri>) {
        _selectedProductImages.value += uris
    }

    fun clearImages() {
        _selectedProductImages.value = emptyList()
    }

    fun removeImage(uris: Uri) {
        _selectedProductImages.value = _selectedProductImages.value.filter { it != uris }
    }

    fun addProduct(product: Product) {
        _addProductUiState.value = AddProductUiState.Loading

        viewModelScope.launch {
            database.getReference("Admin").child("AllProducts").child("${product.productId}")
                .setValue(product)
                .addOnSuccessListener {
                    database.getReference("Admin").child("ProductByGender").child("${product.productGender}")
                        .child("${product.productId}")
                        .setValue(product)
                        .addOnSuccessListener {
                            database.getReference("Admin").child("ProductGenderCategoryType").child("${product.productGender}")
                                .child("${product.productCategory}")
                                .child("${product.productType}")
                                .child("${product.productId}").setValue(product)
                        }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProductUploadError", "Failed to save product: ${exception.message}")
                    _addProductUiState.value = AddProductUiState.Error("Something happened wrong")
                }
        }
    }

    suspend fun uploadVariantImages(
        image: Uri,
        folder1: String,
        folder2: String,
        folder3: String
    ): String? {
        return try {
            withContext(Dispatchers.IO) {
                val imageRef = FirebaseStorage.getInstance().reference
                    .child(Utils.getCurrentAdminId())
                    .child(folder1)
                    .child(folder2)
                    .child(folder3)
                    .child(UUID.randomUUID().toString())

                val uploadTask = imageRef.putFile(image)

                val downloadUrl = Tasks.await(
                    uploadTask.continueWithTask {
                        imageRef.downloadUrl
                    }
                ).toString()

                downloadUrl
            }
        } catch (e: Exception) {
            Log.d("ImageUploading", "Failed while uploading image ${e.message}")
            null
        }
    }

    fun fetchProductVariantsByGender(gender: String) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.fetchVariantsByGender(gender).collect {
                    _productVariantByCategory.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getUserWalletAmount(userId: String) {
        viewModelScope.launch {
            try {
                walletRepositoryImpl.getUsersWalletAmount(userId).collect {
                    _userWalletData.value = it
                    Log.d("Wallet Data", "Successful: $it")
                }
            } catch (e: Exception) {
                Log.e("Wallet Data", "Error: ${e.message}")
            }
        }
    }

    fun addRefundAmount(
        userId: String,
        amount: Int
    ) {
        database.getReference("AllUsers").child(userId).child("wallet").child("amount")
            .setValue(amount)
    }

    fun addTransaction(
        userId: String,
        transactions: Transactions
    ) {
        database.getReference("AllUsers")
            .child(userId)
            .child("wallet")
            .child("transactions")
            .child(Utils.generateRandomId())
            .setValue(transactions)
    }
}
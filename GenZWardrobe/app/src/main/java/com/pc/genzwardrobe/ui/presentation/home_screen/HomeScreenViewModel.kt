@file:OptIn(ExperimentalCoroutinesApi::class)

package com.pc.genzwardrobe.ui.presentation.home_screen

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.pc.genzwardrobe.core.data.LocationRepositoryImpl
import com.pc.genzwardrobe.core.data.OrderReviewRepositoryImpl
import com.pc.genzwardrobe.core.data.ProductRepositoryImpl
import com.pc.genzwardrobe.core.domain.PersonalInfo
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.core.domain.UserReview
import com.pc.genzwardrobe.core.domain.products.Product
import com.pc.genzwardrobe.core.domain.products.ProductHighlight
import com.pc.genzwardrobe.core.domain.products.ProductVariant
import com.pc.genzwardrobe.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID
import javax.inject.Inject

sealed class ProductVariantUiState {
    data object Loading : ProductVariantUiState()
    data class Success(val productVariant: List<ProductVariant>) : ProductVariantUiState()
    data class Error(val message: String) : ProductVariantUiState()
}

sealed class LocationState {
    data object Idle : LocationState()
    data object Loading : LocationState()
    data class Success(val pincode: String, val city: String, val state: String) : LocationState()
    data class Error(val message: String) : LocationState()
}

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val productRepositoryImpl: ProductRepositoryImpl,
    private val database: FirebaseDatabase,
    private val locationRepositoryImpl: LocationRepositoryImpl,
    private val orderReviewRepositoryImpl: OrderReviewRepositoryImpl
) : ViewModel() {

    private val _allProductVariants =
        MutableStateFlow<ProductVariantUiState>(ProductVariantUiState.Loading)
    val allProductVariant = _allProductVariants.flatMapLatest {
        productRepositoryImpl.fetchAllProductVariants().map { productVariant ->
            ProductVariantUiState.Success(productVariant)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), ProductVariantUiState.Loading)

    private val _productVariantsByGender =
        MutableStateFlow<ProductVariantUiState>(ProductVariantUiState.Loading)
    val productVariantsByGender = _productVariantsByGender.asStateFlow()

    private val _pagingProductVariantsByGender =
        MutableStateFlow<PagingData<Triple<String, String, ProductVariant>>>(PagingData.empty())
    val pagingProductVariantsByGender = _pagingProductVariantsByGender.asStateFlow().cachedIn(viewModelScope)

    private val _productDetails = MutableStateFlow<Product?>(null)
    val productDetails = _productDetails.asStateFlow()

    private val _getUserLocation = MutableStateFlow<LocationState>(LocationState.Idle)
    val getUserLocation = _getUserLocation.asStateFlow()

    private val _getAllUserAddress = MutableStateFlow<List<UserAddress>>(emptyList())
    val getAllUserAddress = _getAllUserAddress.asStateFlow()

    private val _getProductHighlight = MutableStateFlow<List<ProductHighlight>>(emptyList())
    val getProductHighlight = _getProductHighlight.asStateFlow()

    private val _getSimilarVariants =
        MutableStateFlow<List<Triple<String, String, ProductVariant>>>(emptyList())
    val getSimilarVariants = _getSimilarVariants.asStateFlow()

    private val _getProductReviews = MutableStateFlow<PagingData<Pair<String, UserReview>>>(PagingData.empty())
    val getProductReviews = _getProductReviews.asStateFlow().cachedIn(viewModelScope)

    private val _searchedVariants = MutableStateFlow<List<Pair<String, ProductVariant>>>(emptyList())
    val searchedVariants = _searchedVariants.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private var searchJob: Job? = null

    private val _selectedUserImage = MutableStateFlow<Uri?>(null)
    val selectedUserImage = _selectedUserImage.asStateFlow()

    private val _selectedProductReviewImages = MutableStateFlow<List<Uri>>(emptyList())
    val selectedProductReviewImages = _selectedProductReviewImages.asStateFlow()

    private val _getReviewingProduct = MutableStateFlow<Product?>(null)
    val getReviewingProduct = _getReviewingProduct.asStateFlow()

    init {
        getAllUserAddress()
    }

    fun getReviewItem(productId: String) {
        viewModelScope.launch {
            try {
                orderReviewRepositoryImpl.getProductForReview(productId).collect {
                    _getReviewingProduct.value = it
                }
            } catch (e: Exception) {
                Log.e("Error", "Error: ${e.message}")
            }
        }
    }

    fun selectReviewImages(uris: List<Uri>) {
        _selectedProductReviewImages.value += uris
    }

    suspend fun uploadReviewImages(
        imageUri: Uri
    ): String? {
        return try {
            withContext(Dispatchers.IO) {
                val images = FirebaseStorage.getInstance()
                    .reference
                    .child("userReviews")
                    .child("${Utils.getCurrentUserId()}")
                    .child(UUID.randomUUID().toString())

                val uploadTask = images.putFile(imageUri)

                val downloadUrl = Tasks.await(
                    uploadTask.continueWithTask {
                        images.downloadUrl
                    }
                ).toString()

                downloadUrl
            }
        } catch (e: Exception) {
            Log.d("ImageUploading", "Failed while uploading image ${e.message}")
            null
        }
    }

    fun addReview(
        productId: String,
        productGender: String,
        productCategory: String,
        productType: String,
        color: String,
        userReview: UserReview
    ) {
        viewModelScope.launch {
            database.getReference("Admin")
                .child("AllProducts")
                .child(productId)
                .child("userReviews")
                .child(Utils.getCurrentUserId().toString())
                .child(color)
                .setValue(userReview)
                .addOnSuccessListener {
                    database.getReference("Admin")
                        .child("ProductByGender")
                        .child(productGender)
                        .child(productId)
                        .child("userReviews")
                        .child(Utils.getCurrentUserId().toString())
                        .child(color)
                        .setValue(userReview)
                        .addOnSuccessListener {
                            database.getReference("Admin")
                                .child("ProductGenderCategoryType")
                                .child(productGender)
                                .child(productCategory)
                                .child(productType)
                                .child(productId)
                                .child("userReviews")
                                .child(Utils.getCurrentUserId().toString())
                                .child(color)
                                .setValue(userReview)
                        }
                        .addOnFailureListener {
                            Log.e("Add Review", "Failed to add review")
                        }
                }
                .addOnFailureListener {
                    Log.e("Add Review", "Failed to add review")
                }
        }
    }

    fun clearReviewImages() {
        _selectedProductReviewImages.value = emptyList()
    }
    fun clearUserImage() {
        _selectedUserImage.value = null
    }

    fun selectUserImage(imageUri: Uri?) {
        _selectedUserImage.value = imageUri
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query

        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(400)
            getSearchedItems(query)
        }
    }

    private fun getSearchedItems(query: String){
        viewModelScope.launch {
            productRepositoryImpl
                .getSearchedVariants(query)
                .collect {
                    _searchedVariants.value = it
                    Log.d("Searching", "Searched items: ${it.size}")
                }
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
    }
    fun fetchProductHighlight(productId: String) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getProductHighlight(productId).collect {
                    _getProductHighlight.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ProductHighlight", "Error: ${e.message}")
            }
        }
    }

    fun getProductReviews(productId: String, sortBy: String) {
        viewModelScope.launch {
            try {
                orderReviewRepositoryImpl.getProductReviews(productId, sortBy).collect {
                    _getProductReviews.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Reviews", "Error: ${e.message}")
            }
        }
    }

    fun clearReviewsFlow() {
        _getProductReviews.value = PagingData.empty()
    }

    fun getSimilarVariants(
        color: String,
        gender: String,
        category: String,
        type: String
    ) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getSimilarProducts(color, gender, category, type).collect {
                    _getSimilarVariants.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("SimilarVariants", "Error: ${e.message}")
            }
        }
    }

    fun resetLocationState() {
        _getUserLocation.value = LocationState.Idle
    }
    fun getUsersLocation() {
        _getUserLocation.value = LocationState.Loading
        viewModelScope.launch {
            try {
                val location = locationRepositoryImpl.getUsersCurrentCoordinates()
                Log.d(
                    "LocationViewmodel",
                    "Got users current lat = ${location?.first} and long = ${location?.second}"
                )
                if (location != null) {
                    val address = locationRepositoryImpl.getUsersCurrentLocation(
                        location.first,
                        location.second
                    )
                    Log.d("LocationViewmodel", "Got users address when location is not null")
                    _getUserLocation.value = LocationState.Success(
                        pincode = address.pincode,
                        city = address.city,
                        state = address.state
                    )
                    Log.d(
                        "LocationViewmodel",
                        "Pincode: ${address.pincode}, City: ${address.city}, state: ${address.state}"
                    )
                } else {
                    _getUserLocation.value = LocationState.Error("Location not found")
                    Log.d("LocationViewmodel", "Location is null")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _getUserLocation.value = LocationState.Error("Error: ${e.message}")
            }
        }
    }

    fun fetchProductVariantsByGender(productGender: String) {
        viewModelScope.launch {
            _productVariantsByGender.value = ProductVariantUiState.Loading

            try {
                productRepositoryImpl.fetchProductsByGender(productGender).collect {
                    _productVariantsByGender.value = ProductVariantUiState.Success(it)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ProductVariantsByGender", "${e.message}")
            }
        }
    }

    fun fetchPagingProductVariants(
        gender: String,
        category: String,
        type: String,
        sortBy: String,
        minPrice: Int?,
        maxPrice: Int?,
        selectedDiscount: List<String>,
        selectedFabric: Set<String>,
        selectedOccasion: Set<String>,
        selectedColor: Set<String>,
    ) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getByPaging(
                    gender, category, type,
                    sortBy, minPrice, maxPrice,
                    selectedDiscount, selectedFabric,
                    selectedOccasion, selectedColor
                ).collect {
                    _pagingProductVariantsByGender.value = it
                    Log.d("VMPaging", "Fetched products ${ it.map { size -> size.first }}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("ProductVariantsByGender", "${e.message}")
            }
        }
    }

//    fun fetchPagingProductVariantsByGenderType(
//        gender: String,
//        category: String,
//        type: String,
//        sortBy: String
//    ) {
//        viewModelScope.launch {
//            try {
//                productRepositoryImpl.getProductsByGenderCategoryType(gender, category, type, sortBy)
//                    .collect {
//                        _pagingProductVariantsByGenderType.value = it
//                    }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                Log.d("ProductVariantsByGenderType", "${e.message}")
//            }
//        }
//    }

    fun getProductDetails(productId: String) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.productDetailsScreen(productId).collect {
                    _productDetails.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveUserAddressInFirebase(
        userAddress: UserAddress
    ) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("userAddress")
            .child("${userAddress.id}").setValue(userAddress)
    }

    private fun getAllUserAddress() {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getAllUserAddress().collect {
                    _getAllUserAddress.value = it
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteUserAddress(addressId: Int) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("userAddress")
            .child("$addressId").removeValue()
    }

    fun editUserAddress(addressId: Int, updatedData: Map<String, Any>) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("userAddress")
            .child("$addressId").updateChildren(updatedData)
    }

    suspend fun saveUserImage(imageUri: Uri): String? {
        return try {
            withContext(Dispatchers.IO) {
                val imageRef = FirebaseStorage.getInstance().reference
                    .child("${Utils.getCurrentUserId()}")
                    .child(UUID.randomUUID().toString())

                val uploadTask = imageRef.putFile(imageUri)

                val downloadUrl = Tasks.await(
                    uploadTask.continueWithTask {
                        imageRef.downloadUrl
                    }
                ).toString()

                downloadUrl
            }
        } catch (e: Exception) {
            Log.e("ImageError", "Error while uploading image: ${e.message}")
            null
        }
    }

    fun savePersonalInfo(personalInfo: PersonalInfo) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("personalInfo").setValue(personalInfo)
    }

    fun logOut() {
        FirebaseAuth.getInstance().signOut()
    }

    fun canReviewProduct(productId: String, color: String, onSuccess: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                orderReviewRepositoryImpl.canReviewProduct(productId, color, onSuccess)
            } catch (e: Exception) {
                Log.e("Can Review", "Error: ${e.message}")
            }
        }
    }


}
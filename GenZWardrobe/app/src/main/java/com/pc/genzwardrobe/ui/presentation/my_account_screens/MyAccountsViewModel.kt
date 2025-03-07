package com.pc.genzwardrobe.ui.presentation.my_account_screens

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobe.core.data.ProductRepositoryImpl
import com.pc.genzwardrobe.core.data.UserRepositoryImpl
import com.pc.genzwardrobe.core.domain.OrderedProducts
import com.pc.genzwardrobe.core.domain.PersonalInfo
import com.pc.genzwardrobe.core.domain.Transactions
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.core.domain.Wallet
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.utils.Utils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class MyOrdersUiState {
    data object Loading : MyOrdersUiState()
    data class Success(val itemList: List<OrderedProducts>) : MyOrdersUiState()
    data class Error(val message: String) : MyOrdersUiState()
}

sealed class WalletUiState {
    data object Loading : WalletUiState()
    data class Success(val data: Wallet) : WalletUiState()
    data class Error(val message: String) : WalletUiState()
}

sealed class AccountDeleteUiState {
    data object Nothing : AccountDeleteUiState()
    data object Loading : AccountDeleteUiState()
    data object CodeSent : AccountDeleteUiState()
    data object VerificationCompleted : AccountDeleteUiState()
    data object Error : AccountDeleteUiState()
}

@HiltViewModel
class MyAccountsViewModel @Inject constructor(
    private val productRepositoryImpl: ProductRepositoryImpl,
    private val userRepositoryImpl: UserRepositoryImpl,
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _myOrders = MutableStateFlow<MyOrdersUiState>(MyOrdersUiState.Loading)
    val myOrders = _myOrders.asStateFlow()

    private val _myOrderById = MutableStateFlow<Pair<CartProducts, UserAddress>?>(null)
    val myOrderById = _myOrderById.asStateFlow()

    private val _otherOrders = MutableStateFlow<List<CartProducts>>(emptyList())
    val otherOrders = _otherOrders.asStateFlow()

    private val _walletData = MutableStateFlow<WalletUiState>(WalletUiState.Loading)
    val walletData = _walletData.asStateFlow()

    private val _walletAmount = MutableStateFlow<Int?>(null)
    val walletAmount = _walletAmount.asStateFlow()

    private val _userInfo = MutableStateFlow<PersonalInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _deleteAccount =
        MutableStateFlow<AccountDeleteUiState>(AccountDeleteUiState.Nothing)
    val deleteAccount = _deleteAccount.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId = _verificationId.asStateFlow()

    init {
        getMyOrders()
        getWalletAmount()
        getWallet()
        getUserInfo()
    }

    fun updateOrderStatus(orderId: String, itemId: Int, orderStatus: Int) {
        viewModelScope.launch {
            val listener = database.getReference("Admin")
                .child("OrderedProducts")
                .child("${Utils.getCurrentUserId()}")
                .child(orderId)
                .child("products")

            val snapshot = listener.get().await()

            withContext(Dispatchers.IO) {
                for (product in snapshot.children) {

                    val id = product.child("itemId").getValue(Int::class.java)

                    if (id == itemId) {
                        product.ref.child("orderStatus").setValue(orderStatus)
                    }
                }
            }
        }
    }

    private fun getMyOrders() {
        viewModelScope.launch {
            _myOrders.value = MyOrdersUiState.Loading
            Log.d("MyOrders", "State is Loading")
            try {
                productRepositoryImpl.getALLMyOrderProducts().collect {
                    Log.d("MyOrders", "Fetching items")
                    _myOrders.value = MyOrdersUiState.Success(it)
                    Log.d("MyOrders", "Fetched items: ${it.size}")
                }
            } catch (e: CancellationException) {
                Log.d("MyOrders", "Operation cancelled")
//                _myOrders.value = MyOrdersUiState.Error("Operation cancelled")
            } catch (e: Exception) {
                Log.e("MyOrders", "Failed to fetch items: ${e.message}")
                _myOrders.value = MyOrdersUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun getOrderDetails(orderId: String, itemId: Int) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getSelectedMyOrder(orderId, itemId).collect {
                    _myOrderById.value = it
                    Log.d(
                        "orderDetails",
                        "Successful: (${it.first.variantName} / ${it.second.name})"
                    )
                }
            } catch (e: CancellationException) {
                Log.d("orderDetails", "Operation cancelled")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("orderDetails", "Error: ${e.message}")
            }
        }
    }

    fun getOtherOrders(orderId: String, itemId: Int) {
        viewModelScope.launch {
            try {
                productRepositoryImpl.getOtherMyOrder(orderId, itemId).collect {
                    _otherOrders.value = it
                }
            } catch (e: Exception) {
                Log.e("order", "Error: ${e.message}")
            }
        }
    }

    private fun getWalletAmount() {
        _walletData.value = WalletUiState.Loading
        viewModelScope.launch {
            try {
                userRepositoryImpl.getWalletData().collect {
                    _walletData.value = WalletUiState.Success(it)
                    Log.d("Wallet", "Success: ${it.amount}, ${it.transactions}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _walletData.value = WalletUiState.Error("Error: ${e.message}")
            }
        }
    }

    fun getWallet() {
        viewModelScope.launch {
            userRepositoryImpl.getWalletData().collect {
                val amount = it.amount ?: 0
                _walletAmount.value = amount
            }
        }
    }

    fun addOrDeductAmountInWallet(amount: Int) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}")
            .child("wallet").child("amount").setValue(amount)
    }

    fun addTransaction(transactions: Transactions) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}")
            .child("wallet").child("transactions").child(Utils.generateRandomId())
            .setValue(transactions)
    }

    fun getUserInfo() {
        viewModelScope.launch {
            try {
                userRepositoryImpl.getUserInfo().collect {
                    _userInfo.value = it
                    Log.d("PersonalInfo", "${it.userFirstName}, ${it.userLastName}")
                }

            } catch (e: Exception) {
                Log.e("User", "Error: ${e.message}")
            }
        }
    }

    fun updateUserInfo(personalInfo: PersonalInfo) {
        database.getReference("AllUsers").child("${Utils.getCurrentUserId()}").child("personalInfo")
            .setValue(personalInfo)
    }

    fun deleteUserAccount() {
        val userData = database.getReference("AllUsers").child("${Utils.getCurrentUserId()}")
        val orderData = database.getReference("Admin").child("OrderedProducts")
            .child("${Utils.getCurrentUserId()}")

        userData.removeValue()
            .addOnSuccessListener {
                Log.d("AccountDelete", "User data deleted successfully")
                orderData.removeValue()

                    .addOnSuccessListener {
                        Log.d("AccountDelete", "Order data deleted successfully")

//                        FirebaseAuth.getInstance().currentUser?.delete()
//                            ?.addOnSuccessListener {
//                                Log.d("AccountDelete", "Account deleted successfully")
//                            }
//                            ?.addOnFailureListener {
//                                Log.e("AccountDelete", "Failed to delete account")
//                            }
                    }
                    .addOnFailureListener {
                        Log.e("AccountDelete", "Failed to delete order data")
                    }
            }
            .addOnFailureListener {
                Log.e("AccountDelete", "Failed to delete user data")
            }
    }

    fun sendOtpForReAuthentication(phoneNumber: String, activity: Activity) {
        _deleteAccount.value = AccountDeleteUiState.Loading
        viewModelScope.launch {
            val option = PhoneAuthOptions.newBuilder(FirebaseAuth.getInstance())
                .setPhoneNumber("+91$phoneNumber")
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            // Auto-retrieval or instant verification success
                            reAuthenticateAndDeleteUser(credential)
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Log.e("DeleteAccount", "OTP Verification failed: ${e.message}")
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            // Store verificationId for manual OTP input
                            Log.d("DeleteAccount", "OTP Sent. Verification ID: $verificationId")
                            // User needs to input OTP manually in UI
                            _deleteAccount.value = AccountDeleteUiState.CodeSent
                            _verificationId.value = verificationId
                        }
                    }
                )
                .build()
            PhoneAuthProvider.verifyPhoneNumber(option)
        }
    }

    fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId.value.toString(), otp)
        reAuthenticateAndDeleteUser(credential)
    }

    fun reAuthenticateAndDeleteUser(credential: PhoneAuthCredential) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.reauthenticate(credential)
            ?.addOnSuccessListener {
                deleteAuthAccount(user)
                _deleteAccount.value = AccountDeleteUiState.VerificationCompleted
            }
            ?.addOnFailureListener {
                Log.e("DeleteAccount", "Error: ${it.message}")
                _deleteAccount.value = AccountDeleteUiState.Error
            }
    }

    private fun deleteAuthAccount(user: FirebaseUser) {
        user.delete()
            .addOnSuccessListener {
                Log.d("DeleteAccount", "Successful")
            }
            .addOnFailureListener {
                Log.e("Delete", "Error: ${it.message}")
            }
    }
}
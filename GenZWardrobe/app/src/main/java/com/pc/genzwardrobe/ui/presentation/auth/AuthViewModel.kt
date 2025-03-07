package com.pc.genzwardrobe.ui.presentation.auth

import android.annotation.SuppressLint
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
import com.google.firebase.messaging.FirebaseMessaging
import com.pc.genzwardrobe.core.domain.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

sealed class AuthUiState {
    data object Nothing : AuthUiState()
    data object Loading : AuthUiState()
    data object CodeSent : AuthUiState()
    data class AlreadyLoggedIn(val message: String) : AuthUiState()
    data class VerificationCompleted(val user: FirebaseUser?) : AuthUiState()
    data object Error : AuthUiState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val database: FirebaseDatabase
) : ViewModel() {

    private val _state = MutableStateFlow<AuthUiState>(AuthUiState.Nothing)
    val state = _state.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    fun setPhoneNumber(newPhoneNumber: String) {
        _phoneNumber.value = newPhoneNumber
    }

    fun resetAuthUiState() {
        _state.value = AuthUiState.Nothing
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("AuthViewModel", "onVerificationCompleted:$credential")
            signInWithPhoneAuthCredential(credential)

        }

        override fun onVerificationFailed(e: FirebaseException) {
            Log.w("AuthViewModel", "onVerificationFailed", e)
            _state.value = AuthUiState.Error
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken,
        ) {
            _verificationId.value = verificationId
//            resendToken = token
            _state.value = AuthUiState.CodeSent
            Log.d("AuthViewModel", "onCodeSent:${_verificationId.value.toString()}")
        }
    }

    // 1 -> Send Otp
    fun onCodeSent(
        phoneNumber: String,
        activity: Activity
    ) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                database.getReference("AllUsers").orderByChild("userPhoneNumber")
                    .equalTo(phoneNumber)
                    .get()
                    .addOnSuccessListener { snapshot ->

                        // Check if phoneNumber already exists or not
                        if (snapshot.exists()) {

                            // If yes, then handle that user
                            val userId = snapshot.children.first().key
                            handleExistingUser(
                                userId,
                                phoneNumber = phoneNumber,
                                activity = activity
                            )
                        } else {
                            // Else, navigate to Otp Screen directly
                            initiateOtp(activity, phoneNumber)
                        }
                    }
                    .addOnFailureListener {
                        _state.value = AuthUiState.Error
                        Log.d("Login", "Error: ${it.message}")
                    }
            } catch (e: Exception) {
                _state.value = AuthUiState.Error
            }
        }
    }

    // 2 -> If User not exists
    private fun initiateOtp(
        activity: Activity,
        phoneNumber: String
    ) {
        viewModelScope.launch {
            val options = PhoneAuthOptions
                .newBuilder(auth)
                .setPhoneNumber("+91$phoneNumber") // Phone number to verify
                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                .setActivity(activity) // Activity (for callback binding)
                .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                .build()

            PhoneAuthProvider.verifyPhoneNumber(options)
        }
    }

    // 2 -> If user already exists
    private fun handleExistingUser(userId: String?, activity: Activity, phoneNumber: String) {

        // Get Current User Status
        val currentUser = auth.currentUser
        when {

            // If its null, then navigate to Otp screen
            currentUser == null -> {
                initiateOtp(activity = activity, phoneNumber = phoneNumber)
            }

            // If yes, then user is already logged in somewhere
            currentUser.uid == userId -> {
                _state.value = AuthUiState.AlreadyLoggedIn("User is already logged in")
                Log.d("Login", "User is already logged in")
            }

            // Else error
            else -> {
                // Different user is logged in
                _state.value = AuthUiState.Error
            }
        }
    }

    // 3 -> Verify Otp
    fun verifyOtp(otp: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId.value.toString(), otp)
        signInWithPhoneAuthCredential(credential)
    }

    // 4 -> Signing User
    @SuppressLint("SuspiciousIndentation")
    fun signInWithPhoneAuthCredential(
        credential: PhoneAuthCredential
    ) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = task.result?.user
                            registerOrLoginUser(user)
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("AuthViewModel", "signInWithCredential:failure", task.exception)
                            _state.value = AuthUiState.Error
                            // Update UI
                        }
                    }
//                }
            } catch (e: Exception) {
                _state.value = AuthUiState.Error
            }
        }
    }

    // 5 -> Register or Login User on conditions
    private fun registerOrLoginUser(user: FirebaseUser?) {
        user?.let { firebaseUser ->
            val userId = firebaseUser.uid

            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                val token = it.result
                database.getReference("AllUsers").child(userId)
                .get()
                .addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        val newUser = User(
                            userId = userId,
                            fcmToken = token,
                            userPhoneNumber = firebaseUser.phoneNumber
                        )
                        database.getReference("AllUsers")
                            .child(userId)
                            .setValue(newUser)
                            .addOnSuccessListener {
                                _state.value = AuthUiState.VerificationCompleted(user)
                            }
                            .addOnFailureListener { exception ->
                                _state.value = AuthUiState.Error
                                Log.d("Login", "Error: ${exception.message}")
                            }
                    } else {
                        _state.value = AuthUiState.VerificationCompleted(user)
                    }
                }
                .addOnFailureListener { exception ->
                    _state.value = AuthUiState.Error
                    Log.d("Login", "Error: ${exception.message}")
                }
            }
        }
    }
}
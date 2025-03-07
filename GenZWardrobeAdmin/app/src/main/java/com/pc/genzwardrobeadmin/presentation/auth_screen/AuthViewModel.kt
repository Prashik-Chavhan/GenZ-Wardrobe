package com.pc.genzwardrobeadmin.presentation.auth_screen

import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.pc.genzwardrobeadmin.core.domain.Admin
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
    data object CodeVerified : AuthUiState()
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

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            Log.d("AuthViewModel", "onVerificationCompleted:$credential")
            _state.value = AuthUiState.CodeVerified
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

    fun onCodeSent(
        phoneNumber: String,
        activity: Activity
    ) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val options = PhoneAuthOptions.newBuilder(auth)
                    .setPhoneNumber("+91$phoneNumber") // Phone number to verify
                    .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                    .setActivity(activity) // Activity (for callback binding)
                    .setCallbacks(callbacks) // OnVerificationStateChangedCallbacks
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
            } catch (e: Exception) {
                _state.value = AuthUiState.Error
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun signInWithPhoneAuthCredential(
        otp: String,
        admin: Admin
    ) {
        _state.value = AuthUiState.Loading
        viewModelScope.launch {
            try {
                val credential =
                    PhoneAuthProvider.getCredential(_verificationId.value.toString(), otp)

                auth.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("AuthViewModel", "signInWithCredential:success")
                            val currentUser = auth.currentUser
                            if (currentUser != null) {
                                val adminId = currentUser.uid
                                val updatedAdmin = admin.copy(adminUid = adminId)
                                database.getReference("Admin").child("AdminInfo").child(adminId)
                                    .setValue(updatedAdmin)
                                    .addOnSuccessListener {

                                        _state.value = AuthUiState.CodeVerified
                                    }.addOnFailureListener {
                                        _state.value = AuthUiState.Error
                                    }
                            }
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
}
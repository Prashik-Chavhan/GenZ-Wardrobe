package com.pc.genzwardrobe.ui.presentation.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pc.genzwardrobe.R
import com.pc.genzwardrobe.ui.presentation.my_account_screens.Otp_Text_Field
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    onVerifyClicked: () -> Unit,
    onEditButtonClicked: () -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state = authViewModel.state.collectAsState()
    val phoneNumber by authViewModel.phoneNumber.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    var otpValue by remember {
        mutableStateOf("")
    }

    var timeLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(timeLeft) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    val context = LocalContext.current
    val activity = context as Activity

    LaunchedEffect(key1 = state.value) {
        when (state.value) {
            is AuthUiState.VerificationCompleted -> {
                isLoading = false
                onVerifyClicked()
            }

            is AuthUiState.Error -> {
                Utils.showToast(context, "Something is wrong!!!")
                isLoading = false
            }

            else -> {}
        }
    }

    if (isLoading) {
        Auth_Loader()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
    ) {
        Image(
            painter = painterResource(R.drawable.verify_otp_image),
            contentDescription = "Verifying otp"
        )
        Text(
            text = "Verify Security Code",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )
        Text(
            text = "Verification code has been sent to your mobile number $phoneNumber",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Otp_Text_Field(
            otpValue = otpValue,
            onOtpValueChange = { newOtpValue ->
                if (newOtpValue.length <= 6) otpValue = newOtpValue
            }
        )
        Auth_Button(
            buttonText = "Verify Phone Number",
            onButtonClicked = {
                isLoading = true
                authViewModel.verifyOtp(otpValue)
            },
            enabled = otpValue.length == 6
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = { onEditButtonClicked() }
            ) {
                Text(
                    text = "Edit phone number ?"
                )
            }
            TextButton(
                onClick = {
                    if (timeLeft == 0) {
                        authViewModel.onCodeSent(phoneNumber, activity)
                        timeLeft = 60
                    }
                },
                enabled = timeLeft == 0
            ) {
                Text(
                    text = if (timeLeft == 0) "Resend OTP" else "Send Again(00:$timeLeft)"
                )
            }
        }
    }
}
package com.pc.genzwardrobeadmin.presentation.auth_screen

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pc.genzwardrobeadmin.R
import com.pc.genzwardrobeadmin.core.domain.Admin
import com.pc.genzwardrobeadmin.utils.Utils
import kotlinx.coroutines.delay

@Composable
fun OtpScreen(
    onVerifyClicked: () -> Unit,
    phoneNumber: String?,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val state = authViewModel.state.collectAsState()
    val verificationId by authViewModel.verificationId.collectAsState()

    var isLoading by remember { mutableStateOf(false) }

    var otpValue by remember {
        mutableStateOf("")
    }

    val context = LocalContext.current
    var timeLeft by remember { mutableIntStateOf(60) }

    LaunchedEffect(timeLeft) {
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
    }

    LaunchedEffect(key1 = state.value) {
        when (state.value) {
            is AuthUiState.CodeVerified -> {
                isLoading = false
                onVerifyClicked()
            }

            is AuthUiState.Error -> {
                isLoading = false
                Utils.showToast(context, "Something is wrong!!!")
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
                authViewModel.signInWithPhoneAuthCredential(
                    otpValue,
                    Admin(Utils.getCurrentAdminId(), adminPhoneNumber = phoneNumber)
                )
            },
            enabled = otpValue.length == 6
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(
                onClick = {
                    if (timeLeft == 0) {
                        authViewModel.onCodeSent(phoneNumber!!, context as Activity)
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

@Composable
fun Otp_Text_Field(
    otpValue: String,
    onOtpValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = otpValue,
        onValueChange = { onOtpValueChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.NumberPassword,
            imeAction = ImeAction.Done
        ),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(6) { index ->
                    val char = when {
                        index >= otpValue.length -> ""
                        else -> otpValue[index].toString()
                    }
                    Box(
                        modifier = modifier
                            .size(38.dp)
                            .border(
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = Color.Gray
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.DarkGray,
                            modifier = Modifier.wrapContentSize(Alignment.Center)
                        )
                    }
                    Spacer(
                        Modifier.width(8.dp)
                    )
                }
            }
        }
    )
}


//@Preview(showSystemUi = true, showBackground = true)
//@Composable
//private fun Preview() {
//    Surface {
//        OtpScreen(
//            onVerifyClicked = {},
//            phoneNumber = "",
//            viewModel = AuthViewModel()
//        )
//    }
//}
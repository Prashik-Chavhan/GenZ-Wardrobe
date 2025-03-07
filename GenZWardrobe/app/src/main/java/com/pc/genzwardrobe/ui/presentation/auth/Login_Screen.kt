package com.pc.genzwardrobe.ui.presentation.auth

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.pc.genzwardrobe.R
import com.pc.genzwardrobe.ui.presentation.components.Circular_Loader
import com.pc.genzwardrobe.utils.Utils

@Composable
fun Login_Screen(
    onContinueClicked: () -> Unit,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {

    val state = authViewModel.state.collectAsState()

    val context = LocalContext.current
    val activity = context as Activity

    val phoneNumber by authViewModel.phoneNumber.collectAsState()
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = state.value) {
        when (state.value) {
            is AuthUiState.CodeSent -> {
                isLoading = false
                onContinueClicked()
                authViewModel.resetAuthUiState()
            }
            is AuthUiState.AlreadyLoggedIn -> {
                val message = (state.value as AuthUiState.AlreadyLoggedIn).message
                isLoading = false
                authViewModel.resetAuthUiState()

                Utils.showToast(context, message)
            }
            is AuthUiState.Error -> {
                isLoading = false
                Utils.showToast(context, "Something is wrong!!")
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
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.new_otp_imag),
            contentDescription = "Send Otp"
        )
        Text(
            text = "Phone Verification",
            fontWeight = FontWeight.Bold,
            fontSize = 26.sp
        )
        Text(
            text = "We need to register your phone number before getting started !",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { authViewModel.setPhoneNumber(it) },
            textStyle = MaterialTheme.typography.bodyLarge,
            prefix = {
                Text(
                    "+91 "
                )
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                unfocusedTextColor = Color.Black,
                focusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Auth_Button(
            buttonText = "Send the Code",
            onButtonClicked = {
                isLoading = true
                authViewModel.onCodeSent(
                    phoneNumber = phoneNumber,
                    activity = activity
                )
            },
            enabled = phoneNumber.length == 10
        )
    }
}

@Composable
fun Auth_Button(
    buttonText: String,
    onButtonClicked: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onButtonClicked() },
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(14.dp)
    ) {
        Text(
            text = buttonText,
            fontSize = 17.sp
        )
    }

}
    @Composable
    fun Auth_Loader(modifier: Modifier = Modifier) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false
            )
        ) {
            Row(
                modifier = modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(horizontal = 24.dp, vertical = 50.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text("Please Wait...")
            }
        }
    }

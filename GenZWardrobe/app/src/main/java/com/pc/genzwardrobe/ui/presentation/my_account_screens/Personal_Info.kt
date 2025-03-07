package com.pc.genzwardrobe.ui.presentation.my_account_screens

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.pc.genzwardrobe.core.domain.PersonalInfo
import com.pc.genzwardrobe.ui.presentation.components.CustomTextField
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Personal_Info(
    onNavBackClicked: () -> Unit,
    onAccountDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val myAccountsViewModel: MyAccountsViewModel = hiltViewModel()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val deleteAccountState = myAccountsViewModel.deleteAccount.collectAsState()

    val context = LocalContext.current
    val userInfo = myAccountsViewModel.userInfo.collectAsState()

    var otp by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember {
        mutableStateOf(
            Utils.getCurrentUserPhoneNumber()?.substringAfter("1") ?: ""
        )
    }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var showOtpDialog by remember { mutableStateOf(false) }
    LaunchedEffect(userInfo.value) {
        userInfo.value?.let {
            firstName = it.userFirstName.toString()
            lastName = it.userLastName.toString()
            email = it.userEmail.toString()
        }
    }

    LaunchedEffect(key1 = deleteAccountState.value) {
        when (deleteAccountState.value) {
            is AccountDeleteUiState.CodeSent -> {
                Utils.showToast(context, "Code sent successfully")
            }

            is AccountDeleteUiState.VerificationCompleted -> {
                myAccountsViewModel.deleteUserAccount()
                showOtpDialog = false
                onAccountDelete()
            }

            is AccountDeleteUiState.Error -> {
                Utils.showToast(context, "Error")
            }

            else -> {}
        }
    }

    if (showOtpDialog) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Dialog(
                onDismissRequest = { showOtpDialog = false },
                DialogProperties(
                    dismissOnClickOutside = true,
                    dismissOnBackPress = false
                )
            ) {
                Column(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White)
                        .padding(25.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Enter otp send to you mobile",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Otp_Text_Field(
                        otpValue = otp,
                        onOtpValueChange = { newOtp ->
                            if (newOtp.length <= 6) otp = newOtp
                        }
                    )
                    Button(
                        onClick = {
                            if (otp.isNotEmpty() || otp.length == 6) {
                                myAccountsViewModel.verifyOtp(otp)
                            } else {
                                Utils.showToast(context, "Please enter otp")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.inversePrimary,
                            contentColor = MaterialTheme.colorScheme.onSurface
                        )
                    ) {
                        Text(
                            text = "Confirm",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }
        }
    }

    if (showDeleteDialog) {
        LogOut_Delete_Alert_Box(
            onDismissRequest = { showDeleteDialog = false },
            text = "Are you sure want to delete this account?",
            onDismissButtonClicked = { showDeleteDialog = false },
            onConfirmButtonClicked = {
                myAccountsViewModel.sendOtpForReAuthentication(
                    phoneNumber,
                    context as Activity
                )
                showDeleteDialog = false
                showOtpDialog = true
            }
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Personal Information",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Save_Personal_Info(
                initialFirstName = firstName,
                onFirstNameChange = { firstName = it },
                initialLastName = lastName,
                onLastNameChange = { lastName = it },
                initialEmail = email,
                onEmailChange = { email = it },
                isImageSelected = userInfo.value?.userImage?.isEmpty() != true,
                selectedImageUri = "",
            )
            CustomTextField(
                initialValue = phoneNumber,
                onInitialValueChanged = { phoneNumber = it },
                label = "Phone number",
                keyboardType = KeyboardType.Number
            )
            Button(
                onClick = {
                    myAccountsViewModel.updateUserInfo(
                        personalInfo = PersonalInfo(
                            userFirstName = firstName,
                            userLastName = lastName,
                            userEmail = email
                        )
                    )
                    Utils.showToast(context, "Update successful")
                },
                enabled = firstName.isNotEmpty() || lastName.isNotEmpty() || email.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(50.dp)
            ) {
                Text(
                    text = "Update changes",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            HorizontalDivider(thickness = 2.dp)

            TextButton(
                onClick = { showDeleteDialog = true },
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text(
                    text = "Delete Account",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun Save_Personal_Info(
    initialFirstName: String,
    onFirstNameChange: (String) -> Unit,
    initialLastName: String,
    onLastNameChange: (String) -> Unit,
    initialEmail: String,
    onEmailChange: (String) -> Unit,
    isImageSelected: Boolean,
    selectedImageUri: String,
    modifier: Modifier = Modifier
) {
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()

    val selectImage =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            homeScreenViewModel.selectUserImage(uri)
        }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (isImageSelected) {
            Product_Image(
                image = selectedImageUri,
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.Gray, CircleShape)
            )
        } else {
            TextButton(
                onClick = { selectImage.launch("image/*") },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .clip(CircleShape)
                    .size(90.dp),
                border = BorderStroke(
                    2.dp,
                    Color.Black
                ),
                contentPadding = PaddingValues(2.dp)
            ) {
                Text("Add Image", color = Color.Black)
            }
        }
        Row {
            CustomTextField(
                initialValue = initialFirstName,
                onInitialValueChanged = { onFirstNameChange(it) },
                label = "First Name",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.weight(0.5f)
            )
            Spacer(Modifier.width(8.dp))
            CustomTextField(
                initialValue = initialLastName,
                onInitialValueChanged = { onLastNameChange(it) },
                label = "Last Name",
                keyboardType = KeyboardType.Text,
                modifier = Modifier.weight(0.5f)
            )
        }
        CustomTextField(
            initialValue = initialEmail,
            onInitialValueChanged = { onEmailChange(it) },
            label = "Email",
            keyboardType = KeyboardType.Email
        )
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

@Composable
fun LogOut_Delete_Alert_Box(
    onDismissRequest: () -> Unit,
    text: String,
    onDismissButtonClicked: () -> Unit,
    onConfirmButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = { onDismissRequest() },
        title = {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        },
        dismissButton = {
            TextButton(
                onClick = { onDismissButtonClicked() }
            ) {
                Text(
                    text = "No",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirmButtonClicked() }
            ) {
                Text(
                    text = "Yes",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    )
}
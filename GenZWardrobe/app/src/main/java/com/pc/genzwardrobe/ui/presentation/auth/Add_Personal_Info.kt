package com.pc.genzwardrobe.ui.presentation.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.core.domain.PersonalInfo
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.my_account_screens.MyAccountsViewModel
import com.pc.genzwardrobe.ui.presentation.my_account_screens.Save_Personal_Info
import kotlinx.coroutines.launch

@Composable
fun Add_Personal_Info(
    homeScreenViewModel: HomeScreenViewModel,
    onSkipClicked: () -> Unit,
    onInfoSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val myAccountsViewModel: MyAccountsViewModel = hiltViewModel()
    val selectedImage = homeScreenViewModel.selectedUserImage.collectAsState()
    val userInfo = myAccountsViewModel.userInfo.collectAsState()

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    LaunchedEffect(key1 = userInfo.value) {
        userInfo.value?.let {
            firstName = it.userFirstName ?: ""
            lastName = it.userLastName ?: ""
            email = it.userEmail ?: ""
        }
    }

    var isSavingInfo by remember { mutableStateOf(false) }

    if (isSavingInfo) {
        Auth_Loader()
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { onSkipClicked() },
            ) {
                Text(
                    text = "Skip",
                    style = MaterialTheme.typography.headlineSmall,
                )
            }
        }
        Spacer(Modifier.height(150.dp))
        Save_Personal_Info(
            initialFirstName = firstName,
            onFirstNameChange = { firstName = it },
            initialLastName = lastName,
            onLastNameChange = { lastName = it },
            initialEmail = email,
            onEmailChange = { email = it },
            isImageSelected = selectedImage.value.toString().isEmpty(),
            selectedImageUri = selectedImage.value.toString()
        )
        Spacer(Modifier.height(16.dp))
        Button(
            onClick = {
                isSavingInfo = true
                coroutineScope.launch {
                    val image = selectedImage.value?.let { homeScreenViewModel.saveUserImage(it) }

                    val personalInfo = PersonalInfo(
                        userImage = image,
                        userFirstName = firstName,
                        userLastName = lastName,
                        userEmail = email
                    )
                    homeScreenViewModel.savePersonalInfo(personalInfo)
                }
                isSavingInfo = false
                onInfoSaved()
            },
            enabled = firstName.isNotBlank() && lastName.isNotEmpty() && email.isNotEmpty(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ) {
            Text("Save Information")
        }
    }
}
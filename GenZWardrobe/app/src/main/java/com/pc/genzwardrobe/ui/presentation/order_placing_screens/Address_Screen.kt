package com.pc.genzwardrobe.ui.presentation.order_placing_screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.PermissionState
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.components.CustomTextField
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.home_screen.LocationState
import com.pc.genzwardrobe.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Add_Address_Screen(
    homeScreenViewModel: HomeScreenViewModel,
    cartViewModel: CartViewModel,
    onSaveClicked: () -> Unit,
    onArrowBackIconClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val locationState = homeScreenViewModel.getUserLocation.collectAsState()
    val userAddresses = homeScreenViewModel.getAllUserAddress.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var houseOrBuildingName by remember { mutableStateOf("") }
    var roadName by remember { mutableStateOf("") }

    val addressType = listOf("Home", "Office")
    val selectedAddressType = remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }

    var hasLocationPermission by remember { mutableStateOf(false) }
    var shouldShowRationale by remember { mutableStateOf(false) }

    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        emptyArray()
    } else {
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(Unit) {
        hasLocationPermission = permissionToRequest.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allPermissionGranted = permissions.all { it.value }

        if (allPermissionGranted) {
            hasLocationPermission = true
        } else {
            shouldShowRationale = true
        }
    }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.Blue
            )
        }
    }

    LaunchedEffect(key1 = locationState.value) {
        when (locationState.value) {
            is LocationState.Loading -> {
                Utils.showToast(context, "Loading")
                Log.d("LocationState", "Location state is Loading")
            }

            is LocationState.Success -> {
                val states = locationState.value as LocationState.Success
                pincode = states.pincode
                state = states.state
                city = states.city
                Log.d("LocationState", "Location state is success ${states.pincode}")
            }

            is LocationState.Error -> {
                Utils.showToast(context, "Failed to get users location")
                Log.d("LocationState", "Failed to get Location ${locationState.value}")
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Add delivery address",
                onIconClicked = {
                    onArrowBackIconClicked()
                    homeScreenViewModel.resetLocationState()
                },
                scrollBehavior
            )
        }
    ) { paddingValue ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValue)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CustomTextField(
                initialValue = fullName,
                onInitialValueChanged = {
                    fullName = it
                },
                label = "Full Name (Required)*",
                keyboardType = KeyboardType.Text
            )
            CustomTextField(
                initialValue = phoneNumber,
                onInitialValueChanged = { phoneNumber = it },
                label = "Phone number (Required)*",
                keyboardType = KeyboardType.Number
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CustomTextField(
                    initialValue = pincode,
                    onInitialValueChanged = { pincode = it },
                    label = "Pincode (Required)*",
                    keyboardType = KeyboardType.Number,
                    modifier = Modifier.weight(0.5f)
                )
                Button(
                    onClick = {
                        if (hasLocationPermission) {
                            Utils.showToast(context, "Location permission granted!")
                            homeScreenViewModel.getUsersLocation()
                        } else {
                            if (shouldShowRationale) {
                                shouldShowRationale = true
                            } else {
                                launcher.launch(permissionToRequest)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    ),
                    shape = RectangleShape,
                    modifier = Modifier.weight(0.5f)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = ""
                    )
                    Text(
                        text = "Use my location"
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomTextField(
                    initialValue = state,
                    onInitialValueChanged = { state = it },
                    label = "State (Required)*",
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.weight(0.5f)
                )
                CustomTextField(
                    initialValue = city,
                    onInitialValueChanged = { city = it },
                    label = "City (Required)*",
                    keyboardType = KeyboardType.Text,
                    modifier = Modifier.weight(0.5f)
                )
            }
            CustomTextField(
                initialValue = houseOrBuildingName,
                onInitialValueChanged = { houseOrBuildingName = it },
                label = "House No., Building Name (Required)*",
                keyboardType = KeyboardType.Text
            )
            CustomTextField(
                initialValue = roadName,
                onInitialValueChanged = { roadName = it },
                label = "Road name, Area, Colony (Required)*",
                keyboardType = KeyboardType.Text
            )
            Text(
                text = "Type of address",
                fontSize = 20.sp,
                color = Color.Black
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(22.dp)
            ) {
                addressType.forEach { text ->
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color.LightGray
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable { selectedAddressType.value = text }
                                .padding(end = 8.dp)
                        ) {
                            RadioButton(
                                selected = text == selectedAddressType.value,
                                onClick = {
                                    selectedAddressType.value = text
                                }
                            )
                            Text(
                                text = text,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
            Button(
                onClick = {
                    if (
                        fullName.isEmpty() || phoneNumber.isEmpty() ||
                        pincode.isEmpty() || city.isEmpty() ||
                        state.isEmpty() || houseOrBuildingName.isEmpty() ||
                        roadName.isEmpty()
                    ) {
                        Utils.showToast(context, "Empty fields are not allowed")
                    } else if (selectedAddressType.value.isEmpty()) {
                        Utils.showToast(context, "Please select address type")
                    } else {
                        isLoading = true
                        val userAddress = UserAddress(
                            id = userAddresses.value.size + 1,
                            name = fullName,
                            phoneNumber = phoneNumber,
                            pincode = pincode,
                            city = city,
                            state = state,
                            houseNo = houseOrBuildingName,
                            area = roadName,
                            addressType = selectedAddressType.value
                        )
                        homeScreenViewModel.saveUserAddressInFirebase(userAddress)
                        cartViewModel.saveSelectedAddressId(userAddress.id!!)
                        isLoading = false
                        onSaveClicked()
                        fullName = ""
                        phoneNumber = ""
                        pincode = ""
                        city = ""
                        state = ""
                        houseOrBuildingName = ""
                        roadName = ""
                        selectedAddressType.value = ""
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Unspecified,
                    contentColor = Color.White
                )
            ) {
                Text(
                    "Save Address",
                    fontSize = 17.sp
                )
            }
        }
    }
}
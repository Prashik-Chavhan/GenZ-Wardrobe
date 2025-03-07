package com.pc.genzwardrobe.ui.presentation.my_account_screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Delivery_Address_Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Manage_Address_Screen(
    onAddNewAddressClicked: () -> Unit,
    onArrowBackIconClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()

    val getAllAddresses by homeScreenViewModel.getAllUserAddress.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Manage Address",
                onIconClicked = {
                    onArrowBackIconClicked()
                },
                scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 8.dp)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            item {
                Button(
                    onClick = {
                        onAddNewAddressClicked()
                    },
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = ""
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = "Add a new address",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Spacer(Modifier.height(20.dp))
                Text(
                    text = if (getAllAddresses.isEmpty()) "Please add some addresses" else getAllAddresses.size.toString() + " saved address".uppercase(),
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
            items(getAllAddresses) { userAddress ->
                Delivery_Address_Card(
                    title = "Delivery to:",
                    userAddress = userAddress,
                    changeButton = false,
                    onUserAddressChange = {},
                    onOkayClicked = {
                        homeScreenViewModel.deleteUserAddress(userAddress.id!!)
                    },
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(BorderStroke(2.dp, color = Color.Black,), shape = RoundedCornerShape(22.dp))
                        .clip(RoundedCornerShape(12.dp))
                )
            }
        }
    }
}
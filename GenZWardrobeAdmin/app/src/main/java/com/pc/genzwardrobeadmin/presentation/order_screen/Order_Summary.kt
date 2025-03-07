package com.pc.genzwardrobeadmin.presentation.order_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.pc.genzwardrobeadmin.core.domain.CartProducts
import com.pc.genzwardrobeadmin.core.domain.Transactions
import com.pc.genzwardrobeadmin.presentation.MainViewModel
import com.pc.genzwardrobeadmin.presentation.OrderedProductUiState
import com.pc.genzwardrobeadmin.presentation.add_screen.components.CustomDropdownMenu
import com.pc.genzwardrobeadmin.presentation.home_screen.Product_Image
import com.pc.genzwardrobeadmin.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Order_Summary(
    orderId: String?,
    userId: String?,
    onNavBackClicked: () -> Unit,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val orderedProductsUiState by mainViewModel.orderedCartProducts.collectAsState()
    val walletData = mainViewModel.userWalletData.collectAsState()

    val currentAmount = walletData.value

    LaunchedEffect(
        key1 = orderId,
        key2 = userId
    ) {
        mainViewModel.getOrderedCartProducts(userId!!, orderId!!)
        mainViewModel.getUserWalletAmount(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Order Details")
                },
                navigationIcon = {
                    IconButton(
                        onClick = { onNavBackClicked() }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Arrow back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) {
        when (orderedProductsUiState) {
            OrderedProductUiState.Loading -> {
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is OrderedProductUiState.Success -> {
                val success = (orderedProductsUiState as OrderedProductUiState.Success).products
                Column(
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    for (products in success) {
                        Ordered_List(
                            timeStamp = products.orderTime!!,
                            date = products.orderDate!!,
                            size = products.products?.size ?: -1,
                            itemAmount = products.totalAmount ?: 0,
                            cartProducts = products.products ?: emptyList(),
                            onConfirmClicked = { itemId, orderStatus, amount ->
                                mainViewModel.updateOrderStatus(
                                    userId = products.userId!!,
                                    orderId = products.orderId?.substring(1)!!,
                                    itemId = itemId,
                                    orderStatus = orderStatus
                                )
                                if (orderStatus == 10) {
                                    mainViewModel.addRefundAmount(userId!!, currentAmount + amount)
                                    mainViewModel.addTransaction(
                                        userId, Transactions(
                                            Utils.generateRandomId(),
                                            "Refund",
                                            amount,
                                            Utils.getTodaysDate(),
                                            System.currentTimeMillis(),
                                        )
                                    )
                                }
                            }
                        )
                    }
                }
            }

            is OrderedProductUiState.Error -> {
                val error = (orderedProductsUiState as OrderedProductUiState.Error).message
                Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(error)
                }
            }
        }
    }
}

@Composable
fun Ordered_List(
    timeStamp: Long,
    date: String,
    size: Int,
    itemAmount: Int,
    cartProducts: List<CartProducts>,
    onConfirmClicked: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val orderOn = "Ordered on: "
    LazyColumn(
        modifier = modifier.fillMaxSize()
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = orderOn + "$date / ${Utils.longTimeToHumanReadable(timeStamp)}",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }

        items(cartProducts) {
            Ordered_Item(
                cartProducts = it,
                onConfirmClicked = { itemId, orderStatus, amount ->
                    onConfirmClicked(itemId, orderStatus, amount)
                },
                itemAmount = itemAmount,
                size = size,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}

@Composable
fun Ordered_Item(
    cartProducts: CartProducts,
    size: Int,
    itemAmount: Int,
    onConfirmClicked: (Int, Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val amount = if (size == 1) "₹$itemAmount" else "₹${cartProducts.discountPrice?.toInt()}"
    var expanded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val orderStatusList = Utils.OrderStatusList.orderStatus

    val orderStatusMap = orderStatusList.getOrElse(cartProducts.orderStatus!!) { "Unknown" }
    val filteredOrderedList =
        orderStatusList.filterIndexed { index, _ -> index > cartProducts.orderStatus }

    var selectedValue by remember { mutableStateOf("") }
    val selectedValueAsInt = orderStatusList.indexOf(selectedValue)

    if (showDialog) {
        AlertDialog(
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = { showDialog = false },
            title = {
                Text("Current Order Status: $orderStatusMap")
            },
            text = {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    CustomDropdownMenu(
                        label = "Select Order Status",
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded },
                        selectedValue = selectedValue,
                        onItemSelected = { selectedValue = it },
                        items = filteredOrderedList,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirmClicked(
                            cartProducts.itemId,
                            selectedValueAsInt,
                            amount.substring(1).toInt()
                        )
                        showDialog = false
                        selectedValue = ""
                    }
                ) {
                    Text("Change")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        selectedValue = ""
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Product_Image(
                    image = cartProducts.productImageUri ?: "",
                    modifier = Modifier.size(92.dp)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = cartProducts.variantName ?: "Variant name",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "${cartProducts.size} / ${cartProducts.variantColor}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = amount
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Current Status: ${cartProducts.orderStatus}",
                    style = MaterialTheme.typography.labelMedium
                )
                TextButton(
                    onClick = { showDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "Update Order Status",
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

//@Preview(showSystemUi = true)
//@Composable
//private fun Preview(
//    modifier: Modifier = Modifier
//) {
//    var expanded by remember { mutableStateOf(false) }
//
//
//    Column(
//        modifier = modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally,
//        verticalArrangement = Arrangement.Center
//    ) {
//        Button(
//            onClick = { expanded = true }
//        ) {
//            Text("Show Dialog")
//        }
//    }
//}
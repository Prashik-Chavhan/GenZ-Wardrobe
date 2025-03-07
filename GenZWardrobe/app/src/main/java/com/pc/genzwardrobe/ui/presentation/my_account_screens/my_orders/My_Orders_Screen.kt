package com.pc.genzwardrobe.ui.presentation.my_account_screens.my_orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.core.domain.OrderedProducts
import com.pc.genzwardrobe.ui.presentation.components.Circular_Loader
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.my_account_screens.MyAccountsViewModel
import com.pc.genzwardrobe.ui.presentation.my_account_screens.MyOrdersUiState
import com.pc.genzwardrobe.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun My_Orders_Screen(
    onItemClicked: (String, Int, Int) -> Unit,
    onNavBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val viewModel: MyAccountsViewModel = hiltViewModel()

    val myOrders = viewModel.myOrders.collectAsState()

    val context = LocalContext.current
    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "My Orders",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior
            )
        }
    ) { paddingValues ->
        when (myOrders.value) {
            is MyOrdersUiState.Loading -> {
                Circular_Loader(modifier)
            }

            is MyOrdersUiState.Success -> {
                val uiState = myOrders.value as MyOrdersUiState.Success
                if (uiState.itemList.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Your haven't ordered yet...")
                    }
                } else {
                    LazyColumn(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    ) {
                        items(uiState.itemList) { orderedProduct ->
                            Order_Item(
                                orderedProducts = orderedProduct,
                                onCardClicked = { itemId ->
                                    onItemClicked(
                                        orderedProduct.orderId?.substring(1) ?: "OrderId",
                                        itemId,
                                        orderedProduct.totalAmount ?: 0
                                    )
                                    Utils.showToast(context, "Clicked on ${orderedProduct.orderId}")
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            is MyOrdersUiState.Error -> {
                Column(
                    modifier = modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Something error occurred!!!")
                    Button(
                        onClick = { Utils.showToast(context, "Clicked on retry") },
                        shape = RectangleShape
                    ) {
                        Text("Clicked on retry")
                    }
                }
            }
        }
    }
}

@Composable
fun Order_Item(
    orderedProducts: OrderedProducts,
    onCardClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    for (orderedProduct in orderedProducts.products!!) {
        val (statusText, statusColor) = when (orderedProduct.orderStatus) {
            0 -> "Your order has been placed and under processing." to Color.Blue
            1 -> "Your order is being prepared." to Color.Blue
            2 -> "Your order has been confirmed." to Color.Blue
            3 -> "Your item have been packed for shipping." to Color.Blue
            4 -> "Your order has been handed over to the courier/logistics provider and is on its way." to Color.Blue
            5 -> "Out for Delivery" to Color.Blue
            6 -> "Your order has been successfully delivered." to Color.Blue
            7 -> "An attempt to deliver the order failed." to Color.Blue
            8 -> "You initiated a return, and it has been processed." to Color.Blue
            9 -> "Refund for a returned or canceled order is being processed." to Color.Blue
            10 -> "The refund for the order has been completed successfully." to Color.Blue
            11 -> "Your order has been canceled by successfully." to Color.Blue
            else -> {
                "Unknown" to Color.Black
            }
        }
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clickable { onCardClicked(orderedProduct.itemId) }
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Product_Image(
                orderedProduct.productImageUri!!,
                modifier = Modifier.size(80.dp)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = orderedProduct.variantName ?: "Unknown",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        HorizontalDivider()
    }
}

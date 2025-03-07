package com.pc.genzwardrobe.ui.presentation.my_account_screens.my_orders

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.ui.presentation.cart_screen.Price_Details
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.OrderStatus
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.my_account_screens.MyAccountsViewModel
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Custom_Card
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun My_Order_Detail(
    orderId: String?,
    itemId: Int?,
    totalAmount: Int?,
    onItemClicked: (String, Int) -> Unit,
    onNavBackClicked: () -> Unit,
    onReturnClicked: (String, Int, String, String, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val myAccountsViewModel: MyAccountsViewModel = hiltViewModel()
    val orderDetails by myAccountsViewModel.myOrderById.collectAsState()
    val otherItems by myAccountsViewModel.otherOrders.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val product = orderDetails?.first
    val userAddress = orderDetails?.second

    val bottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var showCancelDialog by remember { mutableStateOf(false) }

    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    "Are you sure want to cancel this order?",
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (orderId != null && itemId != null) {
                            myAccountsViewModel.updateOrderStatus(orderId, itemId, 11)
                            showCancelDialog = false
                            Utils.showToast(context, "Order cancelled successfully")
                        }
                    },
//                colors = ButtonDefaults.buttonColors(
//                    containerColor = MaterialTheme.colorScheme.onSecondaryContainer
//                )
                ) {
                    Text(
                        "Yes",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showCancelDialog = false }
                ) {
                    Text(
                        "No",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }

    if (bottomSheet.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch {
                    bottomSheet.hide()
                }
            },
            modifier = Modifier.fillMaxHeight(0.7f),
            shape = RectangleShape,
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OrderStatus(
                    currentStatus = product?.orderStatus!!
                )
            }
        }
    }

    LaunchedEffect(key1 = orderId, key2 = itemId) {
        if (itemId != null && orderId != null) {
            myAccountsViewModel.getOrderDetails(orderId, itemId)
            myAccountsViewModel.getOtherOrders(orderId, itemId)
        }
    }

    val totalAmounts =
        if (otherItems.isEmpty()) totalAmount else product?.discountPrice?.toInt()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Order detail",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior
            )
        }
    ) {
        ConstraintLayout(
            modifier = modifier
                .padding(it)
                .verticalScroll(rememberScrollState())
                .background(color = Color.LightGray),
        ) {
            val (
                itemDetailDeliveryStatus, otherItem,
                shipping, priceDetails
            ) = createRefs()

            Custom_Card(
                modifier = Modifier.constrainAs(itemDetailDeliveryStatus) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                content = {
                    Header_Title(
                        title = "Order Id - #$orderId",
                        modifier = Modifier.padding(start = 16.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    HorizontalDivider()
                    Spacer(Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Item_Details(
                            itemName = product?.variantName ?: "Variant Name",
                            itemSize = product?.size ?: "Size",
                            itemColor = product?.variantColor ?: "Color",
                            itemDiscountedPrice = product?.discountPrice?.toInt()?.toString()
                                ?: "Price",
                            modifier = Modifier.weight(1f)
                        )
                        Product_Image(
                            product?.productImageUri ?: "Product Image",
                            modifier = Modifier.size(92.dp)
                        )
                    }
                    HorizontalDivider()
                    Tracking_Updates(
                        onSeeAllClicked = {
                            coroutineScope.launch {
                                bottomSheet.show()
                            }
                        },
                        onReturnOrCancelClicked = { text ->
                            if (text == "Return") {
                                val encodedImageUrl = product?.productImageUri.let { imageUrl ->
                                    URLEncoder.encode(imageUrl, StandardCharsets.UTF_8.toString())
                                }
                                onReturnClicked(
                                    product?.variantName!!,
                                    totalAmounts!!,
                                    encodedImageUrl,
                                    orderId!!,
                                    itemId!!
                                )
                            } else {
                                if (product?.orderStatus == 11) {
                                    Utils.showToast(context, "Order is already cancelled")
                                } else {
                                    showCancelDialog = true
                                }
                            }
                        },
                        isDelivered = product?.orderStatus == 6,
                        onChatWithUs = { Utils.showToast(context, "On chat with us clicked") },
                        modifier = Modifier
                    )
                }
            )
            if (otherItems.isNotEmpty()) {
                Custom_Card(
                    modifier = Modifier
                        .heightIn(max = 500.dp)
                        .constrainAs(otherItem) {
                            start.linkTo(parent.start)
                            top.linkTo(itemDetailDeliveryStatus.bottom)
                            end.linkTo(parent.end)
                        }
                        .padding(top = 8.dp),
                    content = {
                        LazyColumn(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Header_Title(
                                    "Other items in this order"
                                )
                                Spacer(Modifier.height(8.dp))
                                HorizontalDivider()
                            }
                            items(otherItems) { cartProduct ->
                                Other_Items(
                                    cartProducts = cartProduct,
                                    onItemClicked = {
                                        Utils.showToast(
                                            context,
                                            "${cartProduct.itemId} / ${cartProduct.variantColor}"
                                        )
                                        onItemClicked(orderId!!, cartProduct.itemId)
                                    },
                                    modifier = Modifier
                                )
                            }
                        }
                    }
                )
            }
            Custom_Card(
                modifier = Modifier
                    .constrainAs(shipping) {
                        start.linkTo(parent.start)
                        top.linkTo(if (otherItems.isNotEmpty()) otherItem.bottom else itemDetailDeliveryStatus.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 8.dp),
                content = {
                    if (userAddress != null) {
                        Shipping_Details(userAddress = userAddress)
                    }
                }
            )
            Custom_Card(
                modifier = Modifier
                    .constrainAs(priceDetails) {
                        start.linkTo(parent.start)
                        top.linkTo(shipping.bottom)
                        end.linkTo(parent.end)
                    }
                    .padding(top = 4.dp),
                content = {
                    Header_Title("Price Details", modifier = Modifier.padding(start = 16.dp))
                    HorizontalDivider()
                    Price_Details(
                        totalPrice = product?.originalPrice ?: 0,
                        discountPrice = product?.originalPrice?.minus(
                            product.discountPrice?.toInt() ?: 0
                        ) ?: 0,
                        itemCount = 1,
                        deliveryCharge = "",
                        totalAmount = totalAmounts ?: 0,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            )
        }
    }
}

@Composable
fun Header_Title(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.Bold,
        modifier = modifier
    )
}

@Composable
fun Shipping_Details(
    userAddress: UserAddress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Header_Title("Shipping details")
        HorizontalDivider()
        Shipping_Text(text = userAddress.name)
        Shipping_Text(text = userAddress.houseNo)
        Shipping_Text(text = userAddress.area)
        Shipping_Text(text = userAddress.city)
        Shipping_Text(text = "${userAddress.state} - ${userAddress.pincode}")
        Shipping_Text(text = "Phone number: ${userAddress.phoneNumber}")
    }
}

@Composable
fun Shipping_Text(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
    )
}

@Composable
fun Tracking_Updates(
    onSeeAllClicked: () -> Unit,
    onReturnOrCancelClicked: (String) -> Unit,
    onChatWithUs: () -> Unit,
    isDelivered: Boolean,
    modifier: Modifier = Modifier
) {
    val text = if (isDelivered) "Return" else "Cancel"
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TextButton(
            onClick = { onSeeAllClicked() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                contentColor = MaterialTheme.colorScheme.primary,
                containerColor = Color.White
            )
        ) {
            Text(
                text = "See All Shipping Updates",
                style = MaterialTheme.typography.labelMedium
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = ""
            )
        }
        HorizontalDivider()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TextButton(
                onClick = { onReturnOrCancelClicked(text) },
            ) {
                Icon(
                    imageVector = if (isDelivered) Icons.AutoMirrored.Filled.Send else Icons.Default.Clear,
                    contentDescription = ""
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelMedium
                )
            }
            VerticalDivider()
            TextButton(
                onClick = { onChatWithUs() },
            ) {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = ""
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Chat with us",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun Item_Details(
    itemName: String,
    itemSize: String,
    itemColor: String,
    itemDiscountedPrice: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceAround
    ) {
        Text(
            text = itemName,
            maxLines = 2,
            style = MaterialTheme.typography.titleLarge,
        )
        Text(
            text = "$itemSize, $itemColor",
            style = MaterialTheme.typography.titleMedium,
            color = Color.Gray
        )
        Text(
            text = "₹ $itemDiscountedPrice",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Composable
fun Other_Items(
    cartProducts: CartProducts,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onItemClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = cartProducts.variantName ?: "Variant name",
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Product_Image(
            image = cartProducts.productImageUri!!,
            modifier = modifier
                .size(92.dp)
                .padding(end = 16.dp)
        )
    }
}
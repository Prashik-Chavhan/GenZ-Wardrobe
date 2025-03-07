package com.pc.genzwardrobe.ui.presentation.order_placing_screens

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.core.domain.OrderedProducts
import com.pc.genzwardrobe.core.domain.Transactions
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.ui.presentation.cart_screen.Address_Sheet_Content
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartBottomBar
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.cart_screen.Cart_Items_List
import com.pc.genzwardrobe.ui.presentation.cart_screen.Cart_Product_Item
import com.pc.genzwardrobe.ui.presentation.cart_screen.PaymentState
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.my_account_screens.MyAccountsViewModel
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Custom_Card
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Order_Summary_Screen(
    itemId: Int?,
    cartViewModel: CartViewModel,
    onArrowBackIconClicked: () -> Unit,
    onPaymentComplete: (String, String) -> Unit,
    onItemClicked: (String, String) -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val myAccountsViewModel: MyAccountsViewModel = hiltViewModel()
    val walletAmount = myAccountsViewModel.walletAmount.collectAsState()
    val cartProduct = cartViewModel.getAllCartProducts.collectAsState()
    val getCartProductById = cartViewModel.getCartProductById.collectAsState()
    val totalOriginalPrice by cartViewModel.getTotalOriginalPrice.collectAsState(0)
    val totalDiscountedPrice by cartViewModel.getTotalDiscountedPrice.collectAsState(0)
    val selectedAddressId by cartViewModel.selectedAddressId.collectAsState()
    val paymentState = cartViewModel.paymentState.collectAsState()

    val currentStock = cartViewModel.getProductCurrentStock.collectAsState()
    val selectableOption = listOf("1", "2", "3", "4")
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    val context = LocalContext.current
    val activity = context as Activity

    val getAllAddresses = homeScreenViewModel.getAllUserAddress.collectAsState()
    val deliveryCharge = if (totalDiscountedPrice in 1..499) 200 else 0

    val totalAmount = totalDiscountedPrice.plus(deliveryCharge)

    var clickedItemId by remember { mutableStateOf("") }
    var clickedItemColor by remember { mutableStateOf("") }
    var clickedItemSize by remember { mutableStateOf("") }

    LaunchedEffect(
        key1 = clickedItemId,
        key2 = clickedItemColor,
        key3 = clickedItemSize
    ) {
        cartViewModel.getProductStock(clickedItemId, clickedItemColor, clickedItemSize)
    }

    var selectedBillingAddress by remember { mutableStateOf<UserAddress?>(null) }

    val orderId by remember { mutableStateOf(Utils.generateOrderId()) }
    var singleTotalPrice by remember { mutableIntStateOf(0) }
    var singleDiscountedPrice by remember { mutableIntStateOf(0) }
    val singleDeliveryCharge = if (singleDiscountedPrice in 1..499) 200 else 0
    val singleTotalAmount = singleDiscountedPrice.plus(singleDeliveryCharge)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showPaymentOptionSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    if (sheetState.isVisible) {
        Address_Sheet_Content(
            onDismissClicked = {
                coroutineScope.launch {
                    sheetState.hide()
                }
            },
            userAddress = getAllAddresses.value,
            onAddressClicked = {
                cartViewModel.updateSelectedAddressId(it)
            },
            selectedId = selectedAddressId
        )
    }

    LaunchedEffect(key1 = selectedAddressId) {
        selectedBillingAddress = getAllAddresses.value.find { it.id == selectedAddressId }
    }

    LaunchedEffect(key1 = itemId) {
        if (itemId != -1) {
            cartViewModel.getCartProduct(itemId!!)
            Log.d("ItemId", "Item id: $itemId")
        }
    }

    val orderSummaryItem = if (itemId == -1) cartProduct.value else getCartProductById.value
    val originalTotalAmount = if (itemId == -1) totalOriginalPrice else singleTotalPrice
    val discountedPrice = if (itemId == -1) totalDiscountedPrice else singleDiscountedPrice
    val deliveryCharges = if (itemId == -1) deliveryCharge else singleDeliveryCharge
    val totalAmounts = if (itemId == -1) totalAmount else singleTotalAmount

    LaunchedEffect(paymentState.value) {
        when (val state = paymentState.value) {
            is PaymentState.Loading -> {
                Utils.showToast(context, "Loading")
            }

            is PaymentState.Success -> {
                afterSuccessfulPayment(
                    itemId = itemId!!,
                    selectedBillingAddress = selectedBillingAddress!!,
                    orderSummaryItem = orderSummaryItem,
                    orderId = orderId,
                    cartViewModel = cartViewModel,
                    totalAmount = totalAmount
                )
                coroutineScope.launch { showPaymentOptionSheet.hide() }
                onPaymentComplete(selectedBillingAddress?.name ?: "Unknown", orderId)
                Utils.showToast(context, "Payment successful: ${state.paymentId}")
                Log.d("Payment", "Ui Success: ${state.paymentId}")
                cartViewModel.resetPaymentState()
            }

            is PaymentState.Error -> {
                Utils.showToast(context, "Payment Error: ${state.message}")
                Log.d("Payment", "Ui Error: ${state.message}")
                cartViewModel.resetPaymentState()
            }

            else -> {}
        }
    }

    if (showPaymentOptionSheet.isVisible) {
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { showPaymentOptionSheet.hide() }
            },
            modifier = Modifier,
        ) {
            val cardTexts = listOf("Pay via wallet", "Pay via online", "Cash on delivery")

            Column(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.4f),
                verticalArrangement = Arrangement.spacedBy(space = 36.dp, alignment = Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                cardTexts.forEachIndexed { index, text ->
                    Card(
                        onClick = {
                            when (index) {
                                0 -> {
                                    if ((walletAmount.value ?: 0) >= totalAmounts) {
                                        afterSuccessfulPayment(
                                            itemId = itemId!!,
                                            selectedBillingAddress = selectedBillingAddress!!,
                                            orderSummaryItem = orderSummaryItem,
                                            orderId = orderId,
                                            cartViewModel = cartViewModel,
                                            totalAmount = totalAmount
                                        )
                                        paymentViaWallet(
                                            myAccountsViewModel,
                                            finalAmount = totalAmounts,
                                            walletAmount = walletAmount.value!!,
                                            type = "Deduct"
                                        )
                                        coroutineScope.launch { showPaymentOptionSheet.hide() }
                                        onPaymentComplete(
                                            selectedBillingAddress?.name ?: "Unknown",
                                            orderId
                                        )
                                    } else {
                                        Utils.showToast(context, "Insufficient balance in wallet")
                                        Log.d("PaymentError", "Insufficient balance in wallet")
                                    }
                                }

                                1 -> {
                                    cartViewModel.startPayment(
                                        activity = activity,
                                        orderAmount = discountedPrice,
                                        email = "tester123@tesing.com",
                                        phoneNumber = Utils.getCurrentUserPhoneNumber()
                                            ?: "876543548766"
                                    )
                                }

                                2 -> {
                                    afterSuccessfulPayment(
                                        itemId = itemId!!,
                                        selectedBillingAddress = selectedBillingAddress!!,
                                        orderSummaryItem = orderSummaryItem,
                                        orderId = orderId,
                                        cartViewModel = cartViewModel,
                                        totalAmount = totalAmount
                                    )
                                    coroutineScope.launch { showPaymentOptionSheet.hide() }
                                    onPaymentComplete(
                                        selectedBillingAddress?.name ?: "Unknown",
                                        orderId
                                    )
                                }
                            }
                        },
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            contentColor = MaterialTheme.colorScheme.onSecondary
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomAppBar(
                modifier = Modifier.height(72.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                CartBottomBar(
                    totalOriginalPrice = originalTotalAmount,
                    totalDiscountedPrice = totalAmounts,
                    onPlaceOrderClicked = {
                        if (selectedAddressId == 0) {
                            Utils.showToast(context, "Please select delivery address")
                        } else {
                            coroutineScope.launch {
                                showPaymentOptionSheet.show()
                            }
                        }
                    },
                    buttonText = "Pay Now"
                )
            }
        },
        topBar = {
            CustomTopAppBar(
                text = "Order summary",
                onIconClicked = {
                    onArrowBackIconClicked()
                },
                scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .background(Color.LightGray)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
        ) {
            Cart_Items_List(
                cartProduct = orderSummaryItem,
                onChangeAddressClicked = {
                    coroutineScope.launch {
                        sheetState.show()
                    }
                },
                onSelectAddressClicked = {
                    coroutineScope.launch { sheetState.show() }
                },
                totalOriginalPrice = originalTotalAmount,
                totalDiscountedPrice = discountedPrice,
                deliveryCharge = deliveryCharges,
                totalAmount = totalAmounts,
                userAddress = selectedBillingAddress,
                content = { cartProducts ->
                    val originalPrice = cartProducts.productQuantity?.let { quantity ->
                        cartProducts.originalPrice?.times(quantity)
                    }
                    singleTotalPrice = originalPrice ?: 0
                    val productPrice = cartProducts.productQuantity?.let { quantity ->
                        cartProducts.discountPrice?.times(quantity)
                    }
                    if (productPrice != null) {
                        singleDiscountedPrice = productPrice.toInt()
                    }

                    Custom_Card(
                        content = {
                            Cart_Product_Item(
                                cartProductImage = cartProducts.productImageUri.toString(),
                                selectedOption = cartProducts.productQuantity.toString(),
                                selectableOption = selectableOption,
                                onSelectedOptionChanged = { newSelectableQuantity ->

                                    clickedItemId = cartProducts.variantId!!
                                    clickedItemColor = cartProducts.variantColor!!
                                    clickedItemSize = cartProducts.size

                                    if (newSelectableQuantity.toInt() <= currentStock.value) {
                                        cartViewModel.updateCartProductQuantity(
                                            cartProducts.variantId.toString(),
                                            cartProducts.variantColor.toString(),
                                            cartProducts.size,
                                            newSelectableQuantity.toInt()
                                        )
                                    } else {
                                        Utils.showToast(
                                            context,
                                            "only ${currentStock.value} stock left"
                                        )
                                    }
                                },
                                variantName = cartProducts.variantName.toString(),
                                variantDiscount = cartProducts.discount.toString(),
                                variantSize = cartProducts.size,
                                variantDiscountedPrice = productPrice?.toInt().toString(),
                                variantOriginalPrice = originalPrice.toString(),
                                onItemClicked = {
                                    onItemClicked(
                                        cartProducts.variantId ?: "", cartProducts.variantColor
                                            ?: ""
                                    )
                                }
                            )
                        }
                    )
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Delivery_Address_Card(
    title: String,
    userAddress: UserAddress,
    changeButton: Boolean,
    onUserAddressChange: () -> Unit,
    onOkayClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expnaded by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }

    val addressChangeSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = {}
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp)
                ) {
                    Text(
                        text = "Remove Address",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Are you sure want to delete this address?"
                    )
                    Spacer(Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(
                            onClick = { showDialog = false }
                        ) {
                            Text("Cancel")
                        }
                        TextButton(
                            onClick = {
                                showDialog = false
                                onOkayClicked()
                            }
                        ) {
                            Text("Okay")
                        }
                    }
                }
            }
        }
    }

    Custom_Card(
        modifier = modifier,
        content = {
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.W800,
                        modifier = Modifier.weight(1f)
                    )
                    if (changeButton) {
                        Button(
                            onClick = { onUserAddressChange() },
                            shape = RectangleShape,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = Color.Blue
                            ),
                            border = BorderStroke(1.dp, Color.Gray)
                        ) {
                            Text("Change")
                        }
                    } else {
                        IconButton(
                            onClick = { expnaded = !expnaded }
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = ""
                            )
                            DropdownMenu(
                                expanded = expnaded,
                                onDismissRequest = {
                                    expnaded = false
                                },
                                containerColor = Color.White,
                                border = BorderStroke(2.dp, color = Color.LightGray),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 14.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Address_Operation_Menu(
                                        text = "Edit",
                                        onTextClicked = {
                                            coroutineScope.launch {
                                                addressChangeSheet.show()
                                            }
                                        }
                                    )
                                    HorizontalDivider()
                                    Address_Operation_Menu(
                                        text = "Delete",
                                        onTextClicked = { showDialog = true }
                                    )
                                }
                            }
                        }
                    }
                }
                Address_Details(userAddress)
            }
        }
    )
}

@Composable
fun Address_Details(
    userAddress: UserAddress,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = userAddress.name,
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = userAddress.addressType.uppercase(),
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.secondary,
                        shape = RectangleShape
                    )
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = "${userAddress.houseNo}, ${userAddress.area}, ${userAddress.city}, ${userAddress.pincode}",
            style = MaterialTheme.typography.labelMedium
        )
        Text(
            text = userAddress.phoneNumber,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun Address_Operation_Menu(
    text: String,
    onTextClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = modifier
            .fillMaxWidth()
            .clickable { onTextClicked() }
    )
}

fun afterSuccessfulPayment(
    cartViewModel: CartViewModel,
    itemId: Int,
    totalAmount: Int,
    selectedBillingAddress: UserAddress,
    orderSummaryItem: List<CartProducts>,
    orderId: String
) {
    cartViewModel.afterSuccessFullPayment(
        itemId = itemId,
        orderedProducts = OrderedProducts(
            userId = Utils.getCurrentUserId(),
            userAddress = selectedBillingAddress,
            products = orderSummaryItem,
            orderId = orderId,
            orderTime = System.currentTimeMillis(),
            orderDate = Utils.getTodaysDate(),
            totalAmount = totalAmount
        )
    )
}

fun paymentViaWallet(
    myAccountsViewModel: MyAccountsViewModel,
    walletAmount: Int,
    finalAmount: Int,
    type: String
) {
    if (type == "Deduct") {
        myAccountsViewModel.addOrDeductAmountInWallet(walletAmount - finalAmount)
        myAccountsViewModel.addTransaction(
            Transactions(
                transactionId = Utils.generateRandomId(),
                type = "Deduct",
                amount = finalAmount,
                date = Utils.getTodaysDate(),
                timeStamp = System.currentTimeMillis()
            )
        )
    } else {
        myAccountsViewModel.addOrDeductAmountInWallet(walletAmount + finalAmount)
        myAccountsViewModel.addTransaction(
            Transactions(
                transactionId = Utils.generateRandomId(),
                type = "Deposit",
                amount = finalAmount,
                date = Utils.getTodaysDate(),
                timeStamp = System.currentTimeMillis()
            )
        )
    }
}
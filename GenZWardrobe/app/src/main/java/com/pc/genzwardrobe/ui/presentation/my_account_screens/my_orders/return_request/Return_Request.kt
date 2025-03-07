package com.pc.genzwardrobe.ui.presentation.my_account_screens.my_orders.return_request

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.components.Radio_Button_Item
import com.pc.genzwardrobe.ui.presentation.components.Radio_Button_List
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.my_account_screens.MyAccountsViewModel
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Delivery_Address_Card
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Custom_Card
import com.pc.genzwardrobe.utils.Utils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Return_Request(
    name: String?,
    price: Int?,
    image: String?,
    newOrderId: String?,
    newItemId: Int?,
    onNavBackClicked: () -> Unit,
    afterSuccessful: () -> Unit,
    modifier: Modifier = Modifier
) {
    val viewModel: MyAccountsViewModel = hiltViewModel()

    val cartViewModel: CartViewModel = hiltViewModel()
    val homeScreenViewModel: HomeScreenViewModel = hiltViewModel()

    val getAllAddress = homeScreenViewModel.getAllUserAddress.collectAsState()
    val selectedAddressId = cartViewModel.selectedAddressId.collectAsState()

    var selectedAddress by remember { mutableStateOf<UserAddress?>(null) }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    var currentStep by remember { mutableIntStateOf(0) }

    var isReasonSelected by remember { mutableStateOf(false) }
    var isMoreDetailSelected by remember { mutableStateOf(false) }
    var isRefundOrExchangeSelected by remember { mutableStateOf(false) }

    var selectedReasonIndex by remember { mutableIntStateOf(-1) }
    var refundOrExchangeIndex by remember { mutableIntStateOf(-1) }
    var refundIndex by remember { mutableIntStateOf(-1) }
    var isRefundSelected by remember { mutableStateOf(false) }

    var confirmReturnOrExchange by remember { mutableStateOf(false) }

    val newOrderStatus = if (refundOrExchangeIndex == 0) 8 else 2

    if (confirmReturnOrExchange) {
        AlertDialog(
            onDismissRequest = { confirmReturnOrExchange = false },
            title = {
                Text("Are you sure??")
            },
            dismissButton = {
                TextButton(
                    onClick = { confirmReturnOrExchange = false }
                ) {
                    Text("No")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateOrderStatus(
                            orderId = newOrderId!!,
                            itemId = newItemId!!,
                            orderStatus = newOrderStatus
                        )
                        confirmReturnOrExchange = false
                        afterSuccessful()
                    }
                ) {
                    Text("Yes")
                }
            }
        )
    }

    LaunchedEffect(key1 = selectedAddressId.value) {
        selectedAddress = getAllAddress.value.find { it.id == selectedAddressId.value }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Return request",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    if (currentStep == 0) {
                        currentStep++
                    } else {
                        confirmReturnOrExchange = true
                    }
                },
                shape = RectangleShape,
                enabled = if (currentStep == 0) {
                    isMoreDetailSelected
                } else {
                    if (refundOrExchangeIndex == 1) {
                        isRefundOrExchangeSelected
                    } else {
                        isRefundSelected
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Continue",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    ) { paddingValues ->
        ConstraintLayout(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 8.dp)
                .verticalScroll(rememberScrollState())
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            val (itemDetails, reason,
                moreDetails, refundOrExchange,
                refundAmount, pickUpOrDrop,
                returnDescription) = createRefs()

            Custom_Card(
                modifier = Modifier
                    .constrainAs(itemDetails) {
                        start.linkTo(parent.start)
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(bottom = 8.dp),
                content = {
                    Product_Details(
                        text = name ?: "Product name",
                        price = price ?: 0,
                        image = image ?: "Image Uri"
                    )
                }
            )
            when (currentStep) {
                0 -> {
                    Custom_Card(
                        modifier = Modifier
                            .constrainAs(reason) {
                                start.linkTo(parent.start)
                                top.linkTo(itemDetails.bottom)
                                end.linkTo(parent.end)
                            }
                            .padding(bottom = 8.dp),
                        content = {
                            Return_Reason(
                                onReasonClicked = { itemIndex ->
                                    isReasonSelected = true
                                    selectedReasonIndex = itemIndex
                                    Utils.showToast(context, "Clicked on $itemIndex")
                                }
                            )
                        }
                    )

                    if (isReasonSelected) {
                        Custom_Card(
                            modifier = Modifier.constrainAs(moreDetails) {
                                start.linkTo(parent.start)
                                top.linkTo(reason.bottom)
                                end.linkTo(parent.end)
                            },
                            content = {
                                More_Details(
                                    selectedReason = selectedReasonIndex,
                                    onMoreDetailsClicked = {
                                        isMoreDetailSelected = true
                                        Utils.showToast(context, "Clicked on $it")
                                    }
                                )
                            }
                        )
                    }
                }

                1 -> {
                    Custom_Card(
                        modifier = Modifier
                            .constrainAs(refundOrExchange) {
                                start.linkTo(parent.start)
                                top.linkTo(itemDetails.bottom)
                                end.linkTo(parent.end)
                            }
                            .padding(bottom = 8.dp),
                        content = {
                            Radio_Button_List(
                                title = "What do you want in return?",
                                selectedIndex = refundOrExchangeIndex,
                                onClicked = {
                                    refundOrExchangeIndex = it
                                    isRefundOrExchangeSelected = true
                                },
                                buttonTexts = listOf("Refund", "Exchange")
                            )
                        }
                    )
                    if (refundOrExchangeIndex == 0) {
                        Custom_Card(
                            modifier = Modifier
                                .constrainAs(refundAmount) {
                                    start.linkTo(parent.start)
                                    top.linkTo(refundOrExchange.bottom)
                                    end.linkTo(parent.end)
                                }
                                .padding(bottom = 8.dp),
                            content = {
                                Refund(
                                    amount = price ?: 0,
                                    selectedIndex = refundIndex,
                                    onItemClicked = { index ->
                                        refundIndex = index
                                        isRefundSelected = true
                                    },
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                                )
                            }
                        )
                    }
                    Custom_Card(
                        modifier = Modifier
                            .constrainAs(pickUpOrDrop) {
                                start.linkTo(parent.start)
                                top.linkTo(if (refundOrExchangeIndex == 0) refundAmount.bottom else refundOrExchange.bottom)
                                end.linkTo(parent.end)
                            }
                            .padding(bottom = 8.dp),
                        content = {
                            selectedAddress?.let { userAddress ->
                                Delivery_Address_Card(
                                    title = "pick up and delivery".uppercase(),
                                    userAddress = userAddress,
                                    changeButton = true,
                                    onUserAddressChange = {},
                                    onOkayClicked = {}
                                )
                            }
                        }
                    )
                    Custom_Card(
                        modifier = Modifier
                            .constrainAs(returnDescription) {
                                start.linkTo(parent.start)
                                top.linkTo(pickUpOrDrop.bottom)
                                end.linkTo(parent.end)
                            }
                            .padding(bottom = 8.dp),
                        content = {
                            Column(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    "While exchange return old product to delivery partner and accept new one",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "We hope you understand that we can only accept items fo return, if they have not been used or damaged.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    "The brand's original packaging (if present), MRP tags/labels and accessories also need to be returned.",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun Refund(
    amount: Int,
    selectedIndex: Int,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {

    val buttonTexts = listOf("GenZ Wallet", "Cash during pickup")
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val prefix = "Refund amount: "
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(prefix)
            Text(
                text = "₹$amount",
                fontWeight = FontWeight.W800
            )
        }

        buttonTexts.forEachIndexed { index, text ->
            Radio_Button_Item(
                selected = selectedIndex == index,
                text = text,
                onClicked = { onItemClicked(index) }
            )
        }
    }
}

@Composable
fun More_Details(
    selectedReason: Int,
    onMoreDetailsClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedIndex by remember { mutableIntStateOf(-1) }

    Radio_Button_List(
        title = "More details",
        selectedIndex = selectedIndex,
        onClicked = {
            selectedIndex = it
            onMoreDetailsClicked(it)
        },
        buttonTexts = Utils.moreDetailsForReturn(selectedReason),
        modifier = modifier
    )
}

@Composable
fun Return_Reason(
    onReasonClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedText by remember { mutableIntStateOf(-1) }

    Radio_Button_List(
        title = "Reason for return",
        selectedIndex = selectedText,
        onClicked = { index ->
            selectedText = index
            onReasonClicked(index)
        },
        buttonTexts = Utils.returnReasonTexts,
        modifier = modifier
    )
}



@Composable
fun Product_Details(
    text: String,
    price: Int,
    image: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = text,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = "₹ $price",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.W700
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Product_Image(
                image = image,
                modifier = Modifier.size(80.dp)
            )
        }
    }
}

package com.pc.genzwardrobe.ui.presentation.cart_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pc.genzwardrobe.core.domain.UserAddress
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProducts
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Custom_Dropdown_Menu_Box
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Address_Details
import com.pc.genzwardrobe.ui.presentation.order_placing_screens.Delivery_Address_Card
import com.pc.genzwardrobe.ui.presentation.product_details_screen.BottomBarButton
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Custom_Card
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Cart_Screen(
    cartViewModel: CartViewModel,
    homeScreenViewModel: HomeScreenViewModel,
    onItemClicked: (String, String) -> Unit,
    isAddressNotSaved: () -> Unit,
    isAddressSaved: () -> Unit,
    onBuyNowClicked: (Int) -> Unit,
    onArrowBackIconClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val cartProduct = cartViewModel.getAllCartProducts.collectAsState()
    val totalOriginalPrice by cartViewModel.getTotalOriginalPrice.collectAsState(0)
    val totalDiscountedPrice by cartViewModel.getTotalDiscountedPrice.collectAsState(0)
    val getAllUserAddresses by homeScreenViewModel.getAllUserAddress.collectAsState()
    val getSelectedAddressId = cartViewModel.selectedAddressId.collectAsState()
    val wishlistMaxItemId by cartViewModel.getWishlistMaxItemId.collectAsState(0)
    val currentStock = cartViewModel.getProductCurrentStock.collectAsState()

    var selectedBillingAddress by remember { mutableStateOf<UserAddress?>(null) }
    val maxId = wishlistMaxItemId ?: 0

    val deliveryCharge = if (totalDiscountedPrice in 1..499) 200 else 0
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var clickedItemId by remember { mutableStateOf("") }
    var clickedItemColor by remember { mutableStateOf("") }
    var clickedItemSize by remember { mutableStateOf("") }


    val totalAmount = totalDiscountedPrice.plus(deliveryCharge)
    val context = LocalContext.current

    LaunchedEffect(
        key1 = clickedItemId,
        key2 = clickedItemColor,
        key3 = clickedItemSize
    ) {
        cartViewModel.getProductStock(clickedItemId, clickedItemColor, clickedItemSize)
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val coroutineScope = rememberCoroutineScope()

    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }

    LaunchedEffect(key1 = getAllUserAddresses) {
        if (getAllUserAddresses.isEmpty()) {
            cartViewModel.updateSelectedAddressId(0)
        }
    }

    LaunchedEffect(key1 = getSelectedAddressId.value) {
        selectedBillingAddress = getAllUserAddresses.find { it.id == getSelectedAddressId.value }
    }

    if (sheetState.isVisible) {
        Address_Sheet_Content(
            selectedId = getSelectedAddressId.value,
            onDismissClicked = {
                coroutineScope.launch {
                    sheetState.hide()
                }
            },
            userAddress = getAllUserAddresses,
            onAddressClicked = {
                cartViewModel.updateSelectedAddressId(it)
                coroutineScope.launch {
                    sheetState.hide()
                }
            },
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "My Cart",
                onIconClicked = {
                    onArrowBackIconClicked()
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            if (cartProduct.value.isNotEmpty()) {
                BottomAppBar(
                    modifier = Modifier.height(72.dp),
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    CartBottomBar(
                        totalOriginalPrice = totalOriginalPrice,
                        totalDiscountedPrice = totalAmount,
                        buttonText = "Place Order",
                        onPlaceOrderClicked = {
                            if (getSelectedAddressId.value == 0) {
                                isAddressNotSaved()
                            } else {
                                isAddressSaved()
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValue ->
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValue)
                .background(MaterialTheme.colorScheme.background)
                .nestedScroll(scrollBehavior.nestedScrollConnection),
        ) {
            if (cartProduct.value.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        text = "Your cart is empty!",
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            } else {
                isLoading = true
                Cart_Items_List(
                    cartProduct = cartProduct.value,
                    onChangeAddressClicked = {
                        coroutineScope.launch {
                            sheetState.show()
                        }
                    },
                    userAddress = selectedBillingAddress,
                    totalOriginalPrice = totalOriginalPrice,
                    totalDiscountedPrice = totalDiscountedPrice,
                    deliveryCharge = deliveryCharge,
                    totalAmount = totalAmount,
                    onSelectAddressClicked = {
                        coroutineScope.launch { sheetState.show() }
                    },
                    content = {
                        val originalPrice =
                            it.productQuantity?.let { quantity -> it.originalPrice?.times(quantity) }
                        val productPrice =
                            it.productQuantity?.let { it1 -> it.discountPrice?.times(it1) }

                        CartProductItem(
                            onItemClicked = {
                                onItemClicked(it.variantId ?: "", it.variantColor ?: "")
                            },
                            variantName = it.variantName.toString(),
                            variantSize = it.size,
                            variantDiscount = it.discount.toString(),
                            variantOriginalPrice = originalPrice.toString(),
                            variantDiscountedPrice = productPrice?.toInt().toString(),
                            cartProductImage = it.productImageUri.toString(),
                            selectedOption = it.productQuantity.toString(),
                            onSelectedOptionChanged = { newSelectableQuantity ->
                                clickedItemId = it.variantId!!
                                clickedItemColor = it.variantColor!!
                                clickedItemSize = it.size

                                if (newSelectableQuantity.toInt() <= currentStock.value) {
                                    cartViewModel.updateCartProductQuantity(
                                        it.variantId.toString(),
                                        it.variantColor.toString(),
                                        it.size,
                                        newSelectableQuantity.toInt()
                                    )
                                } else {
                                    Utils.showToast(context, "No more stock")
                                }
                            },
                            onRemoveClicked = {
                                cartViewModel.deleteProductFromCartById(it.itemId)
                            },
                            onBuyNowClicked = {
                                Utils.checkAddressStatus(
                                    itemId = it.itemId,
                                    getAllUserAddresses = getAllUserAddresses,
                                    isAddressNotSaved = { isAddressNotSaved() },
                                    isAddressSaved = { itemId ->
                                        onBuyNowClicked(itemId)
                                    }
                                )
                            },
                            onSaveLaterClicked = {
                                val wishListItem = WishlistProducts(
                                    itemId = maxId + 1,
                                    variantId = it.variantId,
                                    variantName = it.variantName,
                                    variantColor = it.variantColor,
                                    productImageUri = it.productImageUri,
                                    productQuantity = it.productQuantity,
                                    size = it.size,
                                    originalPrice = it.originalPrice,
                                    discount = it.discount,
                                    discountPrice = it.discountPrice,
                                    productGender = it.productGender,
                                    productCategory = it.productCategory,
                                    productType = it.productType,
                                    orderStatus = it.orderStatus
                                )
                                cartViewModel.addProductInWishlist(wishListItem)
                                cartViewModel.deleteProductFromCartById(it.itemId)
                            }
                        )
                    }
                )
                isLoading = false
            }
        }
    }
}

@Composable
fun Cart_Items_List(
    userAddress: UserAddress?,
    cartProduct: List<CartProducts>?,
    onChangeAddressClicked: () -> Unit,
    onSelectAddressClicked: () -> Unit,
    totalOriginalPrice: Int,
    totalDiscountedPrice: Int,
    deliveryCharge: Int,
    totalAmount: Int,
    content: @Composable (CartProducts) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.background(Color.LightGray)
    ) {
        if (cartProduct.isNullOrEmpty()) {
            Text("No items in the cart")
        } else {
            LazyColumn(
                modifier = modifier,
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                item {
                    Custom_Card(
                        content = {
                            if (userAddress != null) {
                                Delivery_Address_Card(
                                    title = "Delivery to:",
                                    userAddress = userAddress,
                                    changeButton = true,
                                    onUserAddressChange = {
                                        onChangeAddressClicked()
                                    },
                                    onOkayClicked = {}
                                )
                            } else {
                                Custom_Card(
                                    content = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(
                                                start = 16.dp
                                            ),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Select Delivery Address"
                                            )
                                            TextButton(
                                                onClick = { onSelectAddressClicked() },
                                                colors = ButtonDefaults.textButtonColors(
                                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                                    containerColor = MaterialTheme.colorScheme.primary
                                                ),
                                                shape = RoundedCornerShape(14.dp),
                                                contentPadding = PaddingValues(horizontal = 12.dp),
                                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                            ) {
                                                Text(
                                                    text = "Select"
                                                )
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
                items(cartProduct) {
                    content(it)
                }
                item {
                    if (cartProduct.isNotEmpty()) {
                        Price_Details_Card(
                            totalPrice = totalOriginalPrice,
                            discountPrice = totalOriginalPrice.minus(totalDiscountedPrice),
                            deliveryCharge = if (deliveryCharge == 0) "FREE Delivery" else "$deliveryCharge",
                            itemCount = cartProduct.size,
                            totalAmount = totalAmount
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Address_List(
    selectedId: Int,
    userAddress: List<UserAddress>,
    onClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(userAddress) {
            Address_Item(
                selectedId = selectedId,
                userAddress = it,
                onClicked = { id ->
                    onClicked(id)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
            HorizontalDivider(thickness = 2.dp)
        }
    }
}

@Composable
fun Address_Item(
    selectedId: Int,
    userAddress: UserAddress,
    onClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val isSelected = userAddress.id == selectedId
    Row(modifier = modifier
            .fillMaxWidth()
            .clickable { onClicked(userAddress.id!!) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Address_Details(
                userAddress = userAddress
            )
        }
        Spacer(Modifier.width(6.dp))
        RadioButton(
            selected = isSelected,
            onClick = {  },

        )
    }
}

@Composable
fun Price_Details_Card(
    totalPrice: Int,
    discountPrice: Int,
    itemCount: Int,
    deliveryCharge: String,
    totalAmount: Int,
    modifier: Modifier = Modifier
) {
    Custom_Card(
        modifier = modifier,
        content = {
            Column(
                modifier = Modifier.padding(13.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Price Details",
                        fontWeight = FontWeight.Bold
                    )
                }
                Price_Details(
                    itemCount = itemCount,
                    totalPrice,
                    discountPrice,
                    totalAmount,
                    deliveryCharge
                )
            }
        }
    )
}

@Composable
fun Price_Details(
    itemCount: Int,
    totalPrice: Int,
    discountPrice: Int,
    totalAmount: Int,
    deliveryCharge: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "Price ($itemCount items)",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Discount",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Delivery Charges",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "₹$totalPrice",
                    style = MaterialTheme.typography.titleLarge

                )
                Text(
                    "- ₹$discountPrice",
                    color = Color.Green,
                    style = MaterialTheme.typography.titleLarge

                )
                Text(
                    text = deliveryCharge,
                    color = Color.Green,
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
        Horizontal_Dashed_Divider()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Total Amount",
                style = MaterialTheme.typography.titleLarge

            )
            Text(
                "₹$totalAmount",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Composable
fun CartProductItem(
    selectedOption: String,
    onSelectedOptionChanged: (String) -> Unit,
    cartProductImage: String,
    variantName: String,
    variantSize: String,
    variantDiscount: String,
    variantOriginalPrice: String,
    variantDiscountedPrice: String,
    onRemoveClicked: () -> Unit,
    onBuyNowClicked: () -> Unit,
    onSaveLaterClicked: () -> Unit,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectableOption = listOf("1", "2", "3", "4")

    Custom_Card(
        modifier = modifier,
        content = {
            Cart_Product_Item(
                onItemClicked = { onItemClicked() },
                cartProductImage = cartProductImage,
                selectedOption = selectedOption,
                selectableOption = selectableOption,
                onSelectedOptionChanged = {
                    onSelectedOptionChanged(it)
                },
                variantName = variantName,
                variantDiscount = variantDiscount,
                variantSize = variantSize,
                variantDiscountedPrice = variantDiscountedPrice,
                variantOriginalPrice = variantOriginalPrice
            )
            Spacer(Modifier.height(4.dp))
            HorizontalDivider(thickness = 2.dp, color = Color.Black)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Buttons(
                    onRemoveClicked = { onRemoveClicked() },
                    icon = Icons.Default.Delete,
                    text = "Remove"
                )
                VerticalDivider(thickness = 2.dp, color = Color.Black)
                Buttons(
                    onRemoveClicked = { onSaveLaterClicked() },
                    icon = Icons.Default.Star,
                    text = "Save for later"
                )
                VerticalDivider(thickness = 2.dp, color = Color.Black)
                Buttons(
                    onRemoveClicked = { onBuyNowClicked() },
                    icon = Icons.Default.ThumbUp,
                    text = "Buy this now"
                )
            }
        }
    )
}

@Composable
fun Cart_Product_Item(
    cartProductImage: String,
    selectedOption: String,
    selectableOption: List<String>,
    onSelectedOptionChanged: (String) -> Unit,
    variantName: String,
    variantDiscount: String,
    variantSize: String,
    variantDiscountedPrice: String,
    variantOriginalPrice: String,
    onItemClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isQuantityExpanded by remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, top = 32.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Card(
                onClick = { onItemClicked() },
                modifier = Modifier.size(92.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.LightGray
                )
            ) {
                Product_Image(cartProductImage)
            }
            Custom_Dropdown_Menu_Box(
                isExpanded = isQuantityExpanded,
                onExpandChange = { isQuantityExpanded = !isQuantityExpanded },
                selectableOptions = selectableOption,
                selectedOption = selectedOption,
                onSelectedOptionChanged = {
                    onSelectedOptionChanged(it)
                },
                modifier = Modifier.width(80.dp)
            )
        }
        Cart_Item_Details(
            variantName = variantName,
            variantSize = variantSize,
            variantDiscount = variantDiscount,
            variantOriginalPrice = variantOriginalPrice,
            variantDiscountedPrice = variantDiscountedPrice
        )
    }
}

@Composable
fun Cart_Item_Details(
    variantName: String,
    variantSize: String,
    variantDiscount: String,
    variantOriginalPrice: String,
    variantDiscountedPrice: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = variantName.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = "Size: $variantSize",
            style = MaterialTheme.typography.titleLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$variantDiscount%",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = variantOriginalPrice,
                textDecoration = TextDecoration.LineThrough,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "₹${variantDiscountedPrice}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
@Composable
fun CartBottomBar(
    buttonText: String,
    totalOriginalPrice: Int,
    totalDiscountedPrice: Int,
    onPlaceOrderClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "$totalOriginalPrice",
                style = MaterialTheme.typography.headlineMedium,
                textDecoration = TextDecoration.LineThrough
            )
            Text(
                text = "₹$totalDiscountedPrice",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        BottomBarButton(
            onButtonClicked = { onPlaceOrderClicked() },
            buttonText = buttonText,
            modifier = Modifier
                .width(180.dp)
                .height(50.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentColor = Color.Black,
            containerColor = Color.White
        )
    }
}

@Composable
fun Buttons(
    onRemoveClicked: () -> Unit,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    TextButton(
        modifier = modifier,
        onClick = { onRemoveClicked() },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = ""
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 13.sp
        )
    }
}

@Composable
fun Horizontal_Dashed_Divider(
    modifier: Modifier = Modifier
) {
    val pathEffect = PathEffect.dashPathEffect(
        intervals = floatArrayOf(20f, 10f),
        phase = 0f
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.Gray,
            strokeWidth = 5f,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Address_Sheet_Content(
    selectedId: Int,
    onDismissClicked: () -> Unit,
    userAddress: List<UserAddress>,
    onAddressClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = {
            onDismissClicked()
        },
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f),
        containerColor = Color.White,
        contentColor = Color.Black
    ) {
        Address_List(
            selectedId = selectedId,
            userAddress = userAddress,
            onClicked = {
                onAddressClicked(it)
            }
        )
    }
}
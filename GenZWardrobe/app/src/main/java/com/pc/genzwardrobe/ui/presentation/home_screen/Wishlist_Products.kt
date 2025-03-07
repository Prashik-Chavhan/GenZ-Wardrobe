package com.pc.genzwardrobe.ui.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.data.local.wishlistProducts.WishlistProducts
import com.pc.genzwardrobe.ui.presentation.cart_screen.Buttons
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.cart_screen.Cart_Item_Details
import com.pc.genzwardrobe.ui.presentation.cart_screen.WishlistUiState
import com.pc.genzwardrobe.ui.presentation.components.Circular_Loader
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.product_details_screen.Custom_Card

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Wishlist_Products(
    modifier: Modifier = Modifier,
    onItemClicked: (String, String) -> Unit,
    onBackClicked: () -> Unit
) {
    val viewModel: CartViewModel = hiltViewModel()

    val wishlistProducts = viewModel.getAllWishlistProducts.collectAsState()
    val maxCartItemId = viewModel.getCartMaxItemId.collectAsState(0)

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var isLoading by remember { mutableStateOf(false) }

    val maxItemId = maxCartItemId.value ?: 0

    LaunchedEffect(Unit) {
        viewModel.getAllWishListProducts()
    }

    if (isLoading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Dialog(
                onDismissRequest = {},
                properties = DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false
                )
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator()
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Please wait...",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Wishlist",
                onIconClicked = { onBackClicked() },
                scrollBehavior = scrollBehavior
            )
        },
        modifier = modifier
    ) {
        when (wishlistProducts.value) {
            is WishlistUiState.Loading -> {
                Circular_Loader(modifier)
            }

            is WishlistUiState.Success -> {
                val success = wishlistProducts.value as WishlistUiState.Success
                if (success.items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("You haven't wishlist an item yet")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it)
                            .background(color = MaterialTheme.colorScheme.background)
                            .nestedScroll(scrollBehavior.nestedScrollConnection)
                    ) {
                        items(success.items) { wishlistProduct ->
                            Wishlist_Item(
                                onItemClicked = { productId, color ->
                                    onItemClicked(productId, color)
                                },
                                wishlistProducts = wishlistProduct,
                                onRemoveClicked = {
                                    isLoading = true
                                    viewModel.deleteWishlistItem(wishlistProduct)
                                    isLoading = false
                                },
                                onAddClicked = {
                                    val cartProduct = CartProducts(
                                        itemId = maxItemId.plus(1),
                                        variantId = wishlistProduct.variantId,
                                        variantName = wishlistProduct.variantName,
                                        variantColor = wishlistProduct.variantColor,
                                        productImageUri = wishlistProduct.productImageUri,
                                        productQuantity = wishlistProduct.productQuantity,
                                        size = wishlistProduct.size,
                                        originalPrice = wishlistProduct.originalPrice,
                                        discount = wishlistProduct.discount,
                                        discountPrice = wishlistProduct.discountPrice,
                                        productGender = wishlistProduct.productGender,
                                        productCategory = wishlistProduct.productCategory,
                                        productType = wishlistProduct.productType,
                                        orderStatus = wishlistProduct.orderStatus
                                    )
                                    viewModel.addProductInCart(cartProduct)
                                    viewModel.deleteWishlistItem(wishlistProduct)
                                },
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            is WishlistUiState.Error -> {
                val error = wishlistProducts.value as WishlistUiState.Error
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Error: ${error.message}")
                }
            }

            else -> {}
        }
    }
}

@Composable
fun Wishlist_Item(
    wishlistProducts: WishlistProducts,
    onItemClicked: (String, String) -> Unit,
    onRemoveClicked: () -> Unit,
    onAddClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Custom_Card(
        modifier = modifier,
        content = {
            Row(
                modifier = Modifier
                    .clickable { onItemClicked(wishlistProducts.variantId ?: "", wishlistProducts.variantColor ?: "") }
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Product_Image(
                        wishlistProducts.productImageUri ?: "",
                        modifier = Modifier.size(90.dp)
                    )
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Cart_Item_Details(
                        variantName = wishlistProducts.variantName ?: "Variant name",
                        variantSize = wishlistProducts.size,
                        variantDiscount = wishlistProducts.discount.toString(),
                        variantOriginalPrice = wishlistProducts.originalPrice.toString(),
                        variantDiscountedPrice = wishlistProducts.discountPrice?.toInt().toString()
                    )
                }
            }
            HorizontalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onErrorContainer)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Buttons(
                    onRemoveClicked = { onRemoveClicked() },
                    text = "Remove",
                    icon = Icons.Default.Clear
                )
                VerticalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
                Buttons(
                    onRemoveClicked = { onAddClicked() },
                    text = "Add to Cart",
                    icon = Icons.Default.ShoppingCart
                )
            }
        }
    )
}
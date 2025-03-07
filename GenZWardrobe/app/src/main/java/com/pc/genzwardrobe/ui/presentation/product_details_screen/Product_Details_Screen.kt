package com.pc.genzwardrobe.ui.presentation.product_details_screen

import android.content.Context
import android.widget.Space
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Discount
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TripOrigin
import androidx.compose.material.icons.outlined.Verified
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pc.genzwardrobe.core.domain.UserReview
import com.pc.genzwardrobe.core.domain.products.ProductHighlight
import com.pc.genzwardrobe.core.domain.products.ProductVariant
import com.pc.genzwardrobe.data.local.cartProducts.CartProducts
import com.pc.genzwardrobe.ui.presentation.cart_screen.CartViewModel
import com.pc.genzwardrobe.ui.presentation.components.Circular_Loader
import com.pc.genzwardrobe.ui.presentation.components.Product_Image
import com.pc.genzwardrobe.ui.presentation.components.Product_Item
import com.pc.genzwardrobe.ui.presentation.home_screen.Drawer_Animated_Content
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import com.pc.genzwardrobe.utils.Utils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Product_Details_Screen(
    productId: String?,
    variantColor: String?,
    homeScreenViewModel: HomeScreenViewModel,
    cartViewModel: CartViewModel,
    isAddressNotSaved: () -> Unit,
    isAddressSaved: (Int) -> Unit,
    goToCart: () -> Unit,
    onAllDetailsClicked: (String) -> Unit,
    onAllSimilarClicked: (String, String, String, String) -> Unit,
    onSimilarItemClicked: (String, String) -> Unit,
    onRateProductClicked: (String, String) -> Unit,
    onAllReviewsClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val variants = homeScreenViewModel.productDetails.collectAsState()
    val similarVariants = homeScreenViewModel.getSimilarVariants.collectAsState()
    val productReviews = homeScreenViewModel.getProductReviews.collectAsLazyPagingItems()
    val getAllUserAddresses = homeScreenViewModel.getAllUserAddress.collectAsState()
    val getCartSize = cartViewModel.getAllCartProducts.collectAsState()

    val context = LocalContext.current
    var selectedVariant by remember { mutableStateOf(variantColor) }
    val coroutineScope = rememberCoroutineScope()

    var selectedSize by remember { mutableStateOf("") }
    var priceBySelectedSize by remember { mutableStateOf("") }
    var highlightExpanded by remember { mutableStateOf(true) }

    val sizeSelectBottomSheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isLoading by remember { mutableStateOf(false) }

    var totalRatings by remember { mutableIntStateOf(0) }
    val ratingCount = productReviews.itemSnapshotList.items.size

    LaunchedEffect(
        key1 = productId,
    ) {
        isLoading = true
        delay(2000L)
        homeScreenViewModel.getProductDetails(productId!!)
        isLoading = false
    }

    LaunchedEffect(
        key1 = productReviews,
        key2 = productId
    ) {
        homeScreenViewModel.getProductReviews(productId!!, sortBy = "Default")
        totalRatings = productReviews.itemSnapshotList.items.sumOf { it.second.rating }
    }

    if (isLoading) {
        Circular_Loader(modifier)
    } else {

        variants.value?.let { product ->

            val currentVariant = product.productVariants.values.find { it.color == selectedVariant }

            LaunchedEffect(
                key1 = selectedVariant
            ) {
                homeScreenViewModel.getSimilarVariants(
                    selectedVariant!!,
                    product.productGender!!,
                    product.productCategory!!,
                    product.productType!!
                )
            }


            currentVariant?.let { variant ->
                val discountedPrice =
                    variant.originalPrice?.times(100 - variant.discount!!)?.div(100)
                val discount = discountedPrice?.times(100)?.div(variant.originalPrice)

                val priceByCondition =
                    if (priceBySelectedSize.isEmpty()) "₹$discountedPrice" else "₹$priceBySelectedSize"

                val productBrandName = product.productBrand?.uppercase()

                val cartProduct = product.productId?.let {
                    CartProducts(
                        itemId = getCartSize.value.size + 1,
                        variantId = it,
                        variantName = variant.variantName,
                        productQuantity = 1,
                        originalPrice = variant.originalPrice,
                        discountPrice = priceBySelectedSize.toDoubleOrNull(),
                        productImageUri = variant.variantImages[0],
                        size = selectedSize,
                        variantColor = variant.color,
                        discount = 100 - discount!!,
                        productGender = product.productGender,
                        productCategory = product.productCategory,
                        productType = product.productType,
                        orderStatus = 0
                    )
                }
                val isProductInCart = cartViewModel.isProductInCart(
                    productId = product.productId!!,
                    color = variant.color,
                    size = selectedSize
                )
                var itemId = 0
                coroutineScope.launch {
                    val id = cartViewModel.getItemId(
                        variantId = product.productId,
                        variantSize = selectedSize,
                        variantColor = selectedVariant!!
                    ).first()
                    itemId = id
                }
                val buttonText = if (isProductInCart) "Go to cart" else "Add to cart"

                if (sizeSelectBottomSheet.isVisible) {
                    ModalBottomSheet(
                        modifier = Modifier,
                        onDismissRequest = {
                            coroutineScope.launch {
                                sizeSelectBottomSheet.hide()
                            }
                        },
                        containerColor = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(0.4f)
                                .padding(horizontal = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            sizeSelectBottomSheet.hide()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = ""
                                    )
                                }
                                Text(
                                    text = "Select Size",
                                    style = MaterialTheme.typography.headlineMedium
                                )
                            }
                            HorizontalDivider(thickness = 2.dp, color = Color.Black)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Product_Image(
                                    image = variant.variantImages[0]!!,
                                    modifier = Modifier.size(90.dp)
                                )
                                Spacer(Modifier.width(12.dp))
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = product.productBrand ?: "Brand Name",
                                        color = Color.LightGray
                                    )
                                    Text(
                                        text = variant.variantName ?: "Variant name",
                                        maxLines = 1,
                                        fontSize = 17.sp
                                    )
                                    Row {
                                        Text(
                                            text = "${variant.originalPrice!!}",
                                            fontSize = 18.sp,
                                            textDecoration = TextDecoration.LineThrough
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = priceByCondition,
                                            fontSize = 18.sp
                                        )
                                    }
                                }
                            }
                            Product_Size_Selector(
                                variant = variant,
                                selectedSize = selectedSize,
                                onSizeSelected = { size, price ->
                                    selectedSize = size
                                    priceBySelectedSize = price
                                }
                            )

                            Button(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                onClick = {
                                    if (cartProduct != null) {
                                        cartViewModel.addProductInCart(cartProduct)
                                        Utils.checkAddressStatus(
                                            itemId = cartProduct.itemId,
                                            getAllUserAddresses = getAllUserAddresses.value,
                                            isAddressNotSaved = { isAddressNotSaved() },
                                            isAddressSaved = {
                                                isAddressSaved(it)
                                            }
                                        )
                                    }
                                    Utils.showToast(context, "Item added to cart")
                                    coroutineScope.launch {
                                        sizeSelectBottomSheet.hide()
                                    }
                                },
                                enabled = selectedSize.isNotEmpty(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedSize.isNotEmpty()) MaterialTheme.colorScheme.secondary else Color.LightGray,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Continue",
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }

                Scaffold(
                    topBar = {

                    },
                    bottomBar = {
                        BottomAppBar(
                            modifier = Modifier.height(56.dp)
                        ) {
                            BottomBarButton(
                                onButtonClicked = {
                                    if (selectedSize.isEmpty()) {
                                        coroutineScope.launch {
                                            sizeSelectBottomSheet.show()
                                        }
                                    } else {
                                        // Adding product to Room Database
                                        if (isProductInCart) {
                                            goToCart()
                                        } else {
                                            if (cartProduct != null) {
                                                cartViewModel.addProductInCart(cartProduct)
                                            }
                                            Utils.showToast(context, "Item added to cart")
                                        }
                                    }
                                },
                                containerColor = Color.Yellow,
                                contentColor = Color.Black,
                                buttonText = buttonText,
                                modifier = Modifier.weight(0.5f)
                            )
                            BottomBarButton(
                                onButtonClicked = {
                                    if (selectedSize.isEmpty()) {
                                        coroutineScope.launch {
                                            sizeSelectBottomSheet.show()
                                        }
                                    } else {
                                        if (isProductInCart) {
                                            Utils.checkAddressStatus(
                                                itemId = itemId,
                                                getAllUserAddresses = getAllUserAddresses.value,
                                                isAddressNotSaved = { isAddressNotSaved() },
                                                isAddressSaved = {
                                                    isAddressSaved(it)
                                                }
                                            )
                                        } else {
                                            if (cartProduct != null) {
                                                cartViewModel.addProductInCart(cartProduct)
                                                Utils.checkAddressStatus(
                                                    itemId = cartProduct.itemId,
                                                    getAllUserAddresses = getAllUserAddresses.value,
                                                    isAddressNotSaved = { isAddressNotSaved() },
                                                    isAddressSaved = {
                                                        isAddressSaved(it)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                },
                                containerColor = Color.White,
                                contentColor = Color.Black,
                                buttonText = "Buy now",
                                modifier = Modifier.weight(0.5f)
                            )
                        }
                    }
                ) { paddingValue ->
                    Column(
                        modifier = modifier
                            .fillMaxSize()
                            .padding(paddingValue)
                            .background(color = Color.LightGray)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.elevatedCardColors(
                                containerColor = MaterialTheme.colorScheme.background,
                                contentColor = MaterialTheme.colorScheme.onBackground
                            ),
                            shape = RectangleShape
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                LazyRow(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    items(variant.variantImages.size) {
                                        Product_Image(
                                            image = variant.variantImages[it]!!,
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier
                                                .height(420.dp)
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp)
                                        .padding(bottom = 12.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = productBrandName + " ${variant.variantName}",
                                        style = MaterialTheme.typography.titleLarge,
                                        maxLines = 2
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "Color: " + variant.color,
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                        Text(
                                            "Available color: ${product.productVariants.size}",
                                            style = MaterialTheme.typography.titleMedium,
                                        )
                                    }
                                    LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        items(product.productVariants.values.toList()) { item ->
                                            val isSelected = item.color == selectedVariant
                                            Card(
                                                modifier = Modifier
                                                    .size(80.dp)
                                                    .clickable { selectedVariant = item.color },
                                                shape = RoundedCornerShape(6.dp),
                                                border = BorderStroke(
                                                    width = if (isSelected) 3.dp else 0.dp,
                                                    color = if (isSelected) Color.DarkGray else Color.Transparent
                                                ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color.White
                                                ),
                                                elevation = CardDefaults.cardElevation(
                                                    defaultElevation = 4.dp
                                                )
                                            ) {
                                                AsyncImage(
                                                    model = ImageRequest.Builder(context)
                                                        .data(item.variantImages[0])
                                                        .build(),
                                                    contentDescription = "",
                                                    contentScale = ContentScale.Crop,
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .padding(horizontal = 10.dp),
                                                    alignment = Alignment.Center,
                                                )
                                            }
                                        }
                                    }

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Row (
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Icon(
                                                imageVector = Icons.Outlined.Discount,
                                                contentDescription = "",
                                                tint = Color.Green
                                            )
                                            Text(
                                                text = if (selectedSize.isEmpty()) "${variant.discount!!}%" else "${100 - discount!!}%",
                                                color = Color.Green,
                                                fontSize = 25.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                        Text(
                                            text = "${variant.originalPrice}",
                                            textDecoration = TextDecoration.LineThrough,
                                            fontSize = 25.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = priceByCondition,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 25.sp
                                        )
                                    }
                                }
                            }
                        }
                        Custom_Card(
                            content = {
                                Column(
                                    modifier = Modifier.padding(10.dp),
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            "Size",
                                            style = MaterialTheme.typography.headlineSmall
                                        )
                                        TextButton(onClick = {}) {
                                            Text(
                                                "Size Chart",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    Product_Size_Selector(
                                        variant = variant,
                                        selectedSize = selectedSize,
                                        onSizeSelected = { size, price ->
                                            selectedSize = size
                                            priceBySelectedSize = price
                                        }
                                    )

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.Policy,
                                            contentDescription = "",
                                            modifier = Modifier.size(34.dp),
                                            tint = MaterialTheme.colorScheme.onSecondary
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            text = "Don't worry, we have a 10-day rerun policy on this item.",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSecondary
                                        )
                                    }
                                }
                            }
                        )

                        Custom_Card(
                            content = {
                                Product_Info(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp))
                            }
                        )

                        if (similarVariants.value.isNotEmpty()) {
                            Custom_Card(
                                content = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Similar Products",
                                                style = MaterialTheme.typography.titleLarge
                                            )
                                            IconButton(
                                                onClick = {
                                                    onAllSimilarClicked(
                                                        selectedVariant!!,
                                                        product.productGender!!,
                                                        product.productCategory!!,
                                                        product.productType!!
                                                    )
                                                }
                                            ) {
                                                Icon(
                                                    imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                                                    contentDescription = ""
                                                )
                                            }
                                        }

                                        LazyRow(
                                            modifier = Modifier
                                                .fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                                        ) {
                                            items(similarVariants.value.take(10)) { (productId, brand, productVariant) ->
                                                Product_Item(
                                                    product = productVariant,
                                                    text = brand,
                                                    onClick = {
                                                        onSimilarItemClicked(
                                                            productId,
                                                            productVariant.color
                                                        )
                                                    },
                                                    modifier = Modifier.width(180.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }

                        if (product.productHighlight.isNotEmpty()) {
                            Custom_Card(
                                content = {
                                    Drawer_Animated_Content(
                                        modifier = Modifier.padding(10.dp),
                                        title = "Product Highlight",
                                        isExpanded = highlightExpanded,
                                        onExpandChange = { highlightExpanded = !highlightExpanded },
                                        content = {
                                            val productHighlight =
                                                product.productHighlight.toSortedMap().values.elementAtOrNull(
                                                    0
                                                )

                                            if (productHighlight != null) {
                                                Variant_Details(
                                                    context = context,
                                                    product = productHighlight,
                                                    onAllDetailsClicked = {
                                                        onAllDetailsClicked(product.productId)
                                                    }
                                                )
                                            }
                                        }
                                    )
                                }
                            )
                        }

                        Custom_Card(
                            content = {
                                Product_Details(
                                    product.fabric,
                                    product.sleeveType,
                                    product.pattern,
                                    product.collarType,
                                    product.occasion,
                                    variant.color,
                                    product.faded,
                                    product.rise,
                                    productDetails = product.description,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp)
                                )
                            }
                        )
                        Custom_Card(
                            content = {
                                Ratings_Review(
                                    onRateProductClicked = {
                                        homeScreenViewModel.canReviewProduct( product.productId.toString(), selectedVariant.toString() ) { canReview ->
                                            if (canReview) {
                                                onRateProductClicked(productId.toString(), selectedVariant.toString())
                                            } else {
                                                Utils.showToast(
                                                    context,
                                                    "You haven't purchased this product yet"
                                                )
                                            }
                                        }
                                    },
                                    headerText = if (productReviews.itemSnapshotList.items.isEmpty()) "Be the first one to rate" else "Ratings & review",
                                    content = {
                                        if (productReviews.itemSnapshotList.items.isEmpty()) {
                                            Text(
                                                text = "No rating for this product yet",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            UserRatings(
                                                totalRatings = totalRatings,
                                                ratingCount = ratingCount,
                                                onAllReviewsClicked = {
                                                    onAllReviewsClicked(product.productId)
                                                },
                                                content = {
                                                    for (userReviews in productReviews.itemSnapshotList.items.take(2)){
                                                        val userReview = userReviews.second
                                                        Reviews_Ratings(
                                                            userReview = userReview
                                                        )
                                                        HorizontalDivider(
                                                            thickness = 2.dp,
                                                            color = MaterialTheme.colorScheme.onBackground
                                                        )
                                                    }
                                                }
                                            )
                                        }
                                    }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UserRatings(
    totalRatings: Int,
    ratingCount: Int,
    onAllReviewsClicked: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val averageRatings = if (ratingCount > 0) totalRatings.toFloat() / ratingCount else 0f

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    repeat(5) {index ->
                        val filled = index < averageRatings.toInt()
                        val halfFilled = index == averageRatings.toInt() && averageRatings % 1 != 0f
                        Icon(
                            imageVector = when {
                                filled -> Icons.Filled.Star
                                halfFilled -> Icons.AutoMirrored.Filled.StarHalf
                                else -> Icons.Outlined.Star
                            },
                            contentDescription = "Star",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier
                                .size(34.dp)
                        )
                    }
                }
                Text(
                    text = "$totalRatings ratings and $ratingCount reviews",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            VerticalDivider(thickness = 2.dp, color = MaterialTheme.colorScheme.onBackground)
            Column (
                modifier = Modifier
                    .weight(0.5f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ){
                val text = when(averageRatings) {
                    1f -> "Very Bad"
                    2f -> "Bad"
                    3f -> "Average"
                    4f -> "Good"
                    5f -> "Very Good"
                    else -> ""
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.displaySmall
                )
            }
        }
        HorizontalDivider(
            thickness = 2.dp,
            color = MaterialTheme.colorScheme.onBackground
        )
        content()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAllReviewsClicked() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "All Reviews",
                style = MaterialTheme.typography.bodyLarge
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "More Items"
            )
        }
    }
}

@Composable
fun BottomBarButton(
    onButtonClicked: () -> Unit,
    containerColor: Color,
    contentColor: Color,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = { onButtonClicked() },
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        shape = RectangleShape,
        colors = ButtonDefaults
            .buttonColors(
                containerColor = containerColor,
                contentColor = contentColor
            )
    ) {
        Text(
            text = buttonText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun Product_Size_Selector(
    variant: ProductVariant,
    selectedSize: String,
    onSizeSelected: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (sizeDetails in variant.sizeDetails) {
            val sizes = sizeDetails.key
            val stock = sizeDetails.value.stock
            val price = sizeDetails.value.price.toInt()

            item {
                val isSelected = sizes == selectedSize

                Column {
                    Button(
                        onClick = {
                            onSizeSelected(sizes, price.toString())
                        },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.White,
                            contentColor = if (isSelected) Color.White else Color.Black
                        ),
                        enabled = stock != 0,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (!isSelected) Color.Black else Color.Transparent
                        ),
                        contentPadding = PaddingValues(10.dp)
                    ) {
                        Text(
                            text = sizes,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if (isSelected && stock <= 10) {
                        Text(
                            "Only $stock left",
                            color = Color.Red,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun Variant_Details(
    product: ProductHighlight,
    context: Context,
    onAllDetailsClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Highlight_Image(
                context,
                product.image
            )
            Highlight_Text(
                product.title,
                product.description
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAllDetailsClicked() },
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "All details",
                style = MaterialTheme.typography.titleLarge
            )
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = "Right Arrow"
            )
        }
    }
}

@Composable
fun Custom_Card(
    content: @Composable () -> Unit,
    elevation: Dp = 1.dp,
    containerColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = CardDefaults.cardElevation(elevation),
        shape = RectangleShape
    ) {
        content()
    }
}

@Composable
fun Product_Highlights(
    isEven: Boolean,
    context: Context,
    product: ProductHighlight,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (isEven) {
            Highlight_Image(
                context,
                product.image
            )
            Highlight_Text(
                product.title,
                product.description
            )
        } else {
            Highlight_Text(
                product.title,
                product.description,
                modifier = Modifier.weight(0.6f)
            )
            Highlight_Image(
                context,
                product.image
            )
        }
    }
}


@Composable
fun Highlight_Text(
    title: String?,
    description: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = title ?: "Title",
            fontWeight = FontWeight.W600
        )
        Expandable_Text(
            text = description,
            minimizedMaxLines = 6
        )
    }
}

@Composable
fun Highlight_Image(
    context: Context,
    imageUri: String?,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = "",
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(125.dp)
                .border(
                    border = BorderStroke(
                        width = 3.dp,
                        color = Color.LightGray
                    ),
                    shape = CircleShape
                ),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun Ratings_Review(
    headerText: String,
    onRateProductClicked: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = headerText,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Button(
                onClick = { onRateProductClicked() },
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = Color.Blue
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = Color.Gray
                )
            ) {
                Text(
                    text = "Rate Product",
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        content()
    }
}


@Composable
fun Reviews_Ratings(
    userReview: UserReview,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(userReview.rating) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Star",
                    modifier = Modifier.size(26.dp),
                    tint = Color(0xFFFFD700)
                )
            }
            Spacer(Modifier.width(16.dp))
            Text(
                text = "${userReview.rating} - ${userReview.title}",
                style = MaterialTheme.typography.labelMedium
            )
        }
        Text(
            text = "Review for: Color - ${userReview.color}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = userReview.comment,
            style = MaterialTheme.typography.titleLarge
        )
        LazyRow(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(userReview.images) {imageUri ->
                Product_Image(
                    imageUri.toString(),
                    modifier = Modifier
                        .size(92.dp)
                )
            }
        }
        Text(
            text = userReview.userName,
            style = MaterialTheme.typography.titleLarge)
        Row {
            Icon(
                imageVector = Icons.Outlined.Verified,
                contentDescription = "Verified"
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = "Verified purchased",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun Product_Info(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Product_Detail_Item(
            icon = Icons.Outlined.TripOrigin,
            text1 = "Original",
            text2 = "Product"
        )
        VerticalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 24.dp))
        Product_Detail_Item(
            icon = Icons.Outlined.HighQuality,
            text1 = "Quality",
            text2 = "Assured"
        )
        VerticalDivider(thickness = 2.dp, modifier = Modifier.padding(vertical = 24.dp))
        Product_Detail_Item(
            icon = Icons.Outlined.VerifiedUser,
            text1 = "Verified",
            text2 = "Seller"
        )
    }
}

@Composable
fun Product_Detail_Item(
    icon: ImageVector,
    text1: String,
    text2: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            modifier = Modifier.size(56.dp)
        )
        Text(
            text = text1,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = text2,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
fun Product_Details(
    fabric: String?,
    sleeve: String?,
    pattern: String?,
    collar: String?,
    occasion: String?,
    color: String?,
    faded: String?,
    rise: String?,
    productDetails: String?,
    modifier: Modifier = Modifier
) {
    val details = listOf(
        "Pack Of" to "1",
        "Fabric" to fabric,
        "Sleeve" to sleeve,
        "Pattern" to pattern,
        "Collar" to collar,
        "Occasion" to occasion,
        "Color" to color,
        "Faded" to faded,
        "Rise" to rise
    ).filter { it.second?.isNotEmpty() == true }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Product Details",
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(12.dp))
        Row {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                details.forEach {
                    Text(
                        text = it.first,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                details.forEach {(_, text) ->
                    Text(
                        text = text.toString(),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.W600
                    )
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text(
            "Details",
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))

        Expandable_Text(
            text = productDetails
        )
    }
}

@Composable
fun Expandable_Text(
    text: String?,
    modifier: Modifier = Modifier,
    minimizedMaxLines: Int = 4
) {
    var isTextExpanded by remember { mutableStateOf(false) }
    var textLayoutResultState by remember {
        mutableStateOf<TextLayoutResult?>(null)
    }

    val textLayoutResult = textLayoutResultState
    val seeMoreText = " See More"
    val seeLessText = " See Less"
    val seeMoreLessStyle = SpanStyle(
        fontWeight = FontWeight.SemiBold,
        color = Color.Blue
    )

    val annotatedString: AnnotatedString = buildAnnotatedString {
        if (
            textLayoutResult != null &&
            !isTextExpanded && textLayoutResult.hasVisualOverflow
        ) {
            val lastCharIndex = textLayoutResult.getLineEnd(
                lineIndex = minimizedMaxLines - 1,
                visibleEnd = true
            )
            val adjustedText = text?.substring(
                startIndex = 0,
                endIndex = lastCharIndex.minus(6)
            )
            append(adjustedText)
            withStyle(style = seeMoreLessStyle) {
                append(seeMoreText)
            }
        } else {
            append(text)
            if (isTextExpanded) {
                withStyle(style = seeMoreLessStyle) {
                    append(seeLessText)
                }
            }
        }
    }

    Text(
        modifier = modifier.clickable{ isTextExpanded = !isTextExpanded },
        text = annotatedString,
        style = MaterialTheme.typography.labelMedium,
        textAlign = TextAlign.Justify,
        maxLines = if (isTextExpanded) Int.MAX_VALUE else minimizedMaxLines,
        onTextLayout = { textLayoutResultState = it },
        overflow = TextOverflow.Ellipsis
    )
}
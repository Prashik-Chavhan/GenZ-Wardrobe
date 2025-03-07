package com.pc.genzwardrobeadmin.presentation.home_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.pc.genzwardrobeadmin.core.domain.Category
import com.pc.genzwardrobeadmin.core.domain.product.ProductVariant
import com.pc.genzwardrobeadmin.presentation.MainViewModel
import com.pc.genzwardrobeadmin.utils.Utils


@Composable
fun Home_Screen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val genderUiState = viewModel.productVariantByCategory.collectAsLazyPagingItems()

    var selectedCategory by remember { mutableStateOf("") }

    LaunchedEffect(key1 = selectedCategory) {
        viewModel.fetchProductVariantsByGender(selectedCategory)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        CategoriesList(
            category = Utils.CategoryList.categoryList,
            onCategoryClicked = { selectedCategory = it.text },
            genderUiState = genderUiState
        )
    }
}


@Composable
fun ProductGrid(
    uiState: LazyPagingItems<ProductVariant>,
    modifier: Modifier = Modifier,
) {

    if (uiState.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }  else  {
        if (uiState.itemSnapshotList.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No products")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(192.dp),
                modifier = modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                items(uiState.itemSnapshotList.items) { productVariant ->
                    Product_Item(
                        product = productVariant,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                    )
                }

                when (val state = uiState.loadState.append) {
                    is LoadState.Loading -> {
                        item { CircularProgressIndicator() }
                    }

                    is LoadState.Error -> {
                        item { Text(state.error.toString()) }
                    }

                    else -> {}
                }
            }
        }
    }
}

@Composable
fun Product_Image(
    image: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(image)
            .crossfade(true)
            .build(),
        contentDescription = "Product Image",
        modifier = modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun Image_Card(
    image: String,
    productDiscount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(230.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopStart
        ) {
            Product_Image(image)
            Card(
                modifier = Modifier.padding(start = 2.dp, top = 2.dp),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 1.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            ) {
                Text(
                    text = "${productDiscount}% off",
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun Product_Item(
    product: ProductVariant,
    modifier: Modifier = Modifier
) {
    val discountedPrice =
        product.originalPrice?.times(100 - product.discount!!)?.div(100)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Image_Card(
            image = product.variantImages.firstOrNull() ?: "",
            productDiscount = product.discount ?: 0
        )

        // Variant details
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = product.variantName ?: "Variant Name",
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = product.color,
                style = MaterialTheme.typography.labelSmall,
            )

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                product.originalPrice?.let {
                    Text(
                        text = "₹${it}",
                        textDecoration = TextDecoration.LineThrough,
                        style = MaterialTheme.typography.labelMedium
                    )
                }
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "₹$discountedPrice",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}

@Composable
fun CategoriesList(
    category: List<Category>,
    onCategoryClicked: (Category) -> Unit,
    genderUiState: LazyPagingItems<ProductVariant>,
    modifier: Modifier = Modifier,
) {

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    Column(modifier = modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            category.forEachIndexed { index, category ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        onCategoryClicked(category)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = category.text,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
        ProductGrid(
            uiState = genderUiState
        )
    }
}
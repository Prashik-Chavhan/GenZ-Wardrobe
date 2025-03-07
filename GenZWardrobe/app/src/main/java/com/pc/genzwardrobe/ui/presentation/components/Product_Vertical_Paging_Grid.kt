package com.pc.genzwardrobe.ui.presentation.components

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.pc.genzwardrobe.core.domain.products.ProductVariant

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
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    contentColor = MaterialTheme.colorScheme.onTertiary
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
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val discountedPrice =
        product.originalPrice?.times(100 - product.discount!!)?.div(100)

    Column(
        modifier = modifier
            .clickable { onClick() },
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
                text = text,
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Product_Grid(
    genderProductType: LazyPagingItems<Triple<String, String, ProductVariant>>,
    onVariantClicked: (String, String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    if (genderProductType.loadState.refresh is LoadState.Loading) {
        Circular_Loader()
    } else {
        if (genderProductType.itemSnapshotList.items.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No products")
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = modifier
                    .fillMaxSize()
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
            ) {
                items(genderProductType.itemSnapshotList.items) { (productId, color, productVariant) ->
                    Product_Item(
                        product = productVariant,
                        text = color,
                        onClick = {
                            onVariantClicked(productId, color)
                        }
                    )
                }
                when (val state = genderProductType.loadState.append) {

                    is LoadState.Loading -> {
                        item {
                            Circular_Loader()
                        }
                    }

                    is LoadState.Error -> {
                        Log.d("Paging", "Error: ${state.error}")
                    }

                    else -> {}
                }

            }
        }
    }
}
package com.pc.genzwardrobe.ui.presentation.product_details_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Product_Item
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Similar_Products(
    selectedVariant: String?,
    productGender: String?,
    productCategory: String?,
    productType: String?,
    homeScreenViewModel: HomeScreenViewModel,
    onNavBackClicked: () -> Unit,
    onVariantClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    val similarVariants = homeScreenViewModel.getSimilarVariants.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()


    LaunchedEffect(
        key1 = selectedVariant
    ) {
        homeScreenViewModel.getSimilarVariants(
            selectedVariant!!, productGender!!, productCategory!!, productType!!
        )
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Similar Products",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior
            )
        }
    ) {
        LazyVerticalGrid(
            modifier = modifier
                .fillMaxSize()
                .padding(it),
            columns = GridCells.Fixed(2)
        ) {
            items(similarVariants.value) {(productId, _, productVariant) ->
                Product_Item(
                    product = productVariant,
                    text = productVariant.color,
                    onClick = { onVariantClicked(productId, productVariant.color) },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                )
            }
        }
    }
}
package com.pc.genzwardrobe.ui.presentation.product_details_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Product_Highlight(
    productId: String?,
    onBackIconClicked: () -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val productHighlight = homeScreenViewModel.getProductHighlight.collectAsState()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(key1 = productId) {
        if (productId != null) {
            isLoading = true
            delay(1500L)
            homeScreenViewModel.fetchProductHighlight(productId)
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Product Highlight",
                onIconClicked = { onBackIconClicked() },
                scrollBehavior
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                itemsIndexed(productHighlight.value) { index, item ->
                    Product_Highlights(
                        product = item,
                        isEven = index % 2 == 0,
                        context = context,
                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
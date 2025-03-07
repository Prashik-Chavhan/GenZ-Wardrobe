package com.pc.genzwardrobe.ui.presentation.product_details_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.pc.genzwardrobe.ui.presentation.components.Circular_Loader
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun All_Reviews(
    productId: String?,
    onNavBackClicked: () -> Unit,
    viewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {
    val productReviews = viewModel.getProductReviews.collectAsLazyPagingItems()

    val buttonList = listOf("Default", "Latest", "Positive", "Negative")
    var selectedSortOption by remember { mutableStateOf(buttonList[0]) }

    LaunchedEffect(
        key1 = productId,
        key2 = selectedSortOption
    ) {
        viewModel.getProductReviews(productId!!, sortBy = selectedSortOption)
    }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "All Reviews",
                onIconClicked = { onNavBackClicked() },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(vertical = 8.dp, horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                buttonList.forEach {
                    val isSelected = selectedSortOption == it
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedSortOption = it
                        },
                        label = {
                            Text(
                                text = it,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    )
                }
            }

            if (productReviews.loadState.refresh is LoadState.Loading) {
                Circular_Loader()
            } else {
                if (productReviews.itemSnapshotList.items.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No Reviews")
                    }
                } else {
                    for (userReviews in productReviews.itemSnapshotList.items) {
                        val userReview = userReviews.second
                        Reviews_Ratings(
                            userReview = userReview,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                        HorizontalDivider(
                            thickness = 2.dp,
                            color = Color.Black
                        )
                    }
                    when (val state = productReviews.loadState.append) {

                        is LoadState.Loading -> {
                            Circular_Loader()
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
}
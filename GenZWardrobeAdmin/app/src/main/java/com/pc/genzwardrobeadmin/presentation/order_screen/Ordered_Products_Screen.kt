package com.pc.genzwardrobeadmin.presentation.order_screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.pc.genzwardrobeadmin.presentation.MainViewModel

@Composable
fun Ordered_Products_Screen(
    mainViewModel: MainViewModel,
    onNextIconClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val orderedProductsState = mainViewModel.orderedProducts.collectAsLazyPagingItems()

    if (orderedProductsState.loadState.refresh is LoadState.Loading) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
        ) {
            items(orderedProductsState.itemSnapshotList.items) {
                User_Info(
                    userId = it.second,
                    userName = it.first,
                    orderIds = it.third,
                    onOrderIdClicked = { orderId ->
                        onNextIconClicked(it.second, orderId)
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            when (val state = orderedProductsState.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                }

                is LoadState.Error -> {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${state.error}")
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun User_Info(
    userId: String,
    userName: String,
    orderIds: List<String>,
    onOrderIdClicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isVisible = !isVisible },
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "$userName ( $userId )",
                style = MaterialTheme.typography.labelMedium,
                maxLines = 2,
                modifier = Modifier
                    .weight(0.75f)
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = if (isVisible) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = "Arrow",
                modifier = Modifier.size(32.dp)
            )
        }
        AnimatedVisibility(
            visible = isVisible,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                orderIds.forEach { orderId ->
                    HorizontalDivider(thickness = 2.dp, color = Color.Black)
                    Text(
                        text = "Order Id - #$orderId",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOrderIdClicked(orderId) }
                    )
                }
            }
        }
    }
}
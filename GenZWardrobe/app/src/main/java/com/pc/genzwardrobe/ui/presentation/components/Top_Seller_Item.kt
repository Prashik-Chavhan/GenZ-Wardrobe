package com.pc.genzwardrobe.ui.presentation.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobe.core.domain.GenderCategory
import com.pc.genzwardrobe.ui.presentation.home_screen.ProductVariantUiState

@Composable
fun Top_Sellers_List(
    allUiState: ProductVariantUiState,
    genderUiState: ProductVariantUiState,
    genderList: List<GenderCategory>,
    onCategoryClicked: (GenderCategory) -> Unit,
    modifier: Modifier = Modifier
) {

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    Column(
        modifier = modifier.padding(top = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "Top Seller",
            style = MaterialTheme.typography.headlineLarge
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth(),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ) {
            genderList.forEachIndexed { index, genderCategory ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = {
                        selectedTabIndex = index
                        onCategoryClicked(genderCategory)
                    },
                    modifier = Modifier
                        .padding(8.dp)
                ) {
                    Text(
                        text = genderCategory.text,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }
        when (selectedTabIndex) {
            0 -> {
                Top_Seller_Row(
                    uiState = allUiState
                )
            }

            else -> {
                Top_Seller_Row(
                    uiState = genderUiState
                )
            }
        }
    }
}

@Composable
fun Top_Seller_Row(
    uiState: ProductVariantUiState,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is ProductVariantUiState.Loading -> {
            Circular_Loader(modifier)
        }

        is ProductVariantUiState.Success -> {
            if (uiState.productVariant.isNotEmpty()) {
                LazyRow(
                    modifier = modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.productVariant) {
                        Product_Item(
                            it,
                            it.color,
                            onClick = {},
                            modifier = Modifier.width(180.dp)
                        )
                    }
                }
            } else {
                Text(
                    "Oops, Top Seller Products Are Not Available"
                )
            }
        }

        is ProductVariantUiState.Error -> {
            Log.d("TopSeller", "Error: ${uiState.message}")
        }
    }
}
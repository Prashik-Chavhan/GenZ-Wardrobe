package com.pc.genzwardrobe.ui.presentation.gender_product_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar
import com.pc.genzwardrobe.ui.presentation.components.Filter_Content
import com.pc.genzwardrobe.ui.presentation.components.Product_Grid
import com.pc.genzwardrobe.ui.presentation.components.Radio_Button_Item
import com.pc.genzwardrobe.ui.presentation.home_screen.HomeScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gender_Product_Screen(
    homeScreenViewModel: HomeScreenViewModel,
    gender: String?,
    category: String?,
    type: String?,
    onArrowBackIconClicked: () -> Unit,
    onVariantClicked: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val genderProducts =
        homeScreenViewModel.pagingProductVariantsByGender.collectAsLazyPagingItems()
    val sortingOptions = listOf("Default", "Price High to Low", "Price Low to High", "Popularity")
    var selectedSortOption by rememberSaveable { mutableStateOf("Default") }

    var selectedMinPrice by remember { mutableIntStateOf(0) }
    var selectedMaxPrice by remember { mutableIntStateOf(10000) }

    val selectedDiscountItems = remember { mutableStateOf(setOf<String>()) }
    val selectedRatingItems = remember { mutableStateOf(setOf<String>()) }
    val selectedFabricItems = remember { mutableStateOf(setOf<String>()) }
    val selectedOccasionItems = remember { mutableStateOf(setOf<String>()) }
    val selectedColorItems = remember { mutableStateOf(setOf<String>()) }

    var tempMinPrice by rememberSaveable { mutableIntStateOf(0) }
    var tempMaxPrice by rememberSaveable { mutableIntStateOf(10000) }
    val tempSelectedDiscountItems = remember { mutableStateOf(setOf<String>()) }
    val tempSelectedRatingItems = remember { mutableStateOf(setOf<String>()) }
    val tempSelectedFabricItems = remember { mutableStateOf(setOf<String>()) }
    val tempSelectedOccasionItems = remember { mutableStateOf(setOf<String>()) }
    val tempSelectedColorItems = remember { mutableStateOf(setOf<String>()) }

    val filterSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    var selectedPrice by remember { mutableStateOf(0f..10000f) }

    LaunchedEffect(
        key1 = listOf(gender, category, type, selectedSortOption),
        key2 = listOf(
            selectedMinPrice, selectedMaxPrice,
            selectedDiscountItems.value, selectedOccasionItems.value
        ),
        key3 = listOf(
            selectedRatingItems.value, selectedFabricItems.value,
            selectedColorItems.value
        )
    ) {
        homeScreenViewModel.fetchPagingProductVariants(
            gender!!,
            category!!,
            type!!,
            selectedSortOption,
            maxPrice = selectedMaxPrice,
            minPrice = selectedMinPrice,
            selectedDiscount = selectedDiscountItems.value.map {
                it.filter { char -> char.isDigit() }
            },
            selectedFabric = selectedFabricItems.value,
            selectedOccasion = selectedOccasionItems.value,
            selectedColor = selectedColorItems.value
        )
    }

    if (sortSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch { sortSheetState.hide() } }
        ) {
            sortingOptions.forEach { text ->
                val isSelected = selectedSortOption.contains(text)
                Radio_Button_Item(
                    selected = isSelected,
                    onClicked = {
                        selectedSortOption = text
                        coroutineScope.launch { sortSheetState.hide() }
                    },
                    text = text,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }

    if (filterSheetState.isVisible) {
        ModalBottomSheet(
            onDismissRequest = { coroutineScope.launch { filterSheetState.hide() } },
            modifier = Modifier,
            containerColor = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(0.7f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(
                        space = 8.dp,
                        alignment = Alignment.End
                    )
                ) {
                    TextButton(
                        onClick = {
                            selectedMinPrice = 0
                            selectedMaxPrice = 10000
                            selectedDiscountItems.value = emptySet()
                            selectedRatingItems.value = emptySet()
                            selectedFabricItems.value = emptySet()
                            selectedOccasionItems.value = emptySet()
                            selectedColorItems.value = emptySet()

                            tempMinPrice = 0
                            tempMaxPrice = 10000
                            tempSelectedDiscountItems.value = emptySet()
                            tempSelectedRatingItems.value = emptySet()
                            tempSelectedFabricItems.value = emptySet()
                            tempSelectedOccasionItems.value = emptySet()
                            tempSelectedColorItems.value = emptySet()

                            coroutineScope.launch { filterSheetState.hide() }
                        }
                    ) {
                        Text(
                            text = "Clear Filter",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    TextButton(
                        onClick = {
                            selectedMinPrice = tempMinPrice
                            selectedMaxPrice = tempMaxPrice
                            selectedDiscountItems.value = tempSelectedDiscountItems.value
                            selectedRatingItems.value = tempSelectedRatingItems.value
                            selectedFabricItems.value = tempSelectedFabricItems.value
                            selectedOccasionItems.value = tempSelectedOccasionItems.value
                            selectedColorItems.value = tempSelectedColorItems.value

                            coroutineScope.launch { filterSheetState.hide() }
                        }
                    ) {
                        Text(
                            text = "Apply Filter",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
                Filter_Content(
                    initialPriceRange = selectedPrice,
                    onPriceRangeChange = {
                        selectedPrice = it
                        tempMinPrice = it.start.toInt()
                        tempMaxPrice = it.endInclusive.toInt()
                    },
                    tempSelectedDiscountItems = tempSelectedDiscountItems,
                    tempSelectedRatingItems = tempSelectedRatingItems,
                    tempSelectedFabricItems = tempSelectedFabricItems,
                    tempSelectedOccasionItems = tempSelectedOccasionItems,
                    tempSelectedColorItems = tempSelectedColorItems,
                    onDiscountSelect = {
                        tempSelectedDiscountItems.value = it
                    },
                    onRatingSelect = {
                        tempSelectedFabricItems.value = it
                    },
                    onFabricSelect = {
                        tempSelectedRatingItems.value = it
                    },
                    onOccasionSelect = {
                        tempSelectedOccasionItems.value = it
                    },
                    onColorSelect = {
                        tempSelectedColorItems.value = it
                    }
                )
            }
        }
    }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "$gender $type",
                onIconClicked = {
                    onArrowBackIconClicked()
                },
                scrollBehavior
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .background(color = MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon_Text_Button(
                    onButtonClicked = { coroutineScope.launch { filterSheetState.show() } },
                    icon = Icons.Default.FilterList,
                    text = "Filter"
                )
                Icon_Text_Button(
                    onButtonClicked = { coroutineScope.launch { sortSheetState.show() } },
                    icon = Icons.AutoMirrored.Filled.Sort,
                    text = "Sort"
                )
            }

            if (genderProducts.loadState.hasError) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error")
                }
            } else {
                Product_Grid(
                    genderProductType = genderProducts,
                    onVariantClicked = { productId, color ->
                        onVariantClicked(productId, color)
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        }
    }
}

@Composable
fun Icon_Text_Button(
    onButtonClicked: () -> Unit,
    icon: ImageVector,
    text: String,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = { onButtonClicked() },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.background,
            contentColor = MaterialTheme.colorScheme.onBackground
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 1.dp
        ),
        border = BorderStroke(
            2.dp,
            Color.Gray
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        modifier = modifier
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
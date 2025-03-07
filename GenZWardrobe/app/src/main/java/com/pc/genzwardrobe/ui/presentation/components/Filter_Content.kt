package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import okhttp3.internal.immutableListOf

@Composable
fun Filter_Content(
    initialPriceRange: ClosedFloatingPointRange<Float>,
    tempSelectedDiscountItems: MutableState<Set<String>>,
    tempSelectedRatingItems: MutableState<Set<String>>,
    tempSelectedFabricItems: MutableState<Set<String>>,
    tempSelectedOccasionItems: MutableState<Set<String>>,
    tempSelectedColorItems: MutableState<Set<String>>,

    onPriceRangeChange: (ClosedFloatingPointRange<Float>) -> Unit,
    onDiscountSelect: (Set<String>) -> Unit,
    onRatingSelect: (Set<String>) -> Unit,
    onFabricSelect: (Set<String>) -> Unit,
    onOccasionSelect: (Set<String>) -> Unit,
    onColorSelect: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedItemIndex by remember { mutableIntStateOf(0) }

    var sliderPosition by remember { mutableStateOf(initialPriceRange) }

    val discountsList =
        listOf("30% and more", "40% and more", "50% and more", "60% and more", "70% and more")
    val ratingsList = listOf("4+", "3+", "2+", "1+")
    val fabricList = listOf(
        "Cotton", "Denim", "Linen", "Polyster",
        "Wool", "Silk", "Rayon", "Elastane", "Nylon",
        "Velvet", "Leather", "Cashmere"
    )

    val occasion = listOf(
        "Casual", "Formal", "Party",
        "Sportswear", "Ethnic/Festive", "Business/Office Wear",
        "Travel/Resort Wear", "Loungewear"
    )

    val colors = listOf(
        "Black", "White", "Blue", "Red",
        "Green", "Yellow", "Pink", "Grey",
        "Brown", "Purple", "Orange", "Beige",
        "Maroon", "Teal"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Filter_Options(
            selectedItem = selectedItemIndex,
            onItemClicked = { newIndex ->
                selectedItemIndex = newIndex
            },
            modifier = Modifier
                .fillMaxWidth(0.3f)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            when (selectedItemIndex) {
                0 -> {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(sliderPosition.start.toInt().toString())
                            Text(sliderPosition.endInclusive.toInt().toString())
                        }
                        RangeSlider(
                            value = sliderPosition,
                            onValueChange = { newPrice ->
                                sliderPosition = newPrice
                            },
                            valueRange = 0f..10000f,
                            steps = 999,
                            onValueChangeFinished = {
                                onPriceRangeChange(sliderPosition)
                            }
                        )
                    }
                }

                1 -> {
                    CheckBox_List(
                        itemList = discountsList,
                        selectedItems = tempSelectedDiscountItems,
                        onItemClicked = { setOf ->
                            tempSelectedDiscountItems.value = setOf
                            onDiscountSelect(tempSelectedDiscountItems.value)
                        }
                    )
                }

                2 -> {
                    CheckBox_List(
                        itemList = ratingsList,
                        selectedItems = tempSelectedRatingItems,
                        onItemClicked = {
                            tempSelectedRatingItems.value = it
                            onRatingSelect(tempSelectedRatingItems.value)
                        }
                    )
                }

                3 -> {
                    CheckBox_List(
                        itemList = fabricList,
                        selectedItems = tempSelectedFabricItems,
                        onItemClicked = {
                            tempSelectedFabricItems.value = it
                            onFabricSelect(tempSelectedFabricItems.value)
                        }
                    )
                }

                4 -> {
                    CheckBox_List(
                        itemList = occasion,
                        selectedItems = tempSelectedOccasionItems,
                        onItemClicked = {
                            tempSelectedOccasionItems.value = it
                            onOccasionSelect(tempSelectedOccasionItems.value)
                        }
                    )
                }

                5 -> {
                    CheckBox_List(
                        itemList = colors,
                        selectedItems = tempSelectedColorItems,
                        onItemClicked = {
                            tempSelectedColorItems.value = it
                            onColorSelect(tempSelectedColorItems.value)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CheckBox_List(
    itemList: List<String>,
    selectedItems: MutableState<Set<String>>,
    onItemClicked: (Set<String>) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        itemList.forEach { text ->
            val isChecked = selectedItems.value.contains(text)
            val updateSelection = selectedItems.value.toMutableSet()
            CheckBox_Item(
                isChecked = isChecked,
                onCheckedClicked = {
                    if (selectedItems.value.contains(text)) {
                        updateSelection.remove(text)
                    } else {
                        updateSelection.add(text)
                    }
                    selectedItems.value = updateSelection
                    onItemClicked(updateSelection)
                },
                text = text,
                modifier = modifier
            )
        }
    }

}

@Composable
fun CheckBox_Item(
    isChecked: Boolean,
    onCheckedClicked: () -> Unit,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onCheckedClicked() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isChecked,
            onCheckedChange = { onCheckedClicked() }
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun Filter_Options(
    selectedItem: Int,
    onItemClicked: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val filterList = immutableListOf(
        "Price", "Discount", "Customer Ratings", "Fabric", "Occasion", "Colors"
    )

    LazyColumn(
        modifier = modifier
    ) {
        itemsIndexed(filterList) { index, item ->
            Text(
                text = item,
                color = if (selectedItem == index) Color.Blue else Color.Black,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClicked(index) }
                    .background(if (selectedItem == index) Color.White else Color.LightGray)
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

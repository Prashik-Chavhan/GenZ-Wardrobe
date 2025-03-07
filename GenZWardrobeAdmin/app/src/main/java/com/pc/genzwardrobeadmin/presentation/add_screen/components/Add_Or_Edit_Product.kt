package com.pc.genzwardrobeadmin.presentation.add_screen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobeadmin.core.domain.GenderCategory
import com.pc.genzwardrobeadmin.core.domain.ProductCategory

@Composable
fun Add_Or_Edit_Product(
    initialProductBrand: String,
    onProductBrandChanged: (String) -> Unit,
    initialDescriptionValue: String,
    onDescriptionChange: (String) -> Unit,
    onSelectedCategoryChange: (String) -> Unit,
    selectedCategory: String,
    selectedType: String,
    selectedGender: String,
    onSelectedGenderChange: (String) -> Unit,
    onSelectedTypeChange: (String) -> Unit,
    initialFabricValue: String,
    onFabricChanged: (String) -> Unit,
    initialSleeveType: String,
    onSleeveTypeChanged: (String) -> Unit,
    initialCollarValue: String,
    onCollarValueChanged: (String) -> Unit,
    initialPatternValue: String,
    onPatternValueChanged: (String) -> Unit,
    initialFadedValue: String,
    onFadedValueChanged: (String) -> Unit,
    initialRiseValue: String,
    onRiseValueChanged: (String) -> Unit,
    initialDistressedValue: String,
    onDistressedValueChanged: (String) -> Unit,
    initialOccasionValue: String,
    onOccasionChanged: (String) -> Unit,
    initialCloserValue: String,
    onCloserValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var typeExpanded by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var genderExpanded by remember { mutableStateOf(false) }

    val clothProductGender = listOf(
        GenderCategory(
            name = "Men's",
            categories = listOf(
                ProductCategory(
                    name = "Topwear",
                    type = listOf(
                        "Oversized T-shirts", "Casual Shirts", "Polos", "Solid T-shirts",
                        "Classic Fit T-shirts", "Oversized Full Sleeve", "Dropcut T-shirts",
                        "Co-ord sets", "Jackets", "Hoodies & Sweatshirts", "Full Sleeve T-shirts"
                    )
                ),
                ProductCategory(
                    name = "Bottomwear",
                    type = listOf(
                        "Pants", "Jeans", "Joggers",
                        "Shorts", "Boxers & Innerwear", "Pajamas"
                    )
                )
            )
        ),
        GenderCategory(
            "Women's",
            listOf(
                ProductCategory(
                    "Topwear",
                    listOf(
                        "Oversized T-shirts",
                        "Shirts",
                        "Tops",
                        "Relaxed Fit T-Shirts",
                        "Dresses & Jumpsuits",
                        "Co-ord Sets",
                        "Hoodies & Sweatshirts",
                        "Sweaters",
                        "Jackets"
                    )
                ),
                ProductCategory(
                    "Bottomwear",
                    listOf("Joggers", "Pants", "Jeans", "Shorts & Skirts", "Cargos"),
                )
            )
        )
    )

    val productGender = clothProductGender.map { it.name }
    val productCategory = clothProductGender.find {
        it.name == selectedGender
    }?.categories?.map { it.name } ?: emptyList()

    val productType = clothProductGender.find {
        it.name == selectedGender
    }?.categories?.find {
        it.name == selectedCategory
    }?.type ?: emptyList()

    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            CustomDropdownMenu(
                label = "Gender",
                selectedValue = selectedGender,
                expanded = genderExpanded,
                onExpandedChange = { genderExpanded = !genderExpanded },
                items = productGender,
                onItemSelected = onSelectedGenderChange,
                modifier = Modifier.fillMaxWidth(0.5f)
            )
            Spacer(Modifier.width(12.dp))
            CustomDropdownMenu(
                label = "Category",
                selectedValue = selectedCategory,
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                items = productCategory,
                onItemSelected = onSelectedCategoryChange
            )
        }
        if (productType.isNotEmpty()) {
            CustomDropdownMenu(
                "Product Type",
                selectedValue = selectedType,
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded },
                items = productType,
                onItemSelected = onSelectedTypeChange,
                modifier = Modifier.fillMaxWidth()
            )
        }
        CustomTextField(
            value = initialDescriptionValue,
            onValueChange = onDescriptionChange,
            label = "Add a description",
            singleLine = false,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )

        CustomTextField(
            value = initialProductBrand,
            onValueChange = onProductBrandChanged,
            label = "Product Brand",
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            when (selectedGender) {
                "Men's" -> {
                    when (selectedCategory) {
                        "Topwear" -> {
                            when (selectedType) {
                                "Oversized T-shirts", "Casual Shirts",
                                "Polos", "Solid T-shirts",
                                "Dropcut T-shirts", "Classic Fit T-shirts" -> {
                                    Fabric_Occasion(
                                        initialFabricValue, onFabricChanged,
                                        initialOccasionValue, onOccasionChanged
                                    )
                                    Collar_Sleeve(
                                        initialCollarValue, onCollarValueChanged,
                                        initialSleeveType, onSleeveTypeChanged
                                    )
                                    Closer_Pattern(
                                        initialCloserValue, onCloserValueChanged,
                                        initialPatternValue, onPatternValueChanged
                                    )
                                }
                            }
                        }

                        "Bottomwear" -> {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                CustomTextField(
                                    initialFadedValue,
                                    onFadedValueChanged,
                                    "Faded",
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.weight(0.33f)
                                )
                                Spacer(Modifier.width(8.dp))
                                CustomTextField(
                                    initialRiseValue,
                                    onRiseValueChanged,
                                    "Rise",
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.weight(0.33f)

                                )
                                Spacer(Modifier.width(8.dp))
                                CustomTextField(
                                    initialDistressedValue,
                                    onDistressedValueChanged,
                                    "Distressed",
                                    keyboardOptions = KeyboardOptions(
                                        imeAction = ImeAction.Next
                                    ),
                                    modifier = Modifier.weight(0.33f)

                                )
                            }
                        }
                    }
                }

                "Women's" -> {
                    when (selectedCategory) {
                        "Topwear" -> {
                            when (selectedType) {
                                "Oversized T-shirts", "Shirts", "Tops",
                                "Relaxed Fit T-Shirts", "Co-ord Sets", "Hoodies & Sweatshirts",
                                "Sweaters", "Jackets" -> {
                                    Fabric_Occasion(
                                        initialFabricValue, onFabricChanged,
                                        initialOccasionValue, onOccasionChanged
                                    )
                                    Collar_Sleeve(
                                        initialCollarValue, onCollarValueChanged,
                                        initialSleeveType, onSleeveTypeChanged
                                    )
                                    Closer_Pattern(
                                        initialCloserValue, onCloserValueChanged,
                                        initialPatternValue, onPatternValueChanged
                                    )
                                }

                                "Dresses & Jumpsuits" -> {
                                    Collar_Sleeve(
                                        initialCollarValue,
                                        onCollarValueChanged,
                                        initialSleeveType,
                                        onSleeveTypeChanged
                                    )
                                    Fabric_Occasion(
                                        initialFabricValue,
                                        onFabricChanged,
                                        initialOccasionValue,
                                        onOccasionChanged
                                    )
                                }
                            }
                        }

                        "BottomWear" -> {
                            when (selectedType) {
                                "Joggers", "Pants", "Jeans",
                                "Shorts & Skirts", "Cargos" -> {
                                    Fabric_Occasion(
                                        initialFabricValue, onFabricChanged,
                                        initialOccasionValue, onOccasionChanged
                                    )
                                    Closer_Pattern(
                                        initialCloserValue, onCloserValueChanged,
                                        initialPatternValue, onPatternValueChanged
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Closer_Pattern(
    initialCloserValue: String,
    onCloserValueChanged: (String) -> Unit,
    initialPatternValue: String,
    onPatternValueChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        CustomTextField(
            initialCloserValue,
            onCloserValueChanged,
            label = "Closer Type",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(0.5f)
        )
        Spacer(Modifier.width(8.dp))
        CustomTextField(
            initialPatternValue,
            onPatternValueChanged,
            "Pattern",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun Fabric_Occasion(
    initialFabricValue: String,
    onFabricChanged: (String) -> Unit,
    initialOccasionValue: String,
    onOccasionValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier.fillMaxWidth()) {
        CustomTextField(
            initialFabricValue,
            onFabricChanged,
            "Fabric",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(0.5f)
        )
        Spacer(Modifier.width(8.dp))
        CustomTextField(
            initialOccasionValue,
            onOccasionValueChange,
            "Occasion",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun Collar_Sleeve(
    initialCollarValue: String,
    onCollarValueChanged: (String) -> Unit,
    initialSleeveType: String,
    onSleeveTypeChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        CustomTextField(
            initialCollarValue,
            onCollarValueChanged,
            label = "Collar Type",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(0.5f)
        )
        Spacer(Modifier.width(8.dp))
        CustomTextField(
            initialSleeveType,
            onSleeveTypeChanged,
            "Sleeve Type",
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next
            ),
            modifier = Modifier.weight(0.5f)
        )
    }
}
package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobe.utils.Utils

@Composable
fun Gender_Drawer_Content(
    onItemSelect: (String, String, String) -> Unit,
    modifier: Modifier = Modifier
) {

    var selectedGender by remember { mutableStateOf<String?>(null) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val genderList = Utils.GenderLists.genderLists().map { it.name }

    Column(
        modifier = modifier
    ) {
        genderList.forEach { genderName ->
            val isGenderExpanded = selectedGender == genderName
            Drawer_Item(
                title = genderName,
                isExpanded = isGenderExpanded,
                onExpandChange = {

                    selectedGender = if (isGenderExpanded) null else genderName

                    selectedCategory = null // Reset category when gender changes
                },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            AnimatedVisibility(visible = isGenderExpanded) {
                val categoryList =
                    Utils.GenderLists.genderLists().find { it.name == selectedGender }
                        ?.categories?.map { it.name } ?: emptyList()
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    categoryList.forEach { categoryName ->
                        val isCategoryExpanded = selectedCategory == categoryName
                        Drawer_Item(
                            title = categoryName,
                            isExpanded = isCategoryExpanded,
                            onExpandChange = {
                                selectedCategory = if (!isCategoryExpanded) categoryName else ""
                            },
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        AnimatedVisibility(visible = isCategoryExpanded) {
                            Column(
                                modifier = Modifier
                            ) {
                                val typeList = Utils.GenderLists.genderLists()
                                    .find { it.name == selectedGender }
                                    ?.categories?.find { it.name == selectedCategory }
                                    ?.type ?: emptyList()

                                typeList.forEach { type ->
                                    Text(
                                        text = type,
                                        style = MaterialTheme.typography.titleMedium,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable {
                                                onItemSelect(
                                                    selectedGender!!,
                                                    selectedCategory!!,
                                                    type
                                                )
                                            }
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
fun Drawer_Item(
    title: String,
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onExpandChange(!isExpanded) }
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.weight(1f),
            fontWeight = FontWeight.W600
        )
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = "",
            modifier = Modifier.size(34.dp)
        )
    }
}
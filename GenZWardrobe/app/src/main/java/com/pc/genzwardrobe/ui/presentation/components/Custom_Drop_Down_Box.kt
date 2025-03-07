package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Custom_Dropdown_Menu_Box(
    isExpanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    selectedOption: String,
    selectableOptions: List<String>,
    onSelectedOptionChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { onExpandChange(it) },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            modifier = Modifier
                .menuAnchor(type = MenuAnchorType.PrimaryEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = isExpanded,
            modifier = Modifier,
            containerColor = Color.White,
            onDismissRequest = { onExpandChange(false) },
        ) {
            selectableOptions.forEach { selectableOptions ->
                DropdownMenuItem(
                    text = { Text(selectableOptions) },
                    onClick = {
                        onSelectedOptionChanged(selectableOptions)
                        onExpandChange(false)
                    }
                )
            }
        }
    }
}
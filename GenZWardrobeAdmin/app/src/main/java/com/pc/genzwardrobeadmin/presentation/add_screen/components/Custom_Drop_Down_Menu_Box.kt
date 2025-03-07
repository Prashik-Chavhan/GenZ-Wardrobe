package com.pc.genzwardrobeadmin.presentation.add_screen.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdownMenu(
    label: String,
    selectedValue: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<String>,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { onExpandedChange(!expanded) }
    ) {
        OutlinedTextField(
            value = selectedValue,
            onValueChange = {},
            label = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium
                )
            },
            textStyle = TextStyle(
                fontSize = 17.sp
            ),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            readOnly = true,
            modifier = modifier.menuAnchor(type = MenuAnchorType.SecondaryEditable, enabled = true)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item, style = MaterialTheme.typography.titleMedium) },
                    onClick = {
                        onItemSelected(item)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}
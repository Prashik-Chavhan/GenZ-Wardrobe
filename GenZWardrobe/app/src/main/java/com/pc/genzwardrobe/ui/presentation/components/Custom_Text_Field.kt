package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun CustomTextField(
    initialValue: String,
    onInitialValueChanged: (String) -> Unit,
    keyboardType: KeyboardType,
    label: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = initialValue,
        onValueChange = { onInitialValueChanged(it) },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        textStyle = MaterialTheme.typography.titleMedium,
        singleLine = singleLine,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Blue,
            unfocusedIndicatorColor = Color.LightGray,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            unfocusedLabelColor = Color.Black,
            unfocusedTextColor = Color.Black,
            focusedTextColor = Color.Black
        ),
        modifier = modifier.fillMaxWidth()
    )
}
package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun HomePage_AppDetails(
    icon: ImageVector,
    text1: String,
    text2: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                modifier = Modifier.size(38.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text1,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text2,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
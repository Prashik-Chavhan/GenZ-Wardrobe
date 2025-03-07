package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobe.utils.Utils

@Composable
fun OrderStatus(
    currentStatus: Int,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp)
    ) {
        when (currentStatus) {
            in 0..6 -> {
                Utils.orderStatus.take(currentStatus + 1).forEachIndexed { index, orderStatus ->
                    if (index in 0..6) {
                        Status_Row(
                            status = orderStatus,
                            isActive = index <= currentStatus,
                            isCompleted = index < currentStatus
                        )
                    }
                }
            }

            in 8..10 -> {
                Utils.orderStatus.take(currentStatus + 1).forEachIndexed { index, orderStatus ->
                    if (index in 8..10) {
                        Status_Row(
                            status = orderStatus,
                            isActive = index <= currentStatus,
                            isCompleted = index < currentStatus
                        )
                    }
                }
            }

            11 -> {
                Text(Utils.orderStatus.getOrNull(11) ?: "Unknown")
            }

            7 -> {
                Text(Utils.orderStatus.getOrNull(7) ?: "Unknown")
            }
        }
    }
}

@Composable
fun Status_Row(
    status: String,
    isCompleted: Boolean,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isActive) Icons.Default.Check else Icons.Default.Close,
                contentDescription = "",
                tint = if (isActive) Color.White else Color.Gray,
                modifier = Modifier
                    .size(35.dp)
                    .background(shape = CircleShape, color = Color.Green)
            )
            if (isCompleted) {
                Box(
                    modifier = modifier
                        .width(6.dp)
                        .height(70.dp)
                        .background(
                            color = if (isCompleted) Color.Green else Color.LightGray
                        )
                )
            }
        }
        Text(
            text = status,
            style = MaterialTheme.typography.titleMedium,
            color = if (isActive) MaterialTheme.colorScheme.onSecondaryContainer else Color.Gray
        )
    }
}
package com.pc.genzwardrobeadmin.presentation.add_screen.components

import android.util.Log
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun PermissionDialog(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onDismissClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = {
            onDismissClicked()
        },
        title = {
            Text(
                text = "Storage Permission Required"
            )
        },
        text = {
            Text(
                text = "This app requires storage permission to upload images"
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    Log.d("Permission", "PermissionState.status.shouldShowRationale Confirm Button")
                    onPermissionGranted()
                }
            ) {
                Text(
                    text = "Grant"
                )
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    Log.d("Permission", "PermissionState.status.shouldShowRationale Dismiss Button")
                    onPermissionDenied()
                }
            ) {
                Text(
                    text = "Cancel"
                )
            }
        }
    )
}
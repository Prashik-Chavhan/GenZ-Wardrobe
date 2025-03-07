package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    onMenuClicked: () -> Unit,
    onWishlistClicked: () -> Unit,
    onCartClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    TopAppBar(
        title = {
            Text(
                text = "GenZ",
                style = MaterialTheme.typography.headlineLarge
            )
        },
        navigationIcon = {
            IconButton(
                onClick = { onMenuClicked() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Menu,
                    contentDescription = "",
                )
            }
        },
        actions = {
            IconButton(
                onClick = { onWishlistClicked() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = "",
                    modifier = Modifier.size(30.dp)
                )
            }
            IconButton(
                onClick = { onCartClicked() }
            ) {
                Icon(
                    imageVector = Icons.Outlined.ShoppingCart,
                    contentDescription = "",
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        modifier = modifier
    )
}
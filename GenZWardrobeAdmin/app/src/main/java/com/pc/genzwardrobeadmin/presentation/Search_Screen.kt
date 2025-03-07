package com.pc.genzwardrobeadmin.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pc.genzwardrobeadmin.presentation.home_screen.Product_Item

@Composable
fun Search_Screen(
    onNavBackClicked: () -> Unit,
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {

    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchItems = viewModel.searchVariants.collectAsState()
    var isSearchBarFocused by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(end = 8.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBack_Button(
                onNavBackClicked = {
                    onNavBackClicked()
                    viewModel.clearSearchQuery()
                }
            )
            Spacer(Modifier.width(4.dp))
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    viewModel.updateSearchQuery(it)
                },
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedTextColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedPlaceholderColor = Color.Black,
                    unfocusedTrailingIconColor = Color.Black,
                    focusedTrailingIconColor = Color.Black
                ),
                placeholder = {
                    Text("Search...")
                },
                singleLine = true,
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        isSearchBarFocused = it.isFocused
                    }
            )
        }

        if (searchQuery.isEmpty() && isSearchBarFocused ||
            searchQuery.isEmpty() && !isSearchBarFocused
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No Products")
            }
        } else if (searchQuery.isNotEmpty()) {
            if (searchItems.value.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = modifier.fillMaxSize()
                ) {
                    items(searchItems.value) { (productId, productVariants) ->
                        Product_Item(
                            product = productVariants,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NavBack_Button(
    onNavBackClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = { onNavBackClicked() }
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Default.ArrowBack,
            contentDescription = "Arrow back",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = modifier
        )
    }
}
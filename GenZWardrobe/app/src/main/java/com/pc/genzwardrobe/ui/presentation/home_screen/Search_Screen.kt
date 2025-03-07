package com.pc.genzwardrobe.ui.presentation.home_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.pc.genzwardrobe.ui.presentation.components.Product_Item

@Composable
fun Search_Screen(
    onNavBackClicked: () -> Unit,
    onSearchedItemClicked: (String, String) -> Unit,
    homeScreenViewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier
) {

    val searchQuery by homeScreenViewModel.searchQuery.collectAsState()
    val searchedItems = homeScreenViewModel.searchedVariants.collectAsState()

    var isSearchBarFocused by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = MaterialTheme.colorScheme.primary),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onNavBackClicked()
                        homeScreenViewModel.clearSearchQuery()
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = "Search",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        homeScreenViewModel.updateSearchQuery(it)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged {
                            isSearchBarFocused = it.isFocused
                        }
                        .padding(top = 16.dp, end = 16.dp, bottom = 16.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        unfocusedPlaceholderColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        unfocusedBorderColor = Color.Black,
                        focusedBorderColor = Color.Black
                    ),
                    placeholder = {
                        Text(
                            text = "Search..."
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    textStyle = MaterialTheme.typography.titleMedium
                )
            }
        }
    ) {
        if (searchQuery.isEmpty() && isSearchBarFocused ||
            searchQuery.isEmpty() && !isSearchBarFocused
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Empty")
            }
        } else if (searchQuery.isNotEmpty()) {
            if (searchedItems.value.isNotEmpty()) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = modifier
                        .fillMaxSize()
                        .padding(it)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    items(searchedItems.value) { (productId, productVariant) ->
                        Product_Item(
                            product = productVariant,
                            text = productVariant.color,
                            onClick = {
                                onSearchedItemClicked(productId, productVariant.color)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
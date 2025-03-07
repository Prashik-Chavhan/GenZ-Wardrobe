package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.request.ImageRequest

@Composable
fun Product_Image(
    image: String,
    contentScale: ContentScale = ContentScale.FillBounds,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(image)
            .crossfade(true)
            .build(),
        contentDescription = "Product Image",
        modifier = modifier.fillMaxSize(),
        contentScale = contentScale
    )
}
package com.pc.genzwardrobe.ui.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun Home_Page_Card(
    image1: Int,
    image2: Int,
    buttonText1: String,
    buttonText2: String,
    onCard1Clicked: (String) -> Unit,
    onCard2Clicked: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
    ) {
        Home_Card_Item(
            buttonText = buttonText1,
            image = image1,
            onCardClicked = { onCard1Clicked(buttonText1) },
            modifier = Modifier.weight(0.5f)
        )
        Home_Card_Item(
            buttonText = buttonText2,
            image = image2,
            onCardClicked = { onCard2Clicked(buttonText2) },
            modifier = Modifier.weight(0.5f)
        )
    }
}

@Composable
fun Home_Card_Item(
    image: Int,
    buttonText: String,
    onCardClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onCardClicked() },
        shape = RectangleShape
    ) {
        Box(
            modifier = Modifier,
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(image),
                contentDescription = buttonText,
                contentScale = ContentScale.Crop,
                modifier = Modifier.height(350.dp)
            )
            Text(
                text = buttonText,
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
    }
}
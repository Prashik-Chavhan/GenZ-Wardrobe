package com.pc.genzwardrobe.ui.presentation.order_placing_screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.pc.genzwardrobe.R
import com.pc.genzwardrobe.ui.presentation.components.CustomTopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Order_Success(
    userName: String?,
    orderId: String?,
    onNavBackClicked: () -> Unit,
    onContinueShoppingClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            CustomTopAppBar(
                text = "Payment Successful",
                onIconClicked = {
                    onNavBackClicked()
                },
                scrollBehavior
            )
        }
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(it)
        ) {

            Success_Animation(
                userName,
                orderId,
                onContinueShoppingClicked = {
                    onContinueShoppingClicked()
                }
            )

        }
    }
}

@Composable
fun Success_Animation(
    userName: String?,
    orderId: String?,
    onContinueShoppingClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Payment Successful !",
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = "Hey $userName, thank you for shopping at GenZ Wardrobe ! " +
                    "Your order has confirmed and its number is $orderId.",
            fontSize = 21.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        LottieAnimationView(
            resId = R.raw.lottie_success_animation,
            modifier = Modifier
                .size(200.dp)
                .padding(16.dp)
        )
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = { onContinueShoppingClicked() },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            modifier = Modifier
                .height(58.dp)
        ) {
            Text(
                text = "Continue Shopping",
                fontSize = 17.sp
            )
        }
    }
}

@Composable
fun LottieAnimationView(
    resId: Int,
    modifier: Modifier = Modifier
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(resId))

    LottieAnimation(
        composition = composition,
        iterations = 1,
        modifier = modifier
    )
}

@Preview
@Composable
private fun Preview() {
    Order_Success(
        "",
        "",
        {},
        {}
    )
}
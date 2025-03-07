package com.pc.genzwardrobe.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pc.genzwardrobe.R

val Poppins = FontFamily(
    Font(R.font.poppins_bold, FontWeight.Bold),
    Font(R.font.poppins_regular),
    Font(R.font.poppins_semi_bold, FontWeight.SemiBold)
)
// Set of Material typography styles to start with
val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = Poppins,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 16.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Poppins,
        fontSize = 18.sp,
        fontWeight = FontWeight.W500
    ),
    labelSmall = TextStyle(
        fontFamily = Poppins,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
)
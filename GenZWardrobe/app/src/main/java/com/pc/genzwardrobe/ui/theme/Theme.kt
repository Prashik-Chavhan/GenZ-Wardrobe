package com.pc.genzwardrobe.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val lightScheme = lightColorScheme(
    primary = FlipkartBlue,
    secondary = FlipkartYellow,
    tertiary = OfferOrange,
    background = White,
    surface = LightGray,
    onPrimary = White,
    onSecondary = Black,
    onTertiary = Black,
    onBackground = Black,
    onSurface = DarkGray,
    error = OfferRed,
    onError = White
)

private val darkScheme = darkColorScheme(
    primary = FlipkartBlue,
    secondary = FlipkartYellow,
    tertiary = OfferOrange,
    background = DarkBackground,
    surface = DarkCard,
    onPrimary = White,
    onSecondary = DarkText,
    onTertiary = Black,
    onBackground = DarkText,
    onSurface = LightGray,
    error = OfferRed,
    onError = Black
)


@Composable
fun GenZWardrobeTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkScheme
        else -> lightScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.solinftec.apontamentosmondaydash.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

private val Mint = Color(0xFFC1ffef)
private val Tiffany = Color(0xFF9EDCCC)
private val LightYellow = Color(0xFFF7E386)
private val Gold = Color(0xFFEAC22E)
private val Red = Color(0xFFDB262D)
private val DarkRed = Color(0xFF911C21)
private val Lime = Color(0xFFAEC553)
private val DarkGreen = Color(0xFF79883C)

val AppLightColorScheme = lightColorScheme(
    primary = Lime,
    onPrimary = Color.Black,
    primaryContainer = DarkGreen,
    onPrimaryContainer = Color.White,
    secondary = Tiffany,
    onSecondary = Color.Black,
    secondaryContainer = Mint,
    onSecondaryContainer = Color.Black,
    tertiary = Gold,
    onTertiary = Color.Black,
    tertiaryContainer = LightYellow,
    onTertiaryContainer = Color.Black,
    error = Red,
    onError = Color.White,
    errorContainer = DarkRed,
    onErrorContainer = Color.White,
    background = Color.White,
    onBackground = Color.Black,
    surface = Color.White,
    onSurface = Color.Black,
    surfaceVariant = Mint,
    onSurfaceVariant = Color.Black,
    outline = Color.Gray,
    inverseOnSurface = Color.White,
    inverseSurface = Color.Black,
    // You might need to define more colors based on your specific needs for MaterialTheme
)

val AppDarkColorScheme = darkColorScheme(
    primary = Lime,
    onPrimary = Color.Black,
    primaryContainer = DarkGreen,
    onPrimaryContainer = Color.White,
    secondary = Tiffany,
    onSecondary = Color.Black,
    secondaryContainer = Mint,
    onSecondaryContainer = Color.Black,
    tertiary = Gold,
    onTertiary = Color.Black,
    tertiaryContainer = LightYellow,
    onTertiaryContainer = Color.Black,
    error = Red,
    onError = Color.White,
    errorContainer = DarkRed,
    onErrorContainer = Color.White,
    background = Color.Black,
    onBackground = Color.White,
    surface = Color.Black,
    onSurface = Color.White,
    surfaceVariant = Mint,
    onSurfaceVariant = Color.Black,
    outline = Color.Gray,
    inverseOnSurface = Color.Black,
    inverseSurface = Color.White,
    // You might need to define more colors based on your specific needs for MaterialTheme
)
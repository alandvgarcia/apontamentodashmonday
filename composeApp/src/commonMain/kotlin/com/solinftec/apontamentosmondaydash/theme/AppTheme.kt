package com.solinftec.apontamentosmondaydash.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    darkTheme: Boolean = true, // You would typically get this from system settings or a user preference
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        typography = MaterialTheme.typography, // Use default or define your own
        content = content
    )
}

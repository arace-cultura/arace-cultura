package com.aracecultura.arace.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val araceColorScheme = lightColorScheme(
    primary = purple40,
    secondary = purpleGrey40,
    tertiary = pink40,
    background = bgDefault,
    outline = btColor,
)

val AraceTypography = run {
    val base = Typography()
    Typography(
        displayLarge   = base.displayLarge.copy(fontFamily = GoogleSans),
        displayMedium  = base.displayMedium.copy(fontFamily = GoogleSans),
        displaySmall   = base.displaySmall.copy(fontFamily = GoogleSans),
        headlineLarge  = base.headlineLarge.copy(fontFamily = GoogleSans),
        headlineMedium = base.headlineMedium.copy(fontFamily = GoogleSans),
        headlineSmall  = base.headlineSmall.copy(fontFamily = GoogleSans),
        titleLarge     = base.titleLarge.copy(fontFamily = GoogleSans),
        titleMedium    = base.titleMedium.copy(fontFamily = GoogleSans),
        titleSmall     = base.titleSmall.copy(fontFamily = GoogleSans),
        bodyLarge      = base.bodyLarge.copy(fontFamily = GoogleSans),
        bodyMedium     = base.bodyMedium.copy(fontFamily = GoogleSans),
        bodySmall      = base.bodySmall.copy(fontFamily = GoogleSans),
        labelLarge     = base.labelLarge.copy(fontFamily = GoogleSans),
        labelMedium    = base.labelMedium.copy(fontFamily = GoogleSans),
        labelSmall     = base.labelSmall.copy(fontFamily = GoogleSans),
    )
}

@Composable
fun AraceTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = araceColorScheme,
        typography = AraceTypography,
        content = content
    )
}

package com.dambrofarne.eyeflush.ui.theme

import androidx.compose.ui.graphics.Color

fun Color.darken(factor: Float = .2f): Color {
    return Color(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f),
        alpha = alpha
    )
}

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Flame = Color(0xFFE4572E)
val Error = Color(0xFFFF0033)
val Link = Color(0xFF4286F4)

// Light mode colors
val White = Color(0xFFFFFFFF)
val Platinum = Color(0xFFDADADA)
val EerieBlack = Color(0xFF262626)
package com.dambrofarne.eyeflush.ui.theme

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = Flame, // Primary Orange
    onPrimary = White, // Color on Primary Orange

    background = OnBlackBlack, // Background color for screens
    onBackground = White,

    primaryContainer = MainBlack, // CustomScaffold Color
    onPrimaryContainer = White,
    secondaryContainer = EerieBlack, // On Background container color
    onSecondaryContainer = White,

    surfaceVariant = Platinum, // Scaffold gradient


    onTertiaryContainer = Platinum, // Useful details

    secondary = Link,
    tertiary = Danger
)

private val LightColorScheme = lightColorScheme(
    primary = Flame, // App color
    onPrimary = White, // Text and icons on app color

    background = LightBackground, // Background color for screens
    onBackground = EerieBlack, // Text on background color

    primaryContainer = White, // CustomScaffold Color
    onPrimaryContainer = EerieBlack, // Text on container

    secondaryContainer = Platinum, // On Background container color
    onSecondaryContainer = EerieBlack,

    surfaceVariant = Platinum, // Scaffold gradient

    onTertiaryContainer = Platinum,

    secondary = Link, // Darker app color
    tertiary = Danger // Error color


    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromString(value: String?): ThemePreference {
            return try {
                if (value == null) SYSTEM
                else valueOf(value)
            } catch (e: Exception) {
                SYSTEM
            }
        }
    }
}

@Composable
fun EyeFlushTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {


    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
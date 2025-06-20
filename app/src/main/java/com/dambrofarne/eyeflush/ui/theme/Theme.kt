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
    primary = Flame,
    onPrimary = White,

    background = OnBlackBlack,
    onBackground = White,

    primaryContainer = MainBlack,
    onPrimaryContainer = White,
    secondaryContainer = OnBlackBlack,
    onSecondaryContainer = White,

    onTertiaryContainer = Platinum,

    secondary = Link,
    tertiary = Danger
)

private val LightColorScheme = lightColorScheme(
    primary = Flame, // App color
    onPrimary = White, // Text and icons on app color

    background = LightOrange, // Main background color
    onBackground = EerieBlack, // Text on background color

    primaryContainer = White, // Container on background color
    onPrimaryContainer = EerieBlack, // Text on container
    secondaryContainer = Platinum,
    onSecondaryContainer = EerieBlack,

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
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
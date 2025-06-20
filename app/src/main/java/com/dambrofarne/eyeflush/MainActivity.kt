package com.dambrofarne.eyeflush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.navigation.compose.rememberNavController
import com.dambrofarne.eyeflush.ui.EyeFlushNavGraph
import com.dambrofarne.eyeflush.ui.theme.EyeFlushTheme
import com.dambrofarne.eyeflush.ui.theme.ThemePreference
import com.dambrofarne.eyeflush.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemTheme = isSystemInDarkTheme()
            val themeViewModel: ThemeViewModel = koinViewModel()

            val themePref by themeViewModel.themePreference.collectAsState()

            val isDark = when (themePref) {
                ThemePreference.SYSTEM -> systemTheme
                ThemePreference.LIGHT -> false
                ThemePreference.DARK -> true
            }

            var previousEffectiveTheme by rememberSaveable { mutableStateOf<Boolean?>(null) }
            LaunchedEffect(isDark) {
                if (previousEffectiveTheme != null && previousEffectiveTheme != isDark) {
                    this@MainActivity.recreate()
                }
                previousEffectiveTheme = isDark
            }

            val navController = rememberNavController()
            EyeFlushTheme(darkTheme = isDark) {
                EyeFlushNavGraph(
                    navController = navController,
                    themeViewModel = themeViewModel
                )
            }
        }
    }
}

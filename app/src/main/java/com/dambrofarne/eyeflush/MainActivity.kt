package com.dambrofarne.eyeflush

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.dambrofarne.eyeflush.ui.EyeFlushNavGraph
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigViewModel
import com.dambrofarne.eyeflush.ui.theme.EyeFlushTheme
import com.dambrofarne.eyeflush.ui.theme.ThemeViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val systemTheme = isSystemInDarkTheme()
            val themeViewModel: ThemeViewModel = koinViewModel()

            LaunchedEffect(Unit) {
                themeViewModel.initTheme(systemTheme)
            }


            val isDark  by themeViewModel.isDarkTheme.collectAsState()

            LaunchedEffect(isDark) {
                Log.w("EyeFlushTheme", "Sono CAMBIATO !!!!")
            }


            EyeFlushTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                EyeFlushNavGraph(navController)
            }
        }
    }
}

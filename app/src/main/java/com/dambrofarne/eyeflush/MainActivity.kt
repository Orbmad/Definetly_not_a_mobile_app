package com.dambrofarne.eyeflush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.dambrofarne.eyeflush.ui.EyeFlushNavGraph
import com.dambrofarne.eyeflush.ui.theme.EyeFlushTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EyeFlushTheme {
                val navController = rememberNavController()
                EyeFlushNavGraph(navController)
            }
        }
    }
}

package com.example.eyeflush

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.eyeflush.ui.EyeFlushNavGraph
import com.example.eyeflush.ui.theme.EyeFlushTheme

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

package com.dambrofarne.eyeflush.ui.screens.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavHostController) {
    // Effetto lanciato una sola volta al primo composition
    LaunchedEffect(Unit) {
        delay(2000)

        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            navController.navigate(EyeFlushRoute.Home) {
                popUpTo(EyeFlushRoute.Splash) { inclusive = true }
            }
        } else {
            navController.navigate(EyeFlushRoute.SignIn) {
                popUpTo(EyeFlushRoute.Splash) { inclusive = true }
            }
        }
    }
    // Grafica della splash screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("EyeFlush", style = MaterialTheme.typography.headlineMedium)
    }
}
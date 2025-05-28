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
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(navController: NavHostController) {
    val viewModel: SplashViewModel = koinViewModel<SplashViewModel>()

    //Necessario perchè il viewModel non può navigare direttamente,
    //Però con collect mi faccio dire dove navigare.
    LaunchedEffect(Unit) {
        viewModel.navigation.collect { route ->
            navController.navigate(route) {
                popUpTo(EyeFlushRoute.Splash) { inclusive = true }
            }
        }
    }

    // UI della splash screen
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("EyeFlush", style = MaterialTheme.typography.headlineMedium)
    }
}
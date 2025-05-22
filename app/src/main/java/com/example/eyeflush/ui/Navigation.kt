package com.example.eyeflush.ui.theme

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import kotlinx.serialization.Serializable
import org.koin.androidx.compose.koinViewModel

sealed interface EyeFlushRoute {
    @Serializable data object SplashScreen : EyeFlushRoute
    @Serializable data object SignIn : EyeFlushRoute
    @Serializable data object SignUp : EyeFlushRoute
    @Serializable data object Home : EyeFlushRoute
}

@Composable
fun EyeFlushNavGraph(navController: NavHostController) {

}
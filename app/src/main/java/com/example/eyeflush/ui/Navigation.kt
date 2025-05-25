package com.example.eyeflush.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eyeflush.ui.screens.splashscreen.SplashScreen
import kotlinx.serialization.Serializable

sealed interface EyeFlushRoute {
    //Navigation with kotlin serialization
    @Serializable data object Splash : EyeFlushRoute
    @Serializable data object SignIn : EyeFlushRoute
    @Serializable data object SignUp : EyeFlushRoute
    @Serializable data object Home : EyeFlushRoute
}

@Composable
fun EyeFlushNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = EyeFlushRoute.Splash //Default destination
    ){
        composable<EyeFlushRoute.Splash> {
            SplashScreen(navController)
        }
    }

}
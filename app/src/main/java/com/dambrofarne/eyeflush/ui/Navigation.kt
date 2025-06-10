package com.dambrofarne.eyeflush.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.dambrofarne.eyeflush.ui.screens.camera.CameraScreen
import com.dambrofarne.eyeflush.ui.screens.home.HomeMapScreen
import com.dambrofarne.eyeflush.ui.screens.profile.ProfileScreen
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigScreen
import com.dambrofarne.eyeflush.ui.screens.signin.SignInScreen
import com.dambrofarne.eyeflush.ui.screens.signup.SignUpScreen
import com.dambrofarne.eyeflush.ui.screens.splash.SplashScreen
import kotlinx.serialization.Serializable

sealed interface EyeFlushRoute {

    //Navigation with kotlin serialization
    @Serializable data object Splash : EyeFlushRoute
    @Serializable data object SignIn : EyeFlushRoute
    @Serializable data object SignUp : EyeFlushRoute
    @Serializable data object Home : EyeFlushRoute
    @Serializable data object ProfileConfig : EyeFlushRoute
    @Serializable data object Profile : EyeFlushRoute
    @Serializable data object Camera: EyeFlushRoute
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
        composable<EyeFlushRoute.SignIn> {
            SignInScreen(navController)
        }

        composable<EyeFlushRoute.SignUp> {
            SignUpScreen(navController)
        }

        composable<EyeFlushRoute.Home> {
            //ProfileConfigScreen(navController)
            HomeMapScreen(navController)
        }

        composable<EyeFlushRoute.ProfileConfig> {
            ProfileConfigScreen(navController)
        }

        composable<EyeFlushRoute.Profile> {
            ProfileScreen(navController)
        }

        composable<EyeFlushRoute.Camera> {
            CameraScreen(navController, location)
        }
    }

}
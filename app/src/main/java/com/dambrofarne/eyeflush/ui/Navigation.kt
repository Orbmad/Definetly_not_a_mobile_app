package com.dambrofarne.eyeflush.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.dambrofarne.eyeflush.data.repositories.database.User
import com.dambrofarne.eyeflush.ui.screens.camera.CameraScreen
import com.dambrofarne.eyeflush.ui.screens.gamification.GamificationScreen
import com.dambrofarne.eyeflush.ui.screens.home.HomeMapScreen
import com.dambrofarne.eyeflush.ui.screens.markerOverview.MarkerOverviewScreen
import com.dambrofarne.eyeflush.ui.screens.notifications.NotificationScreen
import com.dambrofarne.eyeflush.ui.screens.profile.ProfileScreen
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigScreen
import com.dambrofarne.eyeflush.ui.screens.signin.SignInScreen
import com.dambrofarne.eyeflush.ui.screens.signup.SignUpScreen
import com.dambrofarne.eyeflush.ui.screens.splash.SplashScreen
import com.dambrofarne.eyeflush.ui.screens.userOverview.UserOverviewScreen
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
    @Serializable data object Game: EyeFlushRoute
    @Serializable data object Notification: EyeFlushRoute
    @Serializable data class MarkerOverview(val markerId : String) : EyeFlushRoute
    @Serializable data class UserOverview(val uId : String) : EyeFlushRoute

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
            ProfileScreen(navController)
            //MarkerOverviewScreen(navController,"NrzOJYoy3RfCUFxkMoOX")
            //HomeMapScreen(navController)
            //UserOverviewScreen(navController,"uYyDPYNRuHZXVogRVwYXMIraRZF3")
        }

        composable<EyeFlushRoute.ProfileConfig> {
            ProfileConfigScreen(navController)
        }

        composable<EyeFlushRoute.Profile> {
            ProfileScreen(navController)
        }

        composable<EyeFlushRoute.Game> {
            GamificationScreen(navController)
        }

        composable<EyeFlushRoute.Notification> {
            NotificationScreen(navController)
        }

        composable<EyeFlushRoute.Camera> {
            CameraScreen(navController)
        }

        composable<EyeFlushRoute.MarkerOverview>{ backStackEntry ->
            val route = backStackEntry.toRoute<EyeFlushRoute.MarkerOverview>()
            val markerId = route.markerId
            MarkerOverviewScreen(navController,markerId)
        }

        composable<EyeFlushRoute.UserOverview>{ backStackEntry ->
            val route = backStackEntry.toRoute<EyeFlushRoute.UserOverview>()
            val uId =  route.uId
            UserOverviewScreen(navController,uId)
        }
    }

}
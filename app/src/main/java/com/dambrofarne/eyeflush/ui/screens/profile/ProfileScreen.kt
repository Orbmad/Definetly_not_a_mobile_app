package com.dambrofarne.eyeflush.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.ProfileImage
import com.dambrofarne.eyeflush.ui.composables.SignOutText
import com.dambrofarne.eyeflush.ui.composables.StandardHeadline
import com.dambrofarne.eyeflush.ui.composables.StandardText
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = koinViewModel<ProfileViewModel>()
){
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserProfileInfo()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(35.dp))
        StandardHeadline("Il tuo profilo")
        ProfileImage(
            url = uiState.profileImageUrl,
            borderSize = 2.dp,
            borderColor = Color.Gray,
            borderShape = CircleShape
        )
        StandardText(uiState.username)
        SignOutText {
            viewModel.signOut()
            navController.navigate(EyeFlushRoute.SignIn){
                popUpTo(0) { inclusive = true }
            }
        }
    }
}
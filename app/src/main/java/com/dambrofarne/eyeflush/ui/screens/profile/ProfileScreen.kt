package com.dambrofarne.eyeflush.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.dambrofarne.eyeflush.ui.composables.AuthenticationError
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.ImageGrid
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import com.dambrofarne.eyeflush.ui.composables.PolaroidOverlayCard
import com.dambrofarne.eyeflush.ui.composables.ProfileImage
import com.dambrofarne.eyeflush.ui.composables.SettingsButton
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
        viewModel.loadUserInfo()
    }

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        uiState.errorMessage != null -> {
            AuthenticationError(text = uiState.errorMessage!!)
        }
        else -> {
            if (uiState.showOverlay) {
                PolaroidOverlayCard(
                    imageUrl = uiState.imageUrlOverlay,
                    username = uiState.usernameOverlay,
                    timestamp = uiState.timestampOverlay,
                    likeCount = uiState.likeCountOverlay,
                    onDismiss = viewModel :: hideOverlay,
                    uId = uiState.uIdvOverlay,
                    userImage = uiState.userImageOverlay,
                    onUserClick = { userId ->
                        navController.navigate(EyeFlushRoute.UserOverview(userId))
                    },
                )
            }else{
                CustomScaffold(
                    title = "Your Profile",
                    showBackButton = true,
                    navController = navController,
                    currentScreen = NavScreen.PROFILE,
                    content = {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(Modifier.height(35.dp))
                            StandardHeadline("Il tuo profilo")
                            ProfileImage(
                                url = uiState.profileImagePath,
                                borderSize = 2.dp,
                                borderColor = Color.Gray,
                                borderShape = CircleShape
                            )
                            StandardText(uiState.username)
                            SettingsButton(onClick = {
                                navController.navigate(EyeFlushRoute.ProfileConfig)
                            })
                            SignOutText {
                                viewModel.signOut()
                                navController.navigate(EyeFlushRoute.SignIn){
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.outline
                            )

                            if (uiState.isUpdating) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(end = 8.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Text("Aggiornamento...", style = MaterialTheme.typography.labelMedium)
                                }
                            }

                            ImageGrid(
                                pictures = uiState.picturesTaken,
                                onImageClick = { clickedPicId ->
                                    viewModel.showOverlay(clickedPicId)
                                },
                                onToggleLike = viewModel::toggleLike,
                                enabled = !uiState.isUpdating
                            )
                        }
                    }
                )

            }
        }
    }
}
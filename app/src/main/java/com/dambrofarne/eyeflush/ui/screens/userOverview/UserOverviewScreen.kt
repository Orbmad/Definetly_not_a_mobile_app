package com.dambrofarne.eyeflush.ui.screens.userOverview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.ErrorMessage
import com.dambrofarne.eyeflush.ui.composables.ImageGrid
import com.dambrofarne.eyeflush.ui.composables.PolaroidOverlayCard
import com.dambrofarne.eyeflush.ui.composables.ProfileImage
import com.dambrofarne.eyeflush.ui.composables.UpdatingMessage
import com.dambrofarne.eyeflush.ui.screens.profile.BadgesRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserOverviewScreen(
    navController: NavHostController,
    uId: String,
    viewModel: UserOverviewViewModel = koinViewModel<UserOverviewViewModel>()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val newNotifications by viewModel.newNotifications.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo(uId)
    }


    CustomScaffold(
        title = "User Overview",
        showBackButton = true,
        navController = navController,
        currentScreen = null,
        newNotification = newNotifications,
        content = {
            when {
                uiState.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null -> {
                    ErrorMessage(uiState.errorMessage!!)
                }

                else -> {
                    if (uiState.showOverlay) {
                        PolaroidOverlayCard(
                            imageUrl = uiState.imageUrlOverlay,
                            username = uiState.usernameOverlay,
                            timestamp = uiState.timestampOverlay,
                            likeCount = uiState.likeCountOverlay,
                            onDismiss = viewModel::hideOverlay,
                            uId = uiState.uIdvOverlay,
                            userImage = uiState.userImageOverlay,
                            onUserClick = { userId ->
                                navController.navigate(EyeFlushRoute.UserOverview(userId))
                            },
                        )
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(2.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(156.dp)
                                    .padding(horizontal = 12.dp)
                            ) {

                                // Left side - profile pic
                                val imageSize = 150.dp

                                Box(
                                    modifier = Modifier
                                        .size(imageSize)
                                ) {
                                    ProfileImage(
                                        url = uiState.profileImagePath,
                                        borderSize = 2.dp,
                                        borderColor = MaterialTheme.colorScheme.primary,
                                        borderShape = CircleShape,
                                        size = imageSize,

                                        )
                                }

                                // Right side - name and badges
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                ) {
                                    // Username
                                    Text(
                                        text = uiState.username,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier
                                            .align(Alignment.Start)
                                            .padding(start = 10.dp, bottom = 6.dp)
                                    )

                                    val iconSize = 20.dp

                                    //Score
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.Start)
                                            .padding(start = 10.dp, bottom = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.Cyclone,
                                            contentDescription = "Achievements",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(iconSize)
                                        )

                                        Text(
                                            text = uiState.score.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier
                                                .padding(start = 6.dp),
                                        )
                                    }

                                    //ImagesCount
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.Start)
                                            .padding(start = 10.dp, bottom = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {

                                        Icon(
                                            imageVector = Icons.Default.PhotoLibrary,
                                            contentDescription = "Achievements",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(iconSize)
                                        )

                                        //Score
                                        Text(
                                            text = uiState.imagesCount.toString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            modifier = Modifier
                                                .padding(start = 6.dp),
                                        )
                                    }


                                    // Badges
                                    BadgesRow(
                                        photoTaken = uiState.photoTaken,
                                        likes = uiState.likes,
                                        firstPlace = uiState.firstPlace,
                                        locations = uiState.locations,
                                    )
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.primary
                            )

                            UpdatingMessage(uiState.isUpdating)

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
                }

            }
        })
}
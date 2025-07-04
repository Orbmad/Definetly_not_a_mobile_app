package com.dambrofarne.eyeflush.ui.screens.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.ErrorMessage
import com.dambrofarne.eyeflush.ui.composables.IconButton
import com.dambrofarne.eyeflush.ui.composables.ImageGrid
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import com.dambrofarne.eyeflush.ui.composables.PolaroidOverlayCard
import com.dambrofarne.eyeflush.ui.composables.ProfileImage
import com.dambrofarne.eyeflush.ui.composables.UpdatingMessage
import com.dambrofarne.eyeflush.utils.AchievementRank
import com.dambrofarne.eyeflush.utils.AchievementType
import com.dambrofarne.eyeflush.utils.getAchievementIconId
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen(
    navController: NavHostController,
    viewModel: ProfileViewModel = koinViewModel<ProfileViewModel>()
){
    val uiState by viewModel.uiState.collectAsState()

    val newNotifications by viewModel.newNotifications.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo()
    }

    CustomScaffold(
        title = "Your Profile",
        showBackButton = false,
        navController = navController,
        currentScreen = NavScreen.PROFILE,
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
                            uId = uiState.uIdOverlay,
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

                                    IconButton(
                                        onClick = { navController.navigate(EyeFlushRoute.ProfileConfig(isFirstConfig = false)) },
                                        icon = Icons.Default.Settings,
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)

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
        }
    )
}

@Composable
fun BadgesRow(
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .padding(horizontal = 10.dp)
        .border(0.dp, MaterialTheme.colorScheme.onTertiaryContainer, RoundedCornerShape(24.dp))
        .clip(RoundedCornerShape(24.dp))
        .background(MaterialTheme.colorScheme.primaryContainer)
        .shadow(2.dp,RoundedCornerShape(24.dp)),
    photoTaken: AchievementRank,
    likes: AchievementRank,
    firstPlace: AchievementRank,
    locations: AchievementRank,
) {
    val badgesIdList: List<Int?> = listOf(
        getAchievementIconId(AchievementType.PHOTO_TAKEN, photoTaken),
        getAchievementIconId(AchievementType.LIKES, likes),
        getAchievementIconId(AchievementType.FIRST_PLACE, firstPlace),
        getAchievementIconId(AchievementType.LOCATION_VISITED, locations)
    )

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        badgesIdList.forEach { iconId ->
            if (iconId != null) {
                Image(
                    painter = painterResource(iconId),
                    contentDescription = "badge",
                    modifier = Modifier
                        .weight(1f)
                        .size(28.dp)
                )
            }
        }
    }
}
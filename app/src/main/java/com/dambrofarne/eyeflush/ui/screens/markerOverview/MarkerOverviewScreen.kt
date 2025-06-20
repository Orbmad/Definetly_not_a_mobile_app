package com.dambrofarne.eyeflush.ui.screens.markerOverview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.ErrorMessage
import com.dambrofarne.eyeflush.ui.composables.ImageCardSimple
import com.dambrofarne.eyeflush.ui.composables.ImageCardSimplified
import com.dambrofarne.eyeflush.ui.composables.ImageLabel
import com.dambrofarne.eyeflush.ui.composables.PageTitle
import com.dambrofarne.eyeflush.ui.composables.PolaroidOverlayCard
import com.dambrofarne.eyeflush.ui.composables.UpdatingMessage
import com.dambrofarne.eyeflush.ui.composables.UserProfileRow
import org.koin.androidx.compose.koinViewModel

@Composable
fun MarkerOverviewScreen(
    navController: NavHostController,
    markerId : String,
    viewModel: MarkerOverviewViewModel = koinViewModel<MarkerOverviewViewModel >()
)  {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadMarkerInfo(markerId)
    }

    // Ranking Elem
    @Composable
    fun RankingRowElem(
        rank: Int,
        picInfo: PicQuickRef,
        onImageClick: (String) -> Unit,
        enabled: Boolean
    ) {
        var localLike by remember(picInfo.liked) { mutableStateOf(picInfo.liked) }
        var localLikeCount by remember(picInfo.likes, picInfo.liked) { mutableIntStateOf(picInfo.likes) }

        Row(
            modifier = Modifier
                .height(112.dp)
                .padding(horizontal = 16.dp)
        ) {
            // Ranking
            Text(
                text = "$rank#",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 12.dp, top = 14.dp)
                    .align(Alignment.CenterVertically)
            )

            // Image
            ImageCardSimplified(
                picture = picInfo,
                onClick = onImageClick,
                enabled = enabled,
                modifier = Modifier
                    .size(104.dp)
                    .padding(
                        start = 8.dp,
                        top = 8.dp,
                        bottom = 8.dp,
                        end = 16.dp)
            )

            // Info
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(18.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                UserProfileRow(
                    userId = picInfo.userId,
                    username = picInfo.username,
                    userImageUrl = picInfo.userImageUrl,
                    onUserClick = { navController.navigate(EyeFlushRoute.UserOverview(picInfo.userId)) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = if (localLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "likes",
                        tint = if (localLike) Color.Red else Color.White,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 12.dp)
                            .clickable {
                                viewModel.toggleLike(picInfo.picId)
                                localLike = !localLike
                                if (localLike) {
                                    localLikeCount += 1
                                } else {
                                    localLikeCount -= 1
                                }
                            }
                    )

                    Text(
                        text = "$localLikeCount",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            }

        }
    }

    CustomScaffold(
        title = "Marker Overview",
        showBackButton = true,
        navController = navController,
        currentScreen = null,
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
                            onDismiss = viewModel :: hideOverlay,
                            uId = uiState.uIdvOverlay,
                            userImage = uiState.userImageOverlay,
                            onUserClick = { userId ->
                                navController.navigate(EyeFlushRoute.UserOverview(userId))
                            },
                        )
                    }else{
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Top section with most liked image
                            val markerTitle = uiState.name ?: "[%.6f, %.6f]".format(
                                uiState.coordinates.latitude,
                                uiState.coordinates.longitude
                            )
                            PageTitle(markerTitle)
                            //StandardText("Pics taken here: " + uiState.imagesCount.toString())

                            Spacer(modifier = Modifier.height(20.dp))

                            uiState.mostLikedPicURL?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (uiState.mostLikedPicId != null) {
                                        ImageCardSimple(
                                            picId = uiState.mostLikedPicId!!,
                                            url = uiState.mostLikedPicURL!!,
                                            likes = uiState.mostLikedPicLikes,
                                            liked = uiState.userLikesMostLiked,
                                            onClick = { clickedPicId ->
                                                viewModel.showOverlay(clickedPicId)
                                            },
                                            onToggleLike = viewModel::toggleLike,
                                            modifier = Modifier
                                                .width(200.dp)
                                                .height(250.dp),
                                            enabled = !uiState.isUpdating
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(top = 4.dp),
                                        verticalArrangement = Arrangement.Top,
                                        horizontalAlignment = Alignment.Start
                                    ) {
                                        UserProfileRow(
                                            userId = uiState.mostLikedPicUserId,
                                            username = uiState.mostLikedPicUsername,
                                            userImageUrl = uiState.mostLikedPicUserImage,
                                            onUserClick = { userId ->
                                                navController.navigate(EyeFlushRoute.UserOverview(userId))
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(20.dp))
                                        ImageLabel("🕒 ${uiState.mostLikedPicTimeStamp}")
                                        ImageLabel("❤️ ${uiState.mostLikedPicLikes}")
                                    }
                                }
                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                UpdatingMessage(uiState.isUpdating)
                            }

                            // Rest of the ranking
                            LazyColumn {
                                itemsIndexed(uiState.picturesTaken) { index, picInfo ->
                                    RankingRowElem(
                                        rank = index + 2,
                                        picInfo = picInfo,
                                        onImageClick = { viewModel.showOverlay(picInfo.picId) },
                                        enabled = !uiState.isUpdating
                                    )
                                    HorizontalDivider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        })
}


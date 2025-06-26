package com.dambrofarne.eyeflush.ui.screens.markerOverview

import android.util.Log
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.dambrofarne.eyeflush.ui.composables.ImageCardSimplified
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

    val newNotifications by viewModel.newNotifications.collectAsState()

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
        var localLike by remember(picInfo.picId) { mutableStateOf(picInfo.liked) }
        var localLikeCount by remember(picInfo.picId) { mutableIntStateOf(picInfo.likes) }

        // Sync with new data
        LaunchedEffect(picInfo.liked, picInfo.likes) {
            localLike = picInfo.liked
            localLikeCount = picInfo.likes
        }

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

                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Icon(
                        imageVector = if (localLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "likes",
                        tint = if (localLike) Color.Red else MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .size(36.dp)
                            .padding(end = 12.dp)
                            .clickable {
                                localLike = !localLike
                                localLikeCount += if (localLike) 1 else -1
                                viewModel.toggleLike(picInfo.picId)
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
                            val markerTitle = uiState.name ?: "[ %.6f, %.6f ]".format(
                                uiState.coordinates.latitude,
                                uiState.coordinates.longitude
                            )
                            PageTitle(markerTitle)
                            //StandardText("Pics taken here: " + uiState.imagesCount.toString())

                            UpdatingMessage(uiState.isUpdating)

                            // Most Liked Pic
                            uiState.mostLikedPicURL?.let {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val mostLikedPic = PicQuickRef(
                                        picId = uiState.mostLikedPicId!!, // check
                                        url = uiState.mostLikedPicURL!!, // check
                                        liked = uiState.userLikesMostLiked,
                                        likes = uiState.mostLikedPicLikes,
                                        userId = uiState.mostLikedPicUserId!!, // check
                                        username = uiState.mostLikedPicUsername!!, //check
                                        userImageUrl = uiState.mostLikedPicUserImage!!, // check
                                        timeStamp = uiState.mostLikedPicTimeStamp!! // check
                                    )
                                    var localLike by remember(mostLikedPic.picId) { mutableStateOf(mostLikedPic.liked) }
                                    var localLikeCount by remember(mostLikedPic.picId) { mutableIntStateOf(mostLikedPic.likes) }

                                    // Sync with new data
                                    LaunchedEffect(mostLikedPic.liked, mostLikedPic.likes) {
                                        localLike = mostLikedPic.liked
                                        localLikeCount = mostLikedPic.likes
                                    }

                                    ImageCardSimplified(
                                        picture = mostLikedPic,
                                        onClick = { viewModel.showOverlay(mostLikedPic.picId) },
                                        modifier = Modifier
                                            .width(200.dp)
                                            .height(250.dp),
                                        enabled = !uiState.isUpdating
                                    )


                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Photo info
                                    Column(
                                        modifier = Modifier
                                            .align(Alignment.Top)
                                    ) {
                                        // Ranking
                                        Text(
                                            text = "1#",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier
                                                .padding(top = 16.dp, bottom = 12.dp)
                                                .align(Alignment.CenterHorizontally)
                                        )

                                        UserProfileRow(
                                            userId = uiState.mostLikedPicUserId,
                                            username = uiState.mostLikedPicUsername,
                                            userImageUrl = uiState.mostLikedPicUserImage,
                                            onUserClick = { userId ->
                                                navController.navigate(EyeFlushRoute.UserOverview(userId))
                                            }
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        // Timestamp
                                        Row(
                                            modifier = Modifier
                                                .padding(start = 6.dp, bottom = 8.dp),
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.CalendarToday,
                                                contentDescription = "timestamp",
                                                tint = MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier
                                                    .size(32.dp)
                                                    .padding(end = 12.dp)
                                            )
                                            Text(
                                                text = mostLikedPic.timeStamp,
                                                color = MaterialTheme.colorScheme.onBackground,
                                                style = MaterialTheme.typography.bodySmall,
                                                modifier = Modifier.align(Alignment.CenterVertically)
                                            )
                                        }

                                        // Likes
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(start = 4.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (localLike) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "likes",
                                                tint = if (localLike) Color.Red else MaterialTheme.colorScheme.onBackground,
                                                modifier = Modifier
                                                    .size(36.dp)
                                                    .padding(end = 12.dp)
                                                    .clickable {
                                                        localLike = !localLike
                                                        localLikeCount += if (localLike) 1 else -1
                                                        viewModel.toggleLike(mostLikedPic.picId)
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

                                HorizontalDivider(
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    thickness = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            // Rest of the ranking
                            LazyColumn {
                                Log.w("MarkerOverview", "LazyColumnLoaded")
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


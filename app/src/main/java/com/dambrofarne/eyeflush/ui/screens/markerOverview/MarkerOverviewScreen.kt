package com.dambrofarne.eyeflush.ui.screens.markerOverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.AuthenticationError
import com.dambrofarne.eyeflush.ui.composables.ImageCard
import com.dambrofarne.eyeflush.ui.composables.ImageCardSimple
import com.dambrofarne.eyeflush.ui.composables.ImageGrid
import com.dambrofarne.eyeflush.ui.composables.ImageLabel
import com.dambrofarne.eyeflush.ui.composables.PageTitle
import com.dambrofarne.eyeflush.ui.composables.StandardHeadline
import com.dambrofarne.eyeflush.ui.composables.StandardText
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(WindowInsets.safeDrawing.asPaddingValues()) // Evita notch/cutouts
                    .padding(16.dp) // Spaziatura interna extra
            ) {
                // Top section with most liked image
                val markerTitle = uiState.name ?: "[%.6f, %.6f]".format(
                    uiState.coordinates.latitude,
                    uiState.coordinates.longitude
                )
                PageTitle(markerTitle)
                StandardText("Immagini scattate qui: " + uiState.imagesCount.toString())
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
                Spacer(modifier = Modifier.height(20.dp))

                uiState.mostLikedPicURL?.let { url ->
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
                                onClick = { /* ... */ },
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
                        color = MaterialTheme.colorScheme.outline
                    )
                }


                ImageGrid(
                    pictures = uiState.picturesTaken,
                    onImageClick = { clickedPicId ->
                        // navController.navigate("pictureDetail/$clickedPicId")
                    },
                    onToggleLike = viewModel::toggleLike,
                    enabled = !uiState.isUpdating
                )
            }
        }
    }
}
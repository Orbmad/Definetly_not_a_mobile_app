package com.dambrofarne.eyeflush.ui.screens.markerOverview

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import com.dambrofarne.eyeflush.ui.composables.AuthenticationError
import com.dambrofarne.eyeflush.ui.composables.ImageGrid
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
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                // Top section with most liked image
                uiState.mostLikedPicURL?.let { url ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Most liked image
                        Image(
                            painter = rememberAsyncImagePainter(url),
                            contentDescription = "Most Liked",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(4f / 5f)
                                .clip(RoundedCornerShape(16.dp))
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Image details
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Most Liked Image", style = MaterialTheme.typography.titleMedium)
                            Text("Pic ID: ${uiState.mostLikedPicId ?: "N/A"}")
                            Text("By: ${uiState.mostLikedPicUserId ?: "Unknown"}")
                            Text("Likes: ${uiState.mostLikedPicLikes ?: 0}")
                        }
                    }
                }

                // Grid section
                ImageGrid(urls = uiState.picturesTaken.map { it.url })
            }
        }
    }
}
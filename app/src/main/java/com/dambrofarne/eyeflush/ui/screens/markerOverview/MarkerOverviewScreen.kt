package com.dambrofarne.eyeflush.ui.screens.markerOverview

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
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
import com.dambrofarne.eyeflush.ui.composables.PageTitle
import com.dambrofarne.eyeflush.ui.composables.StandardHeadline
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
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 4.dp), // porta il contenuto un po' più in alto
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // Riga con immagine profilo e autore
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Piccolo pallino dell'immagine utente
                                uiState.mostLikedPicUserImage?.let { imageUrl ->
                                    Image(
                                        painter = rememberAsyncImagePainter(imageUrl),
                                        contentDescription = "User Profile",
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(RoundedCornerShape(50)) // cerchio
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }

                                Text(
                                    text = uiState.mostLikedPicUserId?: "Autore sconosciuto",
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }

                            // Likes
                            Text(
                                text = "❤️ ${uiState.mostLikedPicLikes}",
                                style = MaterialTheme.typography.labelMedium
                            )

                            // (Opzionale) Data
                            uiState.mostLikedPicTimeStamp?.let { timestamp ->
                                Text(
                                    text = "🕒 $timestamp",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }
                }

                // Grid section
                ImageGrid(urls = uiState.picturesTaken.map { it.url })
            }
        }
    }
}
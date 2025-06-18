package com.dambrofarne.eyeflush.ui.screens.userOverview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.composables.AuthenticationError
import com.dambrofarne.eyeflush.ui.composables.ImageGrid
import com.dambrofarne.eyeflush.ui.composables.ImageLabel
import com.dambrofarne.eyeflush.ui.composables.PageTitle
import com.dambrofarne.eyeflush.ui.composables.ProfileImage
import org.koin.androidx.compose.koinViewModel

@Composable
fun UserOverviewScreen(
    navController: NavHostController,
    uId : String,
    viewModel: UserOverviewViewModel = koinViewModel<UserOverviewViewModel>()
)  {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.loadUserInfo(uId)
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
                    .padding(16.dp)
            ) {
                PageTitle(uiState.username)
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
                Row(){
                    ProfileImage(uiState.profileImagePath)
                    Column {
                        ImageLabel("🕒 ${uiState.imagesCount}")
                        ImageLabel("❤️ ${uiState.score}")
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )

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
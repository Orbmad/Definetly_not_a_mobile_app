package com.dambrofarne.eyeflush.ui.screens.profileconfig

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.composables.ChoicheProfileImage
import com.dambrofarne.eyeflush.ui.composables.EyeFlushTextField
import com.dambrofarne.eyeflush.ui.composables.IconImage
import com.dambrofarne.eyeflush.ui.composables.ImagePickerDialog
import com.dambrofarne.eyeflush.ui.composables.StandardHeadline
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileConfigScreen(
    navController: NavHostController,
    viewModel: ProfileConfigViewModel = koinViewModel<ProfileConfigViewModel>()
){
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                UiEvent.OpenGallery -> {
                    showDialog = true
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadUserProfileImage()
    }

    ImagePickerDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = { uri ->
            showDialog = false
            uri?.let {
                coroutineScope.launch {
                    viewModel.onImageSelected(it)
                }
            }
        }
    )


    val imageSize = 240.dp
    val padding = 8.dp // distanza interna tra immagine e bordo del Box

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StandardHeadline("Configurazione Profilo")
        Spacer(Modifier.height(16.dp))
        val boxPadding = 8.dp

        Box(
            modifier = Modifier
                .size(imageSize + boxPadding * 2)
        ) {
            ChoicheProfileImage(
                url = uiState.profileImageUrl,
                borderSize = 2.dp,
                borderColor = Color.Gray,
                borderShape = CircleShape
            )

            IconImage(
                url = "iconUrl",
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = (-20).dp, y = (-20).dp)
                    .clickable{ viewModel.onPickPhotoClick()}
            )
        }

        EyeFlushTextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = "Username",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}
package com.dambrofarne.eyeflush.ui.screens.profileconfig

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.ChoiceProfileImage
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.CustomStandardButton
import com.dambrofarne.eyeflush.ui.composables.ErrorMessage
import com.dambrofarne.eyeflush.ui.composables.EyeFlushTextField
import com.dambrofarne.eyeflush.ui.composables.IconButton
import com.dambrofarne.eyeflush.ui.composables.ImagePickerDialog
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import com.dambrofarne.eyeflush.ui.composables.ThemePreferenceSelector
import com.dambrofarne.eyeflush.ui.composables.UpdatingMessage
import com.dambrofarne.eyeflush.ui.theme.ThemeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel


@Composable
fun ProfileConfigScreen(
    navController: NavHostController,
    viewModel: ProfileConfigViewModel = koinViewModel<ProfileConfigViewModel>(),
    themeViewModel: ThemeViewModel,
    isFirstConfig: Boolean = true
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val pref by themeViewModel.themePreference.collectAsState()

    var newNotifications by remember { mutableStateOf(false) }

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
        newNotifications = viewModel.checkNotifications()
    }

    CustomScaffold(
        title = "Profile Configuration",
        showBackButton = true,
        navController = navController,
        currentScreen = NavScreen.PROFILE,
        content = {
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


            val imageSize = 200.dp
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 16.dp)
                ) {

                UpdatingMessage(uiState.isLoading, text = "Loading...")

                val boxPadding = 8.dp

                Box(
                    modifier = Modifier
                        .size(imageSize + boxPadding * 2)
                        //.align(Alignment.CenterHorizontally)
                ) {
                    ChoiceProfileImage(
                        url = uiState.profileImageUrl,
                        borderSize = 2.dp,
                        borderShape = CircleShape
                    )

                    IconButton(
                        onClick = { viewModel.onPickPhotoClick() },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-20).dp, y = (-20).dp)
                    )
                }



                Column {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    ){
                        EyeFlushTextField(
                            value = uiState.username,
                            onValueChange = viewModel::onUsernameChange,
                            label = "Username",
                            height = 70.dp,
                            placeholder = uiState.username,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .padding(end = 0.dp)
                        )

                        CustomStandardButton(
                            text = if (!isFirstConfig) "EDIT" else "CONFIRM",
                            modifier = Modifier
                                .padding(horizontal = 22.dp)
                                .fillMaxWidth()
                                .weight(1f)
                        ) {
                            viewModel.setUsername {
                                if (!isFirstConfig) {
                                    navController.popBackStack()
                                } else {
                                    navController.navigate(EyeFlushRoute.Home)
                                }
                            }
                        }
                    }

                    ErrorMessage(uiState.connectionError)
                    ErrorMessage(uiState.usernameError)
                }

                if(!isFirstConfig){
                    Spacer(Modifier.height(24.dp))

                    ThemePreferenceSelector(
                        currentPref = pref,
                        onPreferenceChange = { newPref ->
                            themeViewModel.setThemePreference(newPref)
                        }
                    )

                    Spacer(Modifier.height(24.dp))

                    CustomStandardButton(
                        text = "SIGN OUT",
                        onClickFun = {
                            viewModel.signOut()
                            navController.navigate(EyeFlushRoute.SignIn) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }
        }
    )
}
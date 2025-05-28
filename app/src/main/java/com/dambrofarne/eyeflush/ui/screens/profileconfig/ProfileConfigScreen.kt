package com.dambrofarne.eyeflush.ui.screens.profileconfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.composables.EyeFlushTextField
import com.dambrofarne.eyeflush.ui.composables.StandardHeadline
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileConfigScreen(
    navController: NavHostController,
    viewModel: ProfileConfigViewModel = koinViewModel<ProfileConfigViewModel>()
){
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        StandardHeadline("Configurazione Profilo")
        Spacer(Modifier.height(16.dp))
        EyeFlushTextField(
            value = uiState.username,
            onValueChange = viewModel::onUsernameChange,
            label = "Username",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
    }
}
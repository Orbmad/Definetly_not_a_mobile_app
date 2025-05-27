package com.dambrofarne.eyeflush.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.screens.signin.SignInViewModel

@Composable
fun HomeScreen(
    navController: NavHostController,
    //viewModel: SignInViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Home Page!!", style = MaterialTheme.typography.headlineMedium)
    }
}
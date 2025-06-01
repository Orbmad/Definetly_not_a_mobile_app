package com.dambrofarne.eyeflush.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.data.constants.IconPaths.ACCOUNT_ICON
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.IconImage
import org.koin.androidx.compose.koinViewModel


@Composable
fun HomeScreen(
    navController: NavHostController,
    viewModel: HomeViewModel= koinViewModel<HomeViewModel>()
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.weight(1f)) //Spingo in fondo a destra
            IconImage(
                image = ACCOUNT_ICON,
                modifier = Modifier
                    .clickable{ navController.navigate(EyeFlushRoute.Profile)}
            )
        }
        Text("Home Page!!", style = MaterialTheme.typography.headlineMedium)
    }
}
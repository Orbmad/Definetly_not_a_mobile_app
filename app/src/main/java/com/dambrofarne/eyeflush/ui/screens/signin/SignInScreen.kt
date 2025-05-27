package com.dambrofarne.eyeflush.ui.screens.signin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.CustomStandardButton
import com.dambrofarne.eyeflush.ui.composables.EyeFlushTextField
import com.dambrofarne.eyeflush.ui.composables.GoogleButton
import com.dambrofarne.eyeflush.ui.composables.SignUpText
import kotlinx.coroutines.launch

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Bentornato...", style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(16.dp))

        EyeFlushTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        uiState.emailError?.let {
            if (it.isNotEmpty()) {
                Text(it, color = Color.Red, fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
        }

        Spacer(Modifier.height(8.dp))

        EyeFlushTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            //visualTransformation = PasswordVisualTransformation()
        )
        uiState.passwordError?.let {
            if (it.isNotEmpty()) {
                Text(it, color = Color.Red, fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
        }

        Spacer(Modifier.height(16.dp))

        CustomStandardButton("Accedi") {
            viewModel.signIn {
                navController.navigate(EyeFlushRoute.Home) {
                    popUpTo(EyeFlushRoute.Splash) { inclusive = true }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        GoogleButton("Accedi con Google", {
            coroutineScope.launch {
                val idToken = viewModel.requestGoogleCredential(context)
                if (idToken != null) {
                    viewModel.signInWithGoogle(context, idToken) {
                        navController.navigate(EyeFlushRoute.Home) {
                            popUpTo(EyeFlushRoute.Splash) { inclusive = true }
                        }
                    }
                }
            }
        })

        SignUpText {
            navController.navigate(EyeFlushRoute.SignUp)
        }

        uiState.connectionError?.let {
            if (it.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(it, color = Color.Red, fontSize = MaterialTheme.typography.bodySmall.fontSize)
            }
        }
    }
}

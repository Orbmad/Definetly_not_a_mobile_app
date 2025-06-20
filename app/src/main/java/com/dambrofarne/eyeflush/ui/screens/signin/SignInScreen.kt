package com.dambrofarne.eyeflush.ui.screens.signin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.CustomStandardButton
import com.dambrofarne.eyeflush.ui.composables.ErrorMessage
import com.dambrofarne.eyeflush.ui.composables.EyeFlushTextField
import com.dambrofarne.eyeflush.ui.composables.SignUpText
import com.dambrofarne.eyeflush.ui.composables.UpdatingMessage
import org.koin.androidx.compose.koinViewModel

@Composable
fun SignInScreen(
    navController: NavHostController,
    viewModel: SignInViewModel = koinViewModel<SignInViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        UpdatingMessage(uiState.isLoading, text = "Loading...")
        Text("Welcome back ! ", style = MaterialTheme.typography.titleLarge)

        Spacer(Modifier.height(16.dp))

        EyeFlushTextField(
            value = uiState.email,
            onValueChange = viewModel::onEmailChange,
            label = "Email",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        ErrorMessage(uiState.emailError)

        Spacer(Modifier.height(8.dp))

        EyeFlushTextField(
            value = uiState.password,
            onValueChange = viewModel::onPasswordChange,
            label = "Password",
            isPassword = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            //visualTransformation = PasswordVisualTransformation()
        )
        ErrorMessage(uiState.passwordError)


        Spacer(Modifier.height(16.dp))

        CustomStandardButton("Sign In") {
            viewModel.signIn(
                navToHome =  {navController.navigate(EyeFlushRoute.Home) },
                navToProfileConfig = {navController.navigate((EyeFlushRoute.ProfileConfig()))}
            )
        }

        Spacer(Modifier.height(16.dp))

        SignUpText {
            navController.navigate(EyeFlushRoute.SignUp)
        }

        ErrorMessage(uiState.connectionError)
    }
}

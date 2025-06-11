package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dambrofarne.eyeflush.R
import com.dambrofarne.eyeflush.data.constants.IconPaths.ACCOUNT_ICON
import com.dambrofarne.eyeflush.ui.theme.Purple80

@Composable
fun CustomStandardButton(
    text: String = "Accedi",
    onClickFun: () -> Unit = {}
){
    Button(
        onClick = onClickFun,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer

        )
    ) {
        Text(text)
    }
}

@Composable
fun GoogleButton(
    text: String = "Accedi con Google",
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color.Gray),
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_google_logo),
            contentDescription = "Google Logo",
            modifier = Modifier.size(24.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, color = Color.Black)
    }
}

@Composable
fun SignUpText(onClick: () -> Unit) {
    Text(
        text = "Non hai un account? Registrati",
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun SignInText(onClick: () -> Unit) {
    Text(
        text = "Hai già un account? Accedi",
        color = MaterialTheme.colorScheme.onBackground ,
        fontSize = 14.sp,
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable(onClick = onClick)
    )
}

@Composable
fun SignOutText(onClick: () -> Unit) {
    Text(
        text = "Esci dall'account...",
        color = MaterialTheme.colorScheme.onBackground ,
        fontSize = 14.sp,
        modifier = Modifier
            .padding(top = 16.dp)
            .clickable(onClick = onClick)
    )
}

// Camera button
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraButton(
    onClick: () -> Unit,
    modifier: Modifier
    ) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(
            imageVector = Icons.Default.Camera,
            contentDescription = "Take Photo"
        )
    }
}

@Composable
fun ProfileIcon(onClick: () -> Unit, modifier: Modifier = Modifier) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        modifier = modifier
    ) {
        IconImage(
            image = ACCOUNT_ICON
        )
    }
}

@Composable
fun BackButton(
    onClick: () -> Unit,
    modifier: Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

// Needs Updates
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTopAppBar(
    modifier: Modifier = Modifier,
    backButton: @Composable (() -> Unit)? = null,
    profileIcon: Boolean = true
) {
    Row (
        modifier = modifier
    ) {
        if (backButton != null) {

        }
    }
}




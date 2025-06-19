package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dambrofarne.eyeflush.data.constants.IconPaths.ACCOUNT_ICON

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
            modifier = Modifier.size(74.dp),
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
fun SettingsButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
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
            Icons.Filled.Settings,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ThemeToggle(
    isDark: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = if (isDark) "Modalità Scura" else "Modalità Chiara",
            style = MaterialTheme.typography.bodyLarge
        )
        Switch(
            checked = isDark,
            onCheckedChange = onToggle
        )
    }
}




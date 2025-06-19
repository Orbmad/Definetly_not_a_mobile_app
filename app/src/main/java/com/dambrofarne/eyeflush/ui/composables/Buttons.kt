package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dambrofarne.eyeflush.data.constants.IconPaths.ACCOUNT_ICON
import com.dambrofarne.eyeflush.ui.theme.ThemePreference

@Composable
fun CustomStandardButton(
    text: String = "Sign in",
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
        text = "Don't have an account ? Sign up",
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
        text = "Already have an account ? Sign in",
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
        text = "Log out ...",
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

val radioButtonRowHeight = 24.dp
@Composable
fun ThemePreferenceSelector(
    currentPref: ThemePreference,
    onPreferenceChange: (ThemePreference) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = "Theme preference:",
            modifier = Modifier.padding(bottom = 4.dp),
            style = MaterialTheme.typography.bodyLarge.copy(lineHeight = 18.sp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPreferenceChange(ThemePreference.SYSTEM) }
                .padding(vertical = 2.dp)
                .height(radioButtonRowHeight)
        ) {
            RadioButton(
                selected = currentPref == ThemePreference.SYSTEM,
                onClick = { onPreferenceChange(ThemePreference.SYSTEM) }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "System theme",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 16.sp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPreferenceChange(ThemePreference.LIGHT) }
                .padding(vertical = 2.dp)
                .height(radioButtonRowHeight)
        ) {
            RadioButton(
                selected = currentPref == ThemePreference.LIGHT,
                onClick = { onPreferenceChange(ThemePreference.LIGHT) }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Light Theme",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 16.sp)
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onPreferenceChange(ThemePreference.DARK) }
                .padding(vertical = 2.dp)
                .height(radioButtonRowHeight)
        ) {
            RadioButton(
                selected = currentPref == ThemePreference.DARK,
                onClick = { onPreferenceChange(ThemePreference.DARK) }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Dark Theme",
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 16.sp)
            )
        }
    }
}




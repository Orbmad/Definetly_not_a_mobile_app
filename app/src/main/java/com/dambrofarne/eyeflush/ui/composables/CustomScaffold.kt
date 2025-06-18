package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.EyeFlushRoute

enum class NavScreen {
    HOME,
    GAME,
    NOTIFICATIONS,
    PROFILE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomScaffold(
    title: String = "EyeFlush",
    showBackButton: Boolean,
    navController: NavHostController,
    content: @Composable () -> Unit,
    currentScreen: NavScreen?
) {
    Scaffold(
        topBar = {
//            CenteredTitleTopAppBar(
//                title = "EyeFlush",
//                showBackButton = showBackButton,
//                onBackClick = { navController.popBackStack() }
//            )
            CenteredTitleTopAppBar(
                title = title,
                showBackButton = showBackButton,
                onBackClick = { navController.popBackStack() }
            )
        },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                content()
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.height(86.dp)
            ) {
                val iconColor = MaterialTheme.colorScheme.primary

                // Home button
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Home,
                            contentDescription = "Home",
                            tint = iconColor
                        )
                    },
                    selected = (currentScreen == NavScreen.HOME),
                    onClick = { navController.navigate(EyeFlushRoute.Home) }
                )

                // Game Button
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.RemoveRedEye,
                            contentDescription = "Gamification",
                            tint = iconColor
                        )
                    },
                    selected = (currentScreen == NavScreen.GAME),
                    onClick = { navController.navigate(EyeFlushRoute.Game) }
                )

                // Notifications Button
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = iconColor
                        )
                    },
                    selected = ( currentScreen == NavScreen.NOTIFICATIONS ),
                    onClick = { navController.navigate(EyeFlushRoute.Notification) }
                )

                // Profile Button
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = iconColor
                        )
                    },
                    selected = ( currentScreen == NavScreen.PROFILE ),
                    onClick = { navController.navigate(EyeFlushRoute.Profile) }
                )
            }
        }
    )
}

@Composable
fun CenteredTitleTopAppBar(
    title: String,
    showBackButton: Boolean = true,
    onBackClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    height: Dp = 48.dp
) {
    val insets = WindowInsets.statusBars.asPaddingValues()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height + insets.calculateTopPadding())
            .background(backgroundColor)
            .padding(top = insets.calculateTopPadding())
    ) {
        // Back arrow
        if (showBackButton) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = contentColor,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .clickable(onClick = onBackClick)
                    .size(24.dp)
            )
        }

        // Titolo centrato
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


//@Composable
//fun CenteredTitleTopAppBar(
//    title: String,
//    showBackButton: Boolean,
//    onBackClick: () -> Unit = {},
//    modifier: Modifier = Modifier,
//    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer,
//    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer
//) {
//    Box(
//        modifier = modifier
//            .fillMaxWidth()
//            .height(64.dp)
//            .background(backgroundColor)
//    ) {
//        if (showBackButton) {
//            IconButton(
//                onClick = onBackClick,
//                modifier = Modifier
//                    .align(Alignment.CenterStart)
//                    .padding(start = 8.dp)
//            ) {
//                Icon(
//                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
//                    contentDescription = "Back",
//                    tint = contentColor
//                )
//            }
//        }
//
//        Text(
//            text = title,
//            color = contentColor,
//            style = MaterialTheme.typography.titleLarge,
//            modifier = Modifier.align(Alignment.Center)
//        )
//    }
//}

@Composable
fun BackButton(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier
) {
    if (onClick != null) {
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
}

@Composable
fun CustomTopBar(
    title: String = "",
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    actions: (@Composable RowScope.() -> Unit)? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
    contentColor: Color = MaterialTheme.colorScheme.onBackground
) {
    val height = 64.dp
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = backgroundColor,
        contentColor = contentColor,
        shadowElevation = 4.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Navigation icon (es. back button)
            Box(
                modifier = Modifier
                    .height(height) // touch target
                    .width(50.dp),
                contentAlignment = Alignment.BottomCenter
            ) {
                navigationIcon?.invoke() ?: Spacer(
                    modifier = Modifier.width(48.dp)
                )
            }

            // Centered title
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1,
                    textAlign = TextAlign.Center
                )
            }

            // Action icons
            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .height(height), // same height of navigation icon
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.End
            ) {
                actions?.invoke(this)
            }
        }
    }
}
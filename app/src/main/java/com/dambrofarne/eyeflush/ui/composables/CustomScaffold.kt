package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

@Composable
fun CustomScaffold(
    title: String = "EyeFlush",
    showBackButton: Boolean,
    navController: NavHostController,
    content: @Composable () -> Unit,
    currentScreen: NavScreen?,
    newNotification: Boolean = false
) {
    Scaffold(
        modifier = Modifier
            .statusBarsPadding(),
        topBar = {
            Column {
                CenteredTitleTopAppBar(
                    title = title,
                    showBackButton = showBackButton,
                    onBackClick = { navController.popBackStack() }
                )

                GradientHorizontalDivider()
            }

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
            Column {
                GradientHorizontalDivider(
                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.surfaceVariant)
                )

                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .height(98.dp)
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(4.dp),
                            clip = false
                        )
                        .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Bottom))
                ) {
                    val iconColor = MaterialTheme.colorScheme.primary
                    val iconSize = 42.dp

                    // Home button
                    NavigationBarItem(
                        modifier = Modifier,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Home,
                                contentDescription = "Home",
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        selected = (currentScreen == NavScreen.HOME),
                        onClick = { navController.navigate(EyeFlushRoute.Home) }
                    )

                    // Game Button
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Cyclone,
                                contentDescription = "Gamification",
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        selected = (currentScreen == NavScreen.GAME),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onTertiaryContainer
                        ),
                        onClick = { navController.navigate(EyeFlushRoute.Game) }
                    )

                    // Notifications Button
                    NavigationBarItem(
                        icon = {
                            Icon(
                                imageVector = if (newNotification) Icons.Default.NotificationImportant else Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
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
                                tint = iconColor,
                                modifier = Modifier.size(iconSize)
                            )
                        },
                        selected = ( currentScreen == NavScreen.PROFILE ),
                        onClick = { navController.navigate(EyeFlushRoute.Profile) }
                    )
                }
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
    contentColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 48.dp
) {
    val insets = WindowInsets.statusBars.asPaddingValues()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height + insets.calculateTopPadding())
            .background(backgroundColor)
            .padding(top = insets.calculateTopPadding(), bottom = 8.dp)
    ) {
        // Back arrow
        if (showBackButton) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = contentColor,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .clickable(onClick = onBackClick)
                    .size(42.dp)
            )
        }

        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}


@Composable
fun GradientHorizontalDivider(
    modifier: Modifier = Modifier,
    height: Dp = 2.dp,
    colors: List<Color> = listOf(MaterialTheme.colorScheme.surfaceVariant, Color.Transparent)
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(
                brush = Brush.verticalGradient(colors)
            )
    )
}

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
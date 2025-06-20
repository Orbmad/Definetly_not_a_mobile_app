package com.dambrofarne.eyeflush.ui.screens.notifications

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.DismissDirection
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.data.repositories.database.NotificationItem
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import org.koin.androidx.compose.koinViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.dambrofarne.eyeflush.ui.EyeFlushRoute

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationScreen(
    navController: NavHostController,
    viewModel: NotificationViewModel = koinViewModel<NotificationViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    val readNotifications = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        viewModel.loadNotifications()
    }

    CustomScaffold(
        title = "Notifications",
        showBackButton = false,
        navController = navController,
        currentScreen = NavScreen.NOTIFICATIONS,
        content = {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    if (uiState.notificationsList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "You have no notifications for now...",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                        }
                    } else {
                        HorizontalDivider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.primaryContainer
                        )
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(1.dp),
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            items(uiState.notificationsList, key = { it.id }) { notification ->
                                val dismissState = rememberDismissState()

                                LaunchedEffect(dismissState.currentValue) {
                                    if (dismissState.isDismissed(DismissDirection.StartToEnd)) {
                                        if (!notification.isRead && !readNotifications.contains(notification.id)) {
                                            viewModel.markAsRead(notification.id)
                                            readNotifications.add(notification.id)
                                            dismissState.reset()
                                        } else {
                                            viewModel.deleteNotification(notification.id)
                                        }
                                    }
                                }

                                SwipeToDismiss(
                                    state = dismissState,
                                    directions = setOf(DismissDirection.StartToEnd),
                                    background = {
                                        SwipeBackground(
                                            isRead = notification.isRead || readNotifications.contains(notification.id)
                                        )
                                    },
                                    dismissContent = {
                                        NotificationCard(
                                            notification = notification,
                                            onClick = {
                                                val markerId = notification.referredMarkerId
                                                if (markerId != null) {
                                                    navController.navigate(EyeFlushRoute.MarkerOverview(markerId))
                                                }
                                            }
                                        )
                                    }
                                )

//                                HorizontalDivider(
//                                    thickness = 1.dp,
//                                    color = MaterialTheme.colorScheme.onTertiaryContainer,
//                                    modifier = Modifier.padding(start = 16.dp)
//                                )
                            }

                        }
                    }

                }
            }
        }
    )

}

@Composable
fun NotificationCard(
    notification: NotificationItem,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(
            topStart = 16.dp,
            bottomStart = 16.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) MaterialTheme.colorScheme.secondaryContainer
                else MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                notification.title,
                style = MaterialTheme.typography.titleMedium,
                color = if (notification.isRead) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(notification.message, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text(notification.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onTertiaryContainer)
        }
    }
}

@Composable
fun SwipeBackground(isRead: Boolean) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isRead) Color(0xFFE53935) else MaterialTheme.colorScheme.primary,
        label = "SwipeBackgroundColor"
    )

    val icon = if (isRead) Icons.Default.Delete else Icons.Default.Check
    val description = if (isRead) "Delete" else "Mark as Read"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(start = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Icon(
            imageVector = icon,
            contentDescription = description,
            tint = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}
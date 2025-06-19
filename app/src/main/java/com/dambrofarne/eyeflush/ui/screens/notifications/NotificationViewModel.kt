package com.dambrofarne.eyeflush.ui.screens.notifications

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.NotificationItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class NotificationUiState(
    val isLoading: Boolean = true,
    val notificationsList: List<NotificationItem> = emptyList<NotificationItem>()
)

class NotificationViewModel(
    private val db: DatabaseRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState

    suspend fun loadNotifications() {
        val userId = auth.getCurrentUserId()
        if (userId != null) {
            val loaded = db.getNotifications(userId)
            //Log.w("Notifications", "loaded notifications: $loaded")
            _uiState.update { 
                it.copy(
                    isLoading = false,
                    notificationsList = loaded
                )
            }
            //Log.w("Notifications", "notifications list: ${_uiState.value.notificationsList}")
        }
        //loadDummyNotifications()
    }

    private fun loadDummyNotifications() {
        _uiState.update {
            it.copy(
                notificationsList = listOf(
                    NotificationItem(
                        "1",
                        "LIKE",
                        "Your photo received a like",
                        "-Utente- ha messo like alla tua foto",
                        time = LocalDateTime.now().toString(),
                        isRead = false,
                        referredMarkerId = null
                    ),
                    NotificationItem(
                        "2",
                        "RANK_ONE",
                        "Your photo got first place",
                        "One of your photo is now first place",
                        time = LocalDateTime.now().toString(),
                        isRead = false,
                        referredMarkerId = null
                    )
                )
            )
        }
    }

    suspend fun markAsRead(notificationId: String) {
        val index = _uiState.value.notificationsList.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val newList: MutableList<NotificationItem> = _uiState.value.notificationsList.toMutableList()
            val updated = newList[index].copy(isRead = true)
            newList[index] = updated
            _uiState.update {
                it.copy(
                    notificationsList = newList
                )
            }
            updateNotificationState(notificationId)
        }
    }

    private suspend fun updateNotificationState(notificationId: String) {
        val userId = auth.getCurrentUserId()
        if (userId != null) {
            db.readNotification(userId, notificationId)
        }
    }
}
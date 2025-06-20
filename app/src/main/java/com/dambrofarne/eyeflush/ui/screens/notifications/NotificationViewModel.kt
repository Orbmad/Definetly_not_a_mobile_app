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
        loadDummyNotifications() // For debug

//        val userId = auth.getCurrentUserId()
//        if (userId != null) {
//            val loaded = db.getNotifications(userId)
//            _uiState.update {
//                it.copy(
//                    isLoading = false,
//                    notificationsList = loaded
//                )
//            }
//        }
    }

    private suspend fun loadDummyNotifications() {
        _uiState.update {
            it.copy(
                notificationsList = listOf(
                    NotificationItem(
                        "1",
                        "LIKE",
                        "Your photo received a like",
                        "-User- liked your photo",
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
                ),
                isLoading = false
            )
        }
    }

    suspend fun markAsRead(notificationId: String) {
        val index = _uiState.value.notificationsList.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val newList = _uiState.value.notificationsList.toMutableList()
            val updated = newList[index].copy(isRead = true)
            newList[index] = updated

            _uiState.update {
                it.copy(notificationsList = newList)
            }

            val userId = auth.getCurrentUserId()
            if (userId != null) {
                db.readNotification(userId, notificationId)
            }
        }
    }

    suspend fun deleteNotification(notificationId: String) {
        // Local notification removal
        val newList = _uiState.value.notificationsList.filterNot { it.id == notificationId }
        _uiState.update {
            it.copy(notificationsList = newList)
        }

        // Asynchronous deletion from DB
        val userId = auth.getCurrentUserId()
        if (userId != null) {
            try {
                db.deleteNotification(userId, notificationId)
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error: error during notification delete", e)
                // (optional) error SnackBar
            }
        }
    }
}
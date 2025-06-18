package com.dambrofarne.eyeflush.ui.screens.notifications

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.NotificationItem
import java.time.LocalDateTime

class NotificationViewModel(
    private val db: DatabaseRepository,
    private val auth: AuthRepository
) : ViewModel() {

    private val _notifications = mutableStateListOf<NotificationItem>()
    val notifications: List<NotificationItem> = _notifications

    init {
        loadNotifications()
    }

    private fun loadNotifications() {
        // TODO
        loadDummyNotifications()
    }

    private fun loadDummyNotifications() {
        _notifications.addAll(
            listOf(
                NotificationItem("1", "LIKE", "Your photo received a like", "-Utente- ha messo like alla tua foto", time = LocalDateTime.now().toString(), isRead = false, referredMarkerId = null),
                NotificationItem("2", "RANK_ONE", "Your photo got first place", "One of your photo is now first place", time = LocalDateTime.now().toString(), isRead = false, referredMarkerId = null)
            )
        )
    }

    fun markAsRead(notificationId: String) {
        val index = _notifications.indexOfFirst { it.id == notificationId }
        if (index != -1) {
            val updated = _notifications[index].copy(isRead = true)
            _notifications[index] = updated
            updateNotificationState(notificationId)
        }
    }

    private fun updateNotificationState(notificationId: String) {
        //TODO
    }
}
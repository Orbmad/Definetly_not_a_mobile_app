package com.dambrofarne.eyeflush.ui.screens.gamification

import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.UserAchievements
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import okhttp3.Dispatcher

data class AchievementUiState(
    val isLoading: Boolean = true,
    val userName: String = "",

    // likes received
    val likesReceived: Int = 0,
//    val likesReceivedRank: AchievementRank = AchievementRank.NONE,
//    val likesReceivedMaxPoints: Int = 0,
//    val likesReceivedBadge: ImageVector? = null,

    // pictures taken
    val picturesTaken: Int = 0,
//    val picturesTakenRank: AchievementRank = AchievementRank.NONE,
//    val picturesTakenMaxPoints: Int = 0,
//    val picturesTakenBadge: ImageVector? = null,

    // markers visited
    val markersVisited: Int = 0,
//    val markersVisitedRank: AchievementRank = AchievementRank.NONE,
//    val markersVisitedMaxPoints: Int = 0,
//    val markersVisitedBadge: ImageVector? = null,

    // most liked
    val mostLikedPictures: Int = 0,
//    val mostLikedRank: AchievementRank = AchievementRank.NONE,
//    val mostLikedMaxPoints: Int = 0,
//    val mostLikedBadge: ImageVector? = null

    val score: Int = 0
)

class GamificationViewModel(
    private val db: DatabaseRepository,
    private val auth: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AchievementUiState())
    val uiState: StateFlow<AchievementUiState> = _uiState

    private val _newNotifications = MutableStateFlow(false)
    val newNotifications: StateFlow<Boolean> = _newNotifications

    private fun checkNotifications(uId: String?): Boolean {
        if (uId != null) {
            return runBlocking {
                db.hasUnreadNotifications(uId)
            }
        }
        return false
    }

    suspend fun loadUserAchievements() {
        val userId = auth.getCurrentUserId()
        val userAchievements: UserAchievements = if (userId != null) {
            db.getUserAchievements(userId).getOrNull() ?: UserAchievements(0, 0, 0, 0)
        } else {
            UserAchievements(0,0,0,0)
        }
        _newNotifications.value = checkNotifications(userId)
        _uiState.update {
            it.copy(
                isLoading = false,
                likesReceived = userAchievements.likesReceived,
                picturesTaken = userAchievements.picturesTaken,
                markersVisited = userAchievements.markersPhotographed,
                mostLikedPictures = userAchievements.mostLikedPictures,
                score = userAchievements.score
            )
        }
    }

//    suspend fun loadDummyUserAchievements() {
//        _uiState.update {
//            it.copy(
//                isLoading = false,
//                likesReceived = 44,
//                picturesTaken = 11,
//                markersVisited = 7,
//                mostLikedPictures = 3
//            )
//        }
//    }

}
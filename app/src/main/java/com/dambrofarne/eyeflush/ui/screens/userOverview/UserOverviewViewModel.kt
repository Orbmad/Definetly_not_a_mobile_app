package com.dambrofarne.eyeflush.ui.screens.userOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef
import com.dambrofarne.eyeflush.utils.AchievementRank
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UserOverviewUiState(
    val id : String = "",
    val username: String = "",
    val profileImagePath: String = "",
    val imagesCount: Int = 0,
    val score : Int = 0,
    val picturesTaken: List<PicQuickRef> = emptyList(),
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,

    val showOverlay : Boolean = false,
    val imageUrlOverlay: String = "",
    val uIdvOverlay : String = "",
    val usernameOverlay : String = "",
    val userImageOverlay : String = "",
    val markerNameOverlay : String = "",
    val timestampOverlay: String = "",
    val likeCountOverlay : Int = 0,

    val photoTaken: AchievementRank = AchievementRank.NONE,
    val likes: AchievementRank = AchievementRank.NONE,
    val firstPlace: AchievementRank = AchievementRank.NONE,
    val locations: AchievementRank = AchievementRank.NONE,
)


class UserOverviewViewModel(
    private val db : DatabaseRepository,
    private val auth : AuthRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(UserOverviewUiState())
    val uiState: StateFlow<UserOverviewUiState> = _uiState

    private val _newNotification = MutableStateFlow(false)
    val newNotifications: StateFlow<Boolean> = _newNotification

    private suspend fun checkNotifications(): Boolean {
        val uId = auth.getCurrentUserId()
        if (uId != null) {
            return db.hasUnreadNotifications(uId)
        }
        return false
    }

    suspend fun loadUserInfo(uId: String, isInitialLoad: Boolean = true) {
        _uiState.update { it.copy(
            isLoading = if (isInitialLoad) true else it.isLoading,
            errorMessage = null
        ) }

        val requesterUId = auth.getCurrentUserId()
        if (requesterUId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "USer not authenticated") }
            return
        }


        val result = db.getUserExtendedInfo(uId, requesterUId )
        if (result.isSuccess) {
            val user = result.getOrNull()!!
            _uiState.update {
                it.copy(
                    id = user.uId,
                    username = user.username,
                    profileImagePath = user.profileImagePath,
                    imagesCount = user.imagesCount,
                    picturesTaken = user.picturesTaken,
                    isLoading = false,

                    photoTaken = user.picturesTakenLvl ?: AchievementRank.NONE,
                    likes = user.likesReceivedLvl ?: AchievementRank.NONE,
                    firstPlace = user.mostLikedPicturesLvl ?: AchievementRank.NONE,
                    locations = user.markersPhotographedLvl ?: AchievementRank.NONE,

                    score = user.score,
                )
            }
            _newNotification.value = checkNotifications()
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Unknown error"
            _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
        }
    }

    fun toggleLike(picId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }

            val currentUser = auth.getCurrentUserId()
            if (currentUser == null) {
                _uiState.update { it.copy(isUpdating = false, errorMessage = "User not authenticated") }
                return@launch
            }

            val result = db.likeImage(uId = currentUser, picId = picId)

            if (result.isSuccess) {
                loadUserInfo(
                    uId = _uiState.value.id,
                    isInitialLoad = false
                )
            } else {
                _uiState.update { it.copy(errorMessage = "Error in like toggle") }
            }

            _uiState.update { it.copy(isUpdating = false) }
        }
    }

    fun showOverlay(picId : String) {
        viewModelScope.launch {
            _uiState.update { it.copy(showOverlay = true) }
            val result = db.getPictureExtendedInfo(picId)
            if (result.isSuccess) {
                val picture = result.getOrNull()!!


                _uiState.update {
                    it.copy(
                        imageUrlOverlay = picture.url,
                        uIdvOverlay = picture.uId,
                        usernameOverlay = picture.authorUsername,
                        userImageOverlay = picture.authorImageUrl,
                        markerNameOverlay = picture.markerName,
                        timestampOverlay = picture.timeStamp,
                        likeCountOverlay = picture.likes
                    )
                }

            } else {
                _uiState.update { it.copy(showOverlay = false) }
            }
        }
    }

    fun hideOverlay() {
        _uiState.update { it.copy(showOverlay = false) }
    }
}
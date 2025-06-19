package com.dambrofarne.eyeflush.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef
import com.dambrofarne.eyeflush.ui.screens.profileconfig.ProfileConfigUiState
import com.dambrofarne.eyeflush.utils.AchievementRank
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val id : String = "",
    val username: String = "",
    val profileImagePath: String = "",
    val imagesCount: Int = 0,
    val score : Int = 0,
    val picturesTaken: List<PicQuickRef> = emptyList(),
    val isLoading: Boolean = true,
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


class ProfileViewModel(
    private val db : DatabaseRepository,
    private val auth : AuthRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    suspend fun loadUserInfo(uId: String? = null, isInitialLoad: Boolean = true) {
        _uiState.update {
            it.copy(
                isLoading = if (isInitialLoad) true else it.isLoading,
                errorMessage = null
            )
        }

        val requesterUId = auth.getCurrentUserId()
        if (requesterUId == null) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "Utente non autenticato")
            }
            return
        }

        val targetUId = uId ?: requesterUId

        val result = db.getUserExtendedInfo(targetUId, requesterUId)
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
                    locations = user.markersPhotographedLvl ?: AchievementRank.NONE
                )
            }
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Errore sconosciuto"
            _uiState.update {
                it.copy(isLoading = false, errorMessage = errorMsg)
            }
        }
    }

    fun toggleLike(picId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdating = true) }

            val currentUser = auth.getCurrentUserId()
            if (currentUser == null) {
                _uiState.update { it.copy(isUpdating = false, errorMessage = "Utente non autenticato") }
                return@launch
            }

            val result = db.likeImage(uId = currentUser, picId = picId)

            if (result.isSuccess) {
                loadUserInfo(
                    uId = _uiState.value.id,
                    isInitialLoad = false
                )
            } else {
                _uiState.update { it.copy(errorMessage = "Errore nel toggle like") }
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
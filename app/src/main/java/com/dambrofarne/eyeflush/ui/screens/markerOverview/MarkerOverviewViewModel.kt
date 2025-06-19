package com.dambrofarne.eyeflush.ui.screens.markerOverview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

data class MarkerOverviewUiState(
    val id: String = "",
    val name: String? = null,
    val coordinates: GeoPoint = GeoPoint(0.0, 0.0),
    val mostLikedPicId: String? = null,
    val mostLikedPicURL: String? = null,
    val mostLikedPicUserId: String? = null,
    val mostLikedPicUserImage: String? = null,
    val mostLikedPicUsername: String? = null,
    val mostLikedPicTimeStamp : String? = null,
    val mostLikedPicLikes: Int = 0,
    val imagesCount: Int = 0,
    val picturesTaken: List<PicQuickRef> = emptyList(),
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val errorMessage: String? = null,
    val userLikesMostLiked: Boolean = false,

    val showOverlay : Boolean = false,
    val imageUrlOverlay: String = "",
    val uIdvOverlay : String = "",
    val usernameOverlay : String = "",
    val userImageOverlay : String = "",
    val markerNameOverlay : String = "",
    val timestampOverlay: String = "",
    val likeCountOverlay : Int = 0
)


class MarkerOverviewViewModel(
    private val db : DatabaseRepository,
    private val auth :  AuthRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(MarkerOverviewUiState())
    val uiState: StateFlow<MarkerOverviewUiState> = _uiState

    suspend fun loadMarkerInfo(markerId: String, isInitialLoad: Boolean = true) {
        _uiState.update { it.copy(
            isLoading = if (isInitialLoad) true else it.isLoading,
            errorMessage = null
        ) }
        val userId = auth.getCurrentUserId()
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "User not authenticated") }
            return
        }


        val result = db.getMarkerExtendedInfo(markerId, userId)
        if (result.isSuccess) {
            val marker = result.getOrNull()!!
            _uiState.update {
                it.copy(
                    id = marker.id,
                    name = marker.name,
                    coordinates = marker.coordinates,
                    mostLikedPicId = marker.mostLikedPicId,
                    mostLikedPicURL = marker.mostLikedPicURL,
                    mostLikedPicUserId = marker.mostLikedPicUserId,
                    mostLikedPicLikes = marker.mostLikedPicLikes ?: 0,
                    mostLikedPicUserImage = marker.mostLikedPicUserImage,
                    mostLikedPicUsername =  marker.mostLikedPicUsername,
                    mostLikedPicTimeStamp = marker.mostLikedPicTimeStamp,
                    imagesCount = marker.imagesCount,
                    picturesTaken = marker.picturesTaken,
                    isLoading = false
                )
            }
            val userLikesMostLiked = marker.mostLikedPicId?.let{ db.hasUserLiked(userId,it)}

            if (userLikesMostLiked != null) {
                _uiState.update { it.copy(userLikesMostLiked = userLikesMostLiked) }
            }

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

            val result = db.likeImage(currentUser, picId)

            if (result.isSuccess) {
                loadMarkerInfo(
                    markerId = _uiState.value.id,
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
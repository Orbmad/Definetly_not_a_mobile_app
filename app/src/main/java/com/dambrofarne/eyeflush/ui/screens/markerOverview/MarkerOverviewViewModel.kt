package com.dambrofarne.eyeflush.ui.screens.markerOverview

import android.util.Log
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
import java.time.LocalDateTime

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
    val errorMessage: String? = null
)


class MarkerOverviewViewModel(
    private val db : DatabaseRepository,
    private val auth :  AuthRepository
) : ViewModel(){
    private val _uiState = MutableStateFlow(MarkerOverviewUiState())
    val uiState: StateFlow<MarkerOverviewUiState> = _uiState

    suspend fun loadMarkerInfo(markerId: String) {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        val userId = auth.getCurrentUserId()
        if (userId == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "Utente non autenticato") }
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
        } else {
            val errorMsg = result.exceptionOrNull()?.message ?: "Errore sconosciuto"
            _uiState.update { it.copy(isLoading = false, errorMessage = errorMsg) }
        }
    }

    suspend fun toggleLike(picId: String): Boolean {
        Log.w("Like", "Chiamo funzione di toggle")

        val currentUser = auth.getCurrentUserId()
        return if (currentUser != null) {
            val result = db.likeImage(currentUser, picId)
            result.getOrElse {
                Log.e("Like", "Errore nel like", it)
                false
            }
        } else {
            false
        }
    }
}
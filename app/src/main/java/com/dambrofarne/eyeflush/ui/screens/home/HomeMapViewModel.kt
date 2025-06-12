package com.dambrofarne.eyeflush.ui.screens.home

import androidx.lifecycle.ViewModel
import com.dambrofarne.eyeflush.data.repositories.auth.AuthRepository
import com.dambrofarne.eyeflush.data.repositories.database.DatabaseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.osmdroid.util.GeoPoint

class HomeMapViewModel(
    private val db : DatabaseRepository,
    private val auth : AuthRepository
) : ViewModel() {
    private val _polaroidMarkers = MutableStateFlow<List<PolaroidMarker>>(emptyList())
    val polaroidMarkers: StateFlow<List<PolaroidMarker>> = _polaroidMarkers.asStateFlow()

    private val _currentLocation = MutableStateFlow<GeoPoint?>(null)
    val currentLocation: StateFlow<GeoPoint?> = _currentLocation.asStateFlow()

    fun updateCurrentLocation(location: GeoPoint) {
        _currentLocation.value = location
    }


}
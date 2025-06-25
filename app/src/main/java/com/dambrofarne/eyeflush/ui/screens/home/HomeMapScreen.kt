@file:Suppress("DEPRECATION")
package com.dambrofarne.eyeflush.ui.screens.home

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
// import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.R
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.CameraButton
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.XYTileSource
import org.osmdroid.util.GeoPoint
import org.osmdroid.events.MapListener
import org.osmdroid.events.ScrollEvent
import org.osmdroid.events.ZoomEvent
import kotlin.math.*

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeMapScreen(
    navController: NavHostController,
    viewModel: HomeMapViewModel = koinViewModel<HomeMapViewModel>()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Initialize OSMDroid configuration
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )

    val polaroidMarkers by viewModel.polaroidMarkers.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val locationUpdated by viewModel.locationUpdated.collectAsState()

    var mapView: MapView? by remember { mutableStateOf(null) }
    var currentLocationMarker by remember { mutableStateOf<Marker?>(null) }
    var lastLocation by remember { mutableStateOf<GeoPoint?>(null) }
    var userHasMovedMap by remember { mutableStateOf(false) }
    var lastUserInteractionTime by remember { mutableLongStateOf(0L) }

    val polaroidMarkersRefs = remember { mutableListOf<PolaroidMarker>() }

    val defaultZoom = 19.0
    val movementThreshold = 4.0 // meters - move threshold
    val userInteractionTimeout = 15000L // milli-seconds - reposition timeout

    val userLocationIcon = ContextCompat.getDrawable(context, R.drawable.diamond_man_3)

    val openStreetMapTileSource = XYTileSource(
        "OSM",
        0, 25, 256, ".png",
        arrayOf("https://tile.openstreetmap.org/")
    )

    // Two points distance in meters
    fun calculateDistance(point1: GeoPoint, point2: GeoPoint): Double {
        val lat1Rad = Math.toRadians(point1.latitude)
        val lat2Rad = Math.toRadians(point2.latitude)
        val deltaLatRad = Math.toRadians(point2.latitude - point1.latitude)
        val deltaLonRad = Math.toRadians(point2.longitude - point1.longitude)

        val a = sin(deltaLatRad / 2).pow(2) + cos(lat1Rad) * cos(lat2Rad) * sin(deltaLonRad / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return 6371000 * c // Earth radius
    }

    // Requesting permissions
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        } else {
            // Retrieve initial position
            val initialLocation = viewModel.locationManager.getCurrentLocation()
            initialLocation?.let {
                //Log.w("HomeMapScreen", "Initial location acquired")
                viewModel.updateCurrentLocation(it)
                lastLocation = it
                mapView?.controller?.animateTo(it)
                mapView?.invalidate()
            }

            // Get current location
            viewModel.locationManager.startLocationUpdates { geoPoint ->
                viewModel.updateCurrentLocation(geoPoint)
                mapView?.invalidate()
                //Log.w("HomeMapScreen", "Routine location update")
            }
        }
    }

    LaunchedEffect(mapView, currentLocation, locationUpdated) {
        val location = currentLocation
        val map = mapView

        if (map != null && location != null) {
            // Update or create current position marker
            if (currentLocationMarker == null) {
                currentLocationMarker = Marker(map).apply {
                    icon = userLocationIcon
                    position = location
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "Your location"
                }
                map.overlays.add(currentLocationMarker)
            } else {
                currentLocationMarker?.position = location
            }

            // check movement
            val hasUserMoved = lastLocation?.let { lastLoc ->
                calculateDistance(lastLoc, location) > movementThreshold
            } ?: true

            val currentTime = System.currentTimeMillis()
            val timeSinceLastInteraction = currentTime - lastUserInteractionTime

            // Reposition if:
            // 1. User changed location
            // 2. Interaction timeout expire
            if (hasUserMoved || (!userHasMovedMap && timeSinceLastInteraction > userInteractionTimeout)) {
                map.controller.animateTo(location)
                userHasMovedMap = false // Reset interaction flag
                //Log.w("HomeMapScreen", "Map centered - User moved: $hasUserMoved, Time since interaction: $timeSinceLastInteraction")
            }

            lastLocation = location
            map.invalidate()
        }
    }

    // Update markers when photoMarkers change
    LaunchedEffect(polaroidMarkers) {
        try {
            // Remove old markers
            polaroidMarkersRefs.forEach { marker ->
                mapView?.overlays?.remove(marker)
            }
            polaroidMarkersRefs.clear()

            // Add new markers
            polaroidMarkers.forEach { marker ->
                marker.setMarkerClickedAction {
                    navController.navigate(
                        EyeFlushRoute.MarkerOverview(
                            marker.getID()
                        )
                    )
                }
                polaroidMarkersRefs.add(marker)
                mapView?.overlays?.add(marker)
            }

            mapView?.invalidate()
        } catch (e: Exception) {
            Log.e("HomeMapScreen", "Error updating photo markers", e)
        }
    }

    // HomeScreen View
    CustomScaffold(
        showBackButton = false,
        navController = navController,
        currentScreen = NavScreen.HOME,
        content = {
            Box(modifier = Modifier.fillMaxSize()) {

                AndroidView(
                    factory = { ctx ->
                        MapView(ctx).apply {
                            setTileSource(openStreetMapTileSource)
                            setMultiTouchControls(true)
                            setBuiltInZoomControls(false)
                            controller.setZoom(defaultZoom)
                            controller.setCenter(currentLocation)
                            mapView = this

                            // Add listener for user interactions
                            addMapListener(object : MapListener {
                                override fun onScroll(event: ScrollEvent?): Boolean {
                                    userHasMovedMap = true
                                    lastUserInteractionTime = System.currentTimeMillis()
                                    //Log.d("HomeMapScreen", "User scrolled map")
                                    return false
                                }

                                override fun onZoom(event: ZoomEvent?): Boolean {
                                    userHasMovedMap = true
                                    lastUserInteractionTime = System.currentTimeMillis()
                                    //Log.d("HomeMapScreen", "User zoomed map")
                                    return false
                                }
                            })
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                ) { map ->
                    mapView = map
                }

//                Text(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(16.dp)
//                        .align(Alignment.TopCenter)
//                        .zIndex(1f),
//                    text = currentLocation?.let {
//                        "Lat: ${it.latitude}\nLong: ${it.longitude}"
//                    } ?: "Position unavailable"
//                )

                // Reposition Button
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            val location = viewModel.locationManager.getCurrentLocation()
                            location?.let {
                                viewModel.updateCurrentLocation(it)
                                mapView?.controller?.animateTo(it)
                                mapView?.controller?.setZoom(defaultZoom)
                                userHasMovedMap = false // Reset interaction map
                                lastUserInteractionTime = System.currentTimeMillis()
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier
                        .size(86.dp)
                        .align(Alignment.BottomCenter)
                        .offset(x = (-72).dp)
                        .padding(16.dp),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Center to current position",
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Camera Button
                CameraButton(
                    onClick = {navController.navigate(EyeFlushRoute.Camera)},
                    modifier = Modifier
                        .size(124.dp)
                        .align(Alignment.BottomCenter)
                        .padding(16.dp)
                )
            }
        }
    )
}
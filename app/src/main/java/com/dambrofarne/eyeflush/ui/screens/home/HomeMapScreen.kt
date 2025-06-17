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
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.R
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.data.managers.location.LocationManagerImpl
import com.dambrofarne.eyeflush.ui.composables.CameraButton
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.CustomTopBar
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import com.dambrofarne.eyeflush.ui.composables.ProfileIcon
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.XYTileSource

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

    val polaroidMarkersRefs = remember { mutableListOf<PolaroidMarker>() }

    val defaultZoom = 20.0

    val userLocationIcon = ContextCompat.getDrawable(context, R.drawable.diamond_man_3)

    val openStreetMapTileSource = XYTileSource(
        "OSM",
        0, 25, 256, ".png",
        arrayOf("https://tile.openstreetmap.org/")
    )

    // Requesting permissions
    LaunchedEffect(Unit) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        } else {
            // ✅ 1. Recupera subito la posizione iniziale
            val initialLocation = viewModel.locationManager.getCurrentLocation()
            initialLocation?.let {
                Log.w("HomeMapScreen", "Initial location acquired")
                viewModel.updateCurrentLocation(it)
                //mapView?.controller?.animateTo(it)
            }

            // Get current location
            viewModel.locationManager.startLocationUpdates { geoPoint ->
                viewModel.updateCurrentLocation(geoPoint)
                //viewModel.loadPolaroidMarkers()
                //mapView?.controller?.animateTo(geoPoint)
                mapView?.invalidate()

                Log.w("HomeMapScreen", "Routine location update")
            }
        }
    }

    LaunchedEffect(mapView, currentLocation, locationUpdated) {
        val location = currentLocation
        val map = mapView

        if (map != null && location != null) {
            if (currentLocationMarker == null) {
                // Crea nuovo marker se non esiste
                currentLocationMarker = Marker(map).apply {
                    icon = userLocationIcon
                    position = location
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "La tua posizione"
                }
                map.overlays.add(currentLocationMarker)
            } else {
                // Aggiorna posizione del marker esistente
                currentLocationMarker?.position = location
            }

            map.controller.animateTo(location)
            map.invalidate()
        }
    }

    // Update markers when photoMarkers change
    LaunchedEffect(polaroidMarkers) {
        //Log.w("Test", "Updating photo markers, count: ${photoMarkers.size}")

        try {
            // Rimuovi i vecchi marker delle foto
            polaroidMarkersRefs.forEach { marker ->
                mapView?.overlays?.remove(marker)
            }
            polaroidMarkersRefs.clear()

            //viewModel.createDummyMarkers()

            // Aggiungi i nuovi marker delle foto
            polaroidMarkers.forEach { marker ->
                //Log.w("Test", "Creating photo marker at ${photoMarker.position}")
                val polaroidMarker = marker
                polaroidMarker.setMarkerClickedAction { navController.navigate(EyeFlushRoute.MarkerOverview(polaroidMarker.getID())) }
                polaroidMarkersRefs.add(polaroidMarker)
                mapView?.overlays?.add(polaroidMarker)
                //Log.w("Test", "Photo marker added successfully")
            }

            mapView?.invalidate()
            //Log.w("Test", "Map invalidated, total overlays: ${mapView?.overlays?.size}")

        } catch (e: Exception) {
            //Log.e("Test", "Error updating photo markers", e)
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
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    ) { map ->
                        mapView = map
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter)
                            .zIndex(1f),
                        text = currentLocation?.let {
                            "Lat: ${it.latitude}\nLong: ${it.longitude}"
                        } ?: "Posizione non disponibile"
                    )

                    // Reposition Button
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                val location = viewModel.locationManager.getCurrentLocation()
                                location?.let {
                                    viewModel.updateCurrentLocation(it)
                                    mapView?.controller?.animateTo(it)
                                    mapView?.controller?.setZoom(defaultZoom)
                                }
                            }
                        },
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier
                            .size(82.dp)
                            .align(Alignment.BottomCenter)
                            .offset(x = (-70).dp)
                            .padding(16.dp),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "Center to current position"
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


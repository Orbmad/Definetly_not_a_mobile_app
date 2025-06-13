package com.dambrofarne.eyeflush.ui.screens.home

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
import com.dambrofarne.eyeflush.ui.composables.CustomTopBar
import com.dambrofarne.eyeflush.ui.composables.ProfileIcon
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.tileprovider.tilesource.XYTileSource
//import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase
//import org.osmdroid.util.MapTileIndex
//import org.osmdroid.tileprovider.tilesource.TileSourceFactory
//import android.util.Log

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HomeMapScreen(
    navController: NavHostController,
    viewModel: HomeMapViewModel = koinViewModel<HomeMapViewModel>()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationManager = remember { LocationManagerImpl(context) }

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

    var showCamera by remember { mutableStateOf(false) }
    var mapView: MapView? by remember { mutableStateOf(null) }
    var currentLocationMarker by remember { mutableStateOf<Marker?>(null) }

    val polaroidMarkersRefs = remember { mutableListOf<PolaroidMarker>() }

    val defaultZoom = 20.0

    val userLocationIcon = ContextCompat.getDrawable(context, R.drawable.ic_user_location)

    val openStreetMapTileSource = XYTileSource(
        "OSM",
        0, 25, 256, ".png",
        arrayOf("https://tile.openstreetmap.org/")
    )

//    val cartoLight = object : OnlineTileSourceBase(
//        "CartoLight",
//        0, 28, 256, ".png",
//        arrayOf("https://a.basemaps.cartocdn.com/light_all/")
//    ) {
//        override fun getTileURLString(pMapTileIndex: Long): String {
//            return baseUrl +
//                    MapTileIndex.getZoom(pMapTileIndex) + "/" +
//                    MapTileIndex.getX(pMapTileIndex) + "/" +
//                    MapTileIndex.getY(pMapTileIndex) + ".png"
//        }
//    }

//    val cartoDark = object : OnlineTileSourceBase(
//        "CartoDark",
//        0, 28, 256, ".png",
//        arrayOf("https://a.basemaps.cartocdn.com/dark_all/")
//    ) {
//        override fun getTileURLString(pMapTileIndex: Long): String {
//            return baseUrl +
//                    MapTileIndex.getZoom(pMapTileIndex) + "/" +
//                    MapTileIndex.getX(pMapTileIndex) + "/" +
//                    MapTileIndex.getY(pMapTileIndex) + ".png"
//        }
//    }

    // Requesting permissions
    LaunchedEffect(permissionsState.allPermissionsGranted) {
        if (!permissionsState.allPermissionsGranted) {
            permissionsState.launchMultiplePermissionRequest()
        } else {
            // Get current location
            locationManager.getCurrentLocation()?.let { location ->
                viewModel.updateCurrentLocation(location)
                mapView?.controller?.setCenter(location)
                mapView?.controller?.setZoom(defaultZoom)
            }
        }
    }

    // Starting Location Listener
    LaunchedEffect(Unit) {
        if (permissionsState.allPermissionsGranted) {
            locationManager.startLocationUpdates { geoPoint ->
                viewModel.updateCurrentLocation(geoPoint)
                viewModel.loadPolaroidMarkers()
            }
        }
    }

    // Re-initialize currentLocationMarker when mapView changes
    LaunchedEffect(mapView) {
        if (mapView != null && currentLocation != null) {
            currentLocationMarker = Marker(mapView).apply {
                icon = userLocationIcon
                position = currentLocation!!
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "La tua posizione"
            }
            mapView?.overlays?.add(currentLocationMarker)
            mapView?.controller?.setCenter(currentLocation)
            mapView?.invalidate()
        }
    }

    // Update current location marker
    LaunchedEffect(currentLocation) {
        //Log.w("Test", "LaunchedEffect(currentLocation) triggered")
        currentLocation?.let { location ->
            if (currentLocationMarker == null) {
                // Create marker once
                currentLocationMarker = Marker(mapView).apply {
                    icon = userLocationIcon
                    position = location
                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    title = "La tua posizione"
                }
                mapView?.overlays?.add(currentLocationMarker)
                //Log.w("Test", "Current location marker created")
            } else {
                // Update existing marker position
                currentLocationMarker?.position = location
                //Log.w("Test", "Current location marker updated")
            }

            viewModel.loadPolaroidMarkers()
            mapView?.controller?.animateTo(location)
            mapView?.invalidate()
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
            polaroidMarkers.forEach { photoMarker ->
                //Log.w("Test", "Creating photo marker at ${photoMarker.position}")

                polaroidMarkersRefs.add(photoMarker)
                mapView?.overlays?.add(photoMarker)
                //Log.w("Test", "Photo marker added successfully")
            }

            mapView?.invalidate()
            //Log.w("Test", "Map invalidated, total overlays: ${mapView?.overlays?.size}")

        } catch (e: Exception) {
            //Log.e("Test", "Error updating photo markers", e)
        }
    }

    // HomeScreen View
        Scaffold(
            topBar = {
//                TopAppBar(
//                    title = { "EyeFlush" },
//                    navigationIcon = {
//                        IconButton(
//                            onClick = { navController.navigate(EyeFlushRoute.Profile) }
//                        ) {
//                            IconImage(
//                                image = ACCOUNT_ICON,
//                                modifier = Modifier
//                            )
//                        }
//                    },
//                    colors = TopAppBarDefaults.topAppBarColors(
//                        containerColor = MaterialTheme.colorScheme.secondaryContainer
//                    )
//                )
                CustomTopBar(
                    title = "EyeFlush",
//                    navigationIcon = {
//                        BackButton(
//                            onClick = {navController.navigate(EyeFlushRoute.Home)}
//                        )
//                    },
                    actions = {
                        ProfileIcon(
                            onClick = { navController.navigate(EyeFlushRoute.Profile) }
                        )
                    }
                )
            },
            content = { innerPadding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)) {

                    AndroidView(
                        factory = { ctx ->
                            MapView(ctx).apply {
                                setTileSource(openStreetMapTileSource)
                                setMultiTouchControls(true)
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
                                val location = locationManager.getCurrentLocation()
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


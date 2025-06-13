package com.dambrofarne.eyeflush.ui.screens.camera

import com.dambrofarne.eyeflush.data.managers.camera.CameraManager
import android.Manifest
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.data.managers.camera.CameraState
import com.dambrofarne.eyeflush.data.managers.location.LocationManagerImpl
import com.dambrofarne.eyeflush.ui.EyeFlushRoute
import com.dambrofarne.eyeflush.ui.composables.BackButton
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CameraScreen(
    navController: NavHostController,
    viewModel: CameraViewModel = koinViewModel<CameraViewModel>()
) {
    val context = LocalContext.current
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)
    val locationManager = LocationManagerImpl(context)

    // Cleanup when leaving the screen
    DisposableEffect(Unit) {
        onDispose {
            cameraManager.cleanup()
        }
    }

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    LaunchedEffect(cameraPermissionState.status) {
        if (cameraPermissionState.status.isGranted) {
            cameraManager.location = locationManager.getCurrentLocation()
        } else {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    Scaffold (
        content = { innerPadding ->
            Box (
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when {
                    cameraPermissionState.status.isGranted -> {
                        CameraContent(
                            cameraManager = cameraManager,
                            onNavigateBack = { navController.navigate(EyeFlushRoute.Home) },
                        )
                    }

                    else -> {
                        PermissionDeniedContent(onNavigateBack = {
                            navController.navigate(
                                EyeFlushRoute.Home
                            )
                        })
                    }
                }
            }
        }
    )
}

@Composable
private fun CameraContent(
    cameraManager: CameraManager,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current

    val cameraState by cameraManager.cameraState.collectAsState()
    val capturedImage by cameraManager.capturedImage.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Camera Preview
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val executor = ContextCompat.getMainExecutor(ctx)
                cameraManager.initializeCamera(previewView, lifecycleOwner, executor)
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // Overlay for borders 4:5
        CameraOverlay()

        // Back button
        BackButton(
            onClick = onNavigateBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer,
                    CircleShape
                )
        )

        // Capture button
        CaptureButton(
            cameraState = cameraState,
            onCapture = { cameraManager.capturePhoto() },
            modifier = Modifier
                .size(124.dp)
                .align(Alignment.BottomCenter)
                .padding(16.dp)
        )
    }

    // Confirm dialog
    if (cameraState == CameraState.PHOTO_CAPTURED && capturedImage != null) {
        PhotoConfirmationDialog(
            onConfirm = {
                cameraManager.confirmPhoto()?.let { file ->
                    cameraManager.savePhoto(file)
                    onNavigateBack()
                }
            },
            onRetry = {
                cameraManager.retryPhoto()
            },
            onDismiss = {
                cameraManager.retryPhoto()
            }
        )
    }
}

@Composable
private fun CaptureButton(
    cameraState: CameraState,
    onCapture: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEnabled = cameraState == CameraState.READY
    val isCapturing = cameraState == CameraState.CAPTURING

    FloatingActionButton(
        onClick = { if (isEnabled) onCapture() },
        modifier = modifier.size(124.dp),
        containerColor = when {
            isCapturing -> Color.Gray
            isEnabled -> MaterialTheme.colorScheme.primary
            else -> Color.Gray
        },
        shape = CircleShape
    ) {
        if (isCapturing) {
            CircularProgressIndicator(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            Icon(
                Icons.Default.Camera,
                contentDescription = "Scatta foto",
                modifier = Modifier.size(74.dp),
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
private fun CameraOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val overlayColor = Color.Black.copy(alpha = 0.85f)

        // Calcola le dimensioni del rettangolo 4:5
        val aspectRatio = 4f / 5f
        val rectWidth = canvasWidth * 0.8f
        val rectHeight = rectWidth / aspectRatio

        // Centra il rettangolo
        val left = (canvasWidth - rectWidth) / 2
        val top = (canvasHeight - rectHeight) / 2

        // Disegna l'overlay scuro
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, 0f),
            size = Size(canvasWidth, top)
        )
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, top + rectHeight),
            size = Size(canvasWidth, canvasHeight - (top + rectHeight))
        )
        drawRect(
            color = overlayColor,
            topLeft = Offset(0f, top),
            size = Size(left, rectHeight)
        )
        drawRect(
            color = overlayColor,
            topLeft = Offset(left + rectWidth, top),
            size = Size(canvasWidth - (left + rectWidth), rectHeight)
        )

        // Draw borders
        drawRect(
            color = Color.White,
            topLeft = Offset(left, top),
            size = Size(rectWidth, rectHeight),
            style = Stroke(width = 4.dp.toPx())
        )

        // Draws anngles
        val cornerLength = 20.dp.toPx()
        val cornerWidth = 4.dp.toPx()

        drawLine(
            color = Color.White,
            start = Offset(left, top),
            end = Offset(left + cornerLength, top),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(left, top),
            end = Offset(left, top + cornerLength),
            strokeWidth = cornerWidth
        )

        drawLine(
            color = Color.White,
            start = Offset(left + rectWidth, top),
            end = Offset(left + rectWidth - cornerLength, top),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(left + rectWidth, top),
            end = Offset(left + rectWidth, top + cornerLength),
            strokeWidth = cornerWidth
        )

        drawLine(
            color = Color.White,
            start = Offset(left, top + rectHeight),
            end = Offset(left + cornerLength, top + rectHeight),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(left, top + rectHeight),
            end = Offset(left, top + rectHeight - cornerLength),
            strokeWidth = cornerWidth
        )

        drawLine(
            color = Color.White,
            start = Offset(left + rectWidth, top + rectHeight),
            end = Offset(left + rectWidth - cornerLength, top + rectHeight),
            strokeWidth = cornerWidth
        )
        drawLine(
            color = Color.White,
            start = Offset(left + rectWidth, top + rectHeight),
            end = Offset(left + rectWidth, top + rectHeight - cornerLength),
            strokeWidth = cornerWidth
        )
    }
}

@Composable
private fun PhotoConfirmationDialog(
    onConfirm: () -> Unit,
    onRetry: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Upload Photo")
        },
        text = {
            Text("Do you want to upload the photo?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ok")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onRetry) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Retry")
            }
        }
    )
}

@Composable
private fun PermissionDeniedContent(onNavigateBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Camera permission requested",
            style = MaterialTheme.typography.headlineSmall
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Camera permission is necessary to use this function.",
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onNavigateBack) {
            Text("Back")
        }
    }
}
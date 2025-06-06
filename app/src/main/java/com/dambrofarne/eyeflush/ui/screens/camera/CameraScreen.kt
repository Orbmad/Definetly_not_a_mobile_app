package com.dambrofarne.eyeflush.ui.screens.camera

import android.net.Uri
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.dambrofarne.eyeflush.data.managers.camera.CameraManager
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    onPhotoTaken: (Uri) -> Unit,
    onClose: () -> Unit,
    cameraManager: CameraManager,
    lifecycleOwner: LifecycleOwner
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    var previewView: PreviewView? by remember { mutableStateOf(null) }

    LaunchedEffect(previewView) {
        previewView?.let { preview ->
            cameraManager.initializeCamera(preview, lifecycleOwner, cameraExecutor)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).also { previewView = it }
            },
            modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            FloatingActionButton(
                onClick = onClose,
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Icon(Icons.Default.Close, contentDescription = "Chiudi")
            }

            FloatingActionButton(
                onClick = {
                    scope.launch {
                        cameraManager.capturePhoto()?.let { uri ->
                            onPhotoTaken(uri)
                        }
                    }
                },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    Icons.Default.Camera,
                    contentDescription = "Scatta foto",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}
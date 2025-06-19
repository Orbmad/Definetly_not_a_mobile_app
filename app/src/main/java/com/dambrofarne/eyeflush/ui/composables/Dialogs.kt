package com.dambrofarne.eyeflush.ui.composables

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ImagePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Uri?) -> Unit
) {
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        onConfirm(uri)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Choose a Photo") },
            text = { Text("You’ll be asked to pick an image from your gallery to upload to Imgur. It " +
                    " will become your profile image and could be seen by everyone") },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch("image/*")
                    onDismiss()
                }) {
                    Text("Continue")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Dismiss")
                }
            }
        )
    }
}
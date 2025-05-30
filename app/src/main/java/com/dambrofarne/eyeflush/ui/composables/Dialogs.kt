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
            title = { Text("Seleziona immagine") },
            text = { Text("L'app ti chiederà di scegliere una foto da caricare su Imgur.") },
            confirmButton = {
                TextButton(onClick = {
                    launcher.launch("image/*")
                    onDismiss()
                }) {
                    Text("Continua")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Annulla")
                }
            }
        )
    }
}
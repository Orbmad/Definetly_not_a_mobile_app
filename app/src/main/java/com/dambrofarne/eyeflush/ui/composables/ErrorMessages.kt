package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuthenticationError(text: String){
    Spacer(Modifier.height(8.dp))
    Text(text, color = Color.Red, fontSize = MaterialTheme.typography.bodySmall.fontSize)
}
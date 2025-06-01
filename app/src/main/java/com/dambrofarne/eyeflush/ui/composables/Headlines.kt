package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun StandardHeadline(text : String){
    Text(text, style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun StandardText(text: String){
    Text(text, style = MaterialTheme.typography.headlineSmall)
}
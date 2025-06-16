package com.dambrofarne.eyeflush.ui.composables

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun StandardHeadline(text : String){
    Text(text, style = MaterialTheme.typography.headlineMedium)
}

@Composable
fun StandardText(text: String){
    Text(text, style = MaterialTheme.typography.headlineSmall)
}

@Composable
fun PageTitle(text: String) {
    val colors = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(colors.primary.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = text,
            style = typography.headlineMedium,
            color = colors.primary
        )
    }
}

@Composable
fun ImageLabel(text: String){
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge
    )
}
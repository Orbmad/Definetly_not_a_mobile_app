package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.dambrofarne.eyeflush.R

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
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
fun UserProfileRow(
    userId: String?,
    username: String?,
    userImageUrl: String?,
    onWhite: Boolean = false,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable {
                userId?.let { onUserClick(it) }
            }
            .padding(4.dp)
    ) {
        val painter = if (userImageUrl != null) {
            rememberAsyncImagePainter(
                model = userImageUrl,
                placeholder = painterResource(id = R.drawable.user_image_placeholder),
                error = painterResource(id = R.drawable.user_image_placeholder)
            )
        } else {
            painterResource(id = R.drawable.user_image_placeholder)
        }

        Image(
            painter = painter,
            contentDescription = "User Profile",
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(50))
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(50),
                    clip = false
                )
        )
            Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = username ?: "Unknown",
            style = MaterialTheme.typography.titleMedium,
            color = if (onWhite) Color.Black else MaterialTheme.colorScheme.onBackground
        )
    }
}
package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dambrofarne.eyeflush.R
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef

@Composable
fun ProfileImage(
    url: String?,
    size: Dp = 150.dp,
    borderSize: Dp = 2.dp,
    borderShape: Shape = CircleShape,
    borderColor: Color = Color.Gray
) {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(url)
        .crossfade(true)
        .placeholder(R.drawable.user_image_placeholder)
        .error(R.drawable.user_image_placeholder)
        .build()

    AsyncImage(
        model = request,
        contentDescription = "Profile image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .shadow(elevation = 10.dp, shape = borderShape, clip = false)
            .clip(borderShape)
            .border(borderSize, borderColor, borderShape)
    )
}

@Composable
fun ChoiceProfileImage(
    url: String?,
    borderSize: Dp = 2.dp,
    borderShape: Shape = CircleShape,
    borderColor: Color = MaterialTheme.colorScheme.primary
) {
    ProfileImage(
        url = url,
        size =  200.dp,
        borderSize = borderSize,
        borderShape = borderShape,
        borderColor = borderColor
    )
}


@Composable
fun ImageCard(
    picture: PicQuickRef,
    onClick: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    var liked by remember(picture.picId, picture.liked) { mutableStateOf(picture.liked) }
    var likeCount by remember(picture.picId, picture.likes) { mutableIntStateOf(picture.likes) }

    Card(
        onClick = { if (enabled) onClick(picture.picId) },
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(0.8f)
            .sizeIn(maxWidth = 120.dp, maxHeight = 150.dp)
            .shadow(elevation = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RectangleShape,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = picture.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(enabled = enabled) {
                        liked = !liked
                        likeCount += if (liked) 1 else -1
                        onToggleLike(picture.picId)
                    }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (liked) MaterialTheme.colorScheme.primary else Color.White,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "$likeCount",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun ImageCardSimplified(
    picture: PicQuickRef,
    onClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Card(
        onClick = { if (enabled) onClick(picture.picId) },
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(0.8f)
            .sizeIn(maxWidth = 120.dp, maxHeight = 150.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RectangleShape
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = picture.url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun ImageGrid(
    pictures: List<PicQuickRef>,
    onImageClick: (String) -> Unit,
    onToggleLike: (String) -> Unit,
    enabled : Boolean = true
) {
    if(pictures.isNotEmpty()){
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 120.dp),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(pictures) { picture ->
                ImageCard(
                    picture = picture,
                    onClick = onImageClick,
                    onToggleLike = onToggleLike,
                    enabled = enabled
                )
            }
        }
    }else{
        Text(
            text = "No pictures published yet...",
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun PolaroidOverlayCard(
    imageUrl: String,
    uId : String,
    username: String,
    userImage : String,
    timestamp: String,
    onUserClick : (String) -> Unit,
    likeCount: Int,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss?.invoke() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .padding(24.dp)
                .widthIn(max = 360.dp)
        ) {
            Column(
                modifier = Modifier.padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Polaroid Image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .border(
                            width = 12.dp,
                            color = Color.White,
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        UserProfileRow(
                            userId = uId,
                            username = username,
                            userImageUrl = userImage,
                            onWhite = true,
                            onUserClick = onUserClick
                        )
                        Text(
                            text = timestamp,
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Likes",
                            tint = Color.Red,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$likeCount",
                            color = Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}


package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.dambrofarne.eyeflush.data.repositories.database.PicQuickRef
import kotlinx.coroutines.launch

@Composable
fun ProfileImage(
    url: String,
    size: Dp = 150.dp,
    borderSize: Dp = 2.dp,
    borderShape: Shape = CircleShape,
    borderColor: Color = Color.Gray
) {
    val context = LocalContext.current
    val request = ImageRequest.Builder(context)
        .data(url)
        .crossfade(true)
        //.placeholder(R.drawable.ic_placeholder)
        //.error(R.drawable.ic_error)
        .build()

    AsyncImage(
        model = request,
        contentDescription = "Profile image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .clip(borderShape)
            .border(borderSize, borderColor, borderShape)
    )
}

@Composable
fun IconImage(
    image: Any,  // può essere String o Int
    modifier: Modifier = Modifier,
    size: Dp = 50.dp,
    borderSize: Dp = 1.dp,
    borderShape: Shape = RoundedCornerShape(8.dp),
    borderColor: Color = Color.Gray
) {
    val context = LocalContext.current
    val request = when (image) {
        is String -> ImageRequest.Builder(context)
            .data(image)
            .crossfade(true)
            .build()
        is Int -> ImageRequest.Builder(context)
            .data(image)
            .build()
        else -> throw IllegalArgumentException("Unsupported image type")
    }

    AsyncImage(
        model = request,
        contentDescription = "Icon image",
        contentScale = ContentScale.Fit,
        modifier = modifier
            .size(size)
            .clip(borderShape)
            .border(borderSize, borderColor, borderShape)
    )
}



@Composable
fun ChoicheProfileImage(
    url: String,
    borderSize: Dp = 2.dp,
    borderShape: Shape = CircleShape,
    borderColor: Color = Color.Gray
) {
    ProfileImage(
        url = url,
        size =  240.dp,
        borderSize = borderSize,
        borderShape = borderShape,
        borderColor = borderColor
    )
}

@Composable
fun ChoicheSmallProfileImage(
    url: String,
    borderSize: Dp = 2.dp,
    borderShape: Shape = CircleShape,
    borderColor: Color = Color.Gray
) {
    ProfileImage(
        url = url,
        size = 50.dp,
        borderSize = borderSize,
        borderShape = borderShape,
        borderColor = borderColor
    )
}

@Composable
fun ImageCard(
    picture: PicQuickRef,
    onClick: (String) -> Unit,
    onToggleLike: suspend (String) -> Boolean,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    // Stato locale per like e counter
    var liked by remember { mutableStateOf(picture.liked) }
    var likeCount by remember { mutableStateOf(picture.likes) }

    Card(
        onClick = { onClick(picture.picId) },
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

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable {
                        // Optimistic update
                        liked = !liked
                        likeCount += if (liked) 1 else -1

                        // Backend sync
                        scope.launch {
                            try {
                                val newLiked = onToggleLike(picture.picId)
                                if (newLiked != liked) {
                                    liked = newLiked
                                    likeCount += if (newLiked) 1 else -1
                                }
                            } catch (e: Exception) {
                                liked = !liked
                                likeCount += if (liked) 1 else -1
                            }
                        }
                    }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (liked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    tint = if (liked) Color.Red else Color.White,
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
fun ImageGrid(
    pictures: List<PicQuickRef>,
    onImageClick: (String) -> Unit,
    onToggleLike: suspend (String) -> Boolean
) {
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
                onToggleLike = onToggleLike
            )
        }
    }
}

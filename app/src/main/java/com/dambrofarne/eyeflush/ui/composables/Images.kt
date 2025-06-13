package com.dambrofarne.eyeflush.ui.composables

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest

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
fun ImageCard(url: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(0.8f), //Formato polaroid 4:5
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = url,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ImageGrid(urls: List<String>) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 120.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(urls) { url ->
            ImageCard(url = url)
        }
    }
}

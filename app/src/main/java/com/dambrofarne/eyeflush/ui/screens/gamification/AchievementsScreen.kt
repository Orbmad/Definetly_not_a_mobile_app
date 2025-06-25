package com.dambrofarne.eyeflush.ui.screens.gamification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cyclone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.dambrofarne.eyeflush.ui.composables.CustomScaffold
import com.dambrofarne.eyeflush.ui.composables.NavScreen
import com.dambrofarne.eyeflush.utils.AchievementType
import com.dambrofarne.eyeflush.utils.calcAchievementRank
import com.dambrofarne.eyeflush.utils.getAchievementIconId
import com.dambrofarne.eyeflush.utils.getNextAchievementMaxPoints
import com.dambrofarne.eyeflush.utils.getNextRank
import com.dambrofarne.eyeflush.utils.mapRankToLevel
import org.koin.androidx.compose.koinViewModel

@Composable
fun GamificationScreen(
    navController: NavHostController,
    viewModel: GamificationViewModel = koinViewModel<GamificationViewModel>()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        //viewModel.loadDummyUserAchievements()
        viewModel.loadUserAchievements()
    }

    CustomScaffold(
        title = "Achievements",
        showBackButton = false,
        navController = navController,
        currentScreen = NavScreen.GAME,
        newNotification = viewModel.checkNotifications(),
        content = {
            when {
                uiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                else -> {
                    AchievementScreen(uiState)
                }
            }
        }
    )

}

@Composable
fun AchievementScreen(achievementUiState: AchievementUiState) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        //Score
        Row(
            modifier = Modifier
                .padding(start = 10.dp, top=8.dp, bottom = 6.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {

            Icon(
                imageVector = Icons.Default.Cyclone,
                contentDescription = "Achievements",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )

            Text(
                text = achievementUiState.score.toString() + " FlushPoints",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(start = 6.dp)
                    .align(Alignment.CenterVertically)
            )
        }

        // Achievement photo taken
        val photoTakenRank = calcAchievementRank(AchievementType.PHOTO_TAKEN, achievementUiState.picturesTaken)
        AchievementItem(
            title = "Photo Taken - LV ${mapRankToLevel(photoTakenRank)}",
            maxPoints = getNextAchievementMaxPoints(AchievementType.PHOTO_TAKEN, achievementUiState.picturesTaken),
            actualPoints = achievementUiState.picturesTaken,
            iconId = getAchievementIconId(AchievementType.PHOTO_TAKEN, getNextRank(photoTakenRank))!!
        )

        // Achievement likes
        val likesRank = calcAchievementRank(AchievementType.LIKES, achievementUiState.likesReceived)
        AchievementItem(
            title = "Likes received - LV ${mapRankToLevel(likesRank)}",
            maxPoints = getNextAchievementMaxPoints(AchievementType.LIKES, achievementUiState.likesReceived),
            actualPoints = achievementUiState.likesReceived,
            iconId = getAchievementIconId(AchievementType.LIKES, getNextRank(likesRank))!!
        )

        // Achievement first place
        val firstPlaceRank = calcAchievementRank(AchievementType.FIRST_PLACE, achievementUiState.mostLikedPictures)
        AchievementItem(
            title = "Most liked photos - LV ${mapRankToLevel(firstPlaceRank)}",
            maxPoints = getNextAchievementMaxPoints(AchievementType.FIRST_PLACE, achievementUiState.mostLikedPictures),
            actualPoints = achievementUiState.mostLikedPictures,
            iconId = getAchievementIconId(AchievementType.FIRST_PLACE, getNextRank(firstPlaceRank))!!
        )

        // Achievement location visited
        val locationRank = calcAchievementRank(AchievementType.LOCATION_VISITED, achievementUiState.markersVisited)
        AchievementItem(
            title = "Most liked photos - LV ${mapRankToLevel(locationRank)}",
            maxPoints = getNextAchievementMaxPoints(AchievementType.LOCATION_VISITED, achievementUiState.markersVisited),
            actualPoints = achievementUiState.markersVisited,
            iconId = getAchievementIconId(AchievementType.LOCATION_VISITED, getNextRank(locationRank))!!
        )
    }
}

@Composable
fun AchievementItem(title: String, maxPoints: Int, actualPoints: Int, iconId: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        //verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            //modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {

                CustomAchievementProgressBarWithText(
                    current = actualPoints,
                    goal = maxPoints
                )
            }

            Image(
                painter = painterResource(iconId),
                contentDescription = "badge",
                modifier = Modifier
                    .size(82.dp)
                    .padding(start = 24.dp)
            )
        }
    }

}

@Composable
fun CustomAchievementProgressBarWithText(
    current: Int,
    goal: Int,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.LightGray,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    height: Dp = 32.dp,
    cornerRadius: Dp = 6.dp,
    textColor: Color = Color.Black
) {
    val progress = current.coerceAtMost(goal).toFloat() / goal

    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(width = 1.dp, MaterialTheme.colorScheme.onPrimaryContainer, RoundedCornerShape(cornerRadius))
    ) {
        // Progress bar
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(progress)
                .clip(RoundedCornerShape(cornerRadius))
                .background(progressColor)
        )

        // Progress ratio
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Text(
                text = "$current / $goal",
                color = textColor,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
        }
    }
}

package com.dambrofarne.eyeflush.utils

import com.dambrofarne.eyeflush.R

enum class AchievementType {
    LIKES,
    PHOTO_TAKEN,
    FIRST_PLACE,
    LOCATION_VISITED
}

enum class AchievementRank {
    NONE,
    BRONZE,
    SILVER,
    GOLD
}

data class AchievementCheckpoints(
    val achievementName: String,
    val achievementType: AchievementType,
    val bronzeCheckpoint: Int,
    val silverCheckpoint: Int,
    val goldCheckpoint: Int
)

val likesAchievement = AchievementCheckpoints(
    achievementName = "Likes received",
    achievementType = AchievementType.LIKES,
    bronzeCheckpoint = 5,
    silverCheckpoint = 20,
    goldCheckpoint = 50
)

val photoTakenAchievement = AchievementCheckpoints(
    achievementName = "Photo taken",
    achievementType = AchievementType.PHOTO_TAKEN,
    bronzeCheckpoint = 3,
    silverCheckpoint = 10,
    goldCheckpoint = 25
)

val firstPlaceAchievement = AchievementCheckpoints(
    achievementName = "First place reached",
    achievementType = AchievementType.FIRST_PLACE,
    bronzeCheckpoint = 1,
    silverCheckpoint = 3,
    goldCheckpoint = 10
)

val locationVisitedAchievement = AchievementCheckpoints(
    achievementName = "Locations visited",
    achievementType = AchievementType.LOCATION_VISITED,
    bronzeCheckpoint = 4,
    silverCheckpoint = 12,
    goldCheckpoint = 40
)

fun calcAchievementRank(achievementType: AchievementType, points: Int): AchievementRank {
    val checkpoint: AchievementCheckpoints = when (achievementType) {
        AchievementType.LIKES -> likesAchievement
        AchievementType.PHOTO_TAKEN -> photoTakenAchievement
        AchievementType.FIRST_PLACE -> firstPlaceAchievement
        AchievementType.LOCATION_VISITED -> locationVisitedAchievement
    }

    return when {
        points >= checkpoint.goldCheckpoint -> AchievementRank.GOLD
        points >= checkpoint.silverCheckpoint -> AchievementRank.SILVER
        points >= checkpoint.bronzeCheckpoint -> AchievementRank.BRONZE
        else -> AchievementRank.NONE
    }
}

fun getNextRank(achievementRank: AchievementRank): AchievementRank {
    return when (achievementRank) {
        AchievementRank.NONE -> AchievementRank.BRONZE
        AchievementRank.BRONZE -> AchievementRank.SILVER
        AchievementRank.SILVER -> AchievementRank.GOLD
        AchievementRank.GOLD -> AchievementRank.GOLD
    }
}

fun getNextAchievementMaxPoints(achievementType: AchievementType, points: Int): Int {
    val checkpoint: AchievementCheckpoints = when (achievementType) {
        AchievementType.LIKES -> likesAchievement
        AchievementType.PHOTO_TAKEN -> photoTakenAchievement
        AchievementType.FIRST_PLACE -> firstPlaceAchievement
        AchievementType.LOCATION_VISITED -> locationVisitedAchievement
    }

    return when {
        points >= checkpoint.goldCheckpoint -> checkpoint.goldCheckpoint
        points >= checkpoint.silverCheckpoint -> checkpoint.goldCheckpoint
        points >= checkpoint.bronzeCheckpoint -> checkpoint.silverCheckpoint
        else -> checkpoint.bronzeCheckpoint
    }
}

fun getAchievementIconId(achievementType: AchievementType, achievementRank: AchievementRank): Int? {
    when (achievementType) {
        AchievementType.LIKES -> {
            return when (achievementRank) {
                AchievementRank.NONE -> null
                AchievementRank.BRONZE -> R.drawable.likes_badge_bronze
                AchievementRank.SILVER -> R.drawable.likes_badge_silver
                AchievementRank.GOLD -> R.drawable.likes_badge_gold
            }
        }
        AchievementType.PHOTO_TAKEN -> {
            return when (achievementRank) {
                AchievementRank.NONE -> null
                AchievementRank.BRONZE -> R.drawable.photo_taken_badge_bronze
                AchievementRank.SILVER -> R.drawable.photo_taken_badge_silver
                AchievementRank.GOLD -> R.drawable.photo_taken_badge_gold
            }
        }
        AchievementType.FIRST_PLACE -> {
            return when (achievementRank) {
                AchievementRank.NONE -> null
                AchievementRank.BRONZE -> R.drawable.first_place_badge_bronze
                AchievementRank.SILVER -> R.drawable.first_place_badge_silver
                AchievementRank.GOLD -> R.drawable.first_place_badge_gold
            }
        }
        AchievementType.LOCATION_VISITED -> {
            return when (achievementRank) {
                AchievementRank.NONE -> null
                AchievementRank.BRONZE -> R.drawable.location_visited_badge_bronze
                AchievementRank.SILVER -> R.drawable.location_visited_badge_silver
                AchievementRank.GOLD -> R.drawable.location_visited_badge_gold
            }
        }
    }
}

fun mapRankToLevel(achievementRank: AchievementRank): String {
    return when (achievementRank) {
        AchievementRank.NONE -> "0"
        AchievementRank.BRONZE -> "1"
        AchievementRank.SILVER -> "2"
        AchievementRank.GOLD -> "MAX"
    }
}
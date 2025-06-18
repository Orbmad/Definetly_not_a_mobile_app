package com.dambrofarne.eyeflush.utils

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
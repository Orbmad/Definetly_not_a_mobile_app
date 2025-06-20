package com.dambrofarne.eyeflush.data.repositories.database

import android.util.Log
import com.dambrofarne.eyeflush.utils.AchievementType
import com.dambrofarne.eyeflush.utils.calcAchievementRank
import com.dambrofarne.eyeflush.utils.getBoundingBox
import com.dambrofarne.eyeflush.utils.isWithinRange
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class FirestoreDatabaseRepository(
    private val db: FirebaseFirestore = Firebase.firestore
) : DatabaseRepository {
    override suspend fun addUser(uId: String, username: String): Result<String> {
        val user = hashMapOf(
            "username" to username,
            "picsCount" to 0
        )
        return try {
            db.collection("users")
                .document(uId)
                .set(user, SetOptions.merge())
                .await()
            Result.success("Username aggiornato correttamente.")
        } catch (e: Exception) {
            Log.e("Firestore", "Errore aggiornando username per $uId", e)
            Result.failure(e)
        }
    }


    override suspend fun addUser(uId: String): Result<String> {
        return try {
            val initialUserData = mapOf(
                "likes" to emptyList<String>(),
                "picsCount" to 0,
                "picturesTaken" to emptyList<Map<String, String>>()
            )

            db.collection("users")
                .document(uId)
                .set(initialUserData)
                .await()

            Result.success("Utente creato correttamente.")
        } catch (e: Exception) {
            Log.e("Firestore", "Errore creando documento utente per $uId", e)
            Result.failure(e)
        }
    }


    override suspend fun isUser(uId: String): Boolean {
        return try {
            val doc = db.collection("users").document(uId).get().await()
            doc.exists()
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun changeProfileImage(uId: String, imagePath: String): Result<String> {
        try {
            db.collection("users")
                .document(uId)
                .update("profileImagePath", imagePath)
                .await()
            return Result.success("");
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore aggiornando immagine profilo per $uId", e)
            return Result.success(e.message ?: "");
        }
    }

    override suspend fun getUserImagePath(uId: String): String {
        return try {
            val snapshot = db.collection("users")
                .document(uId)
                .get()
                .await()

            snapshot.getString("profileImagePath") ?: ""
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore recuperando immagine profilo per $uId", e)
            ""
        }
    }

    override suspend fun isUsernameTaken(username: String): Boolean {
        return try {
            val querySnapshot = db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .await()

            !querySnapshot.isEmpty
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore controllando se username è già preso: $username", e)
            false // oppure true se vuoi bloccare in caso di errore
        }
    }

    override suspend fun getUsername(uId: String): String {
        return try {
            val snapshot = db.collection("users")
                .document(uId)
                .get()
                .await()

            snapshot.getString("username") ?: ""
        } catch (e: Exception) {
            Log.e("UserRepo", "Errore recuperando username del profilo per $uId", e)
            ""
        }
    }

    override suspend fun getUserExtendedInfo(uId: String, requesterUId: String): Result<User> {
        return try {
            val snapshot = db.collection("users")
                .document(uId)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("User con id $uId non trovato"))
            }

            val id = snapshot.id
            val username = snapshot.getString("username") ?: ""
            val imagesCount = snapshot.getLong("picsCount")?.toInt() ?: 0
            val profileImagePath = snapshot.getString("profileImagePath")  ?: ""

            val rawList = snapshot.get("picturesTaken") as? List<*>
            val pictureRefs = rawList?.mapNotNull { item ->
                if (item is Map<*, *>) {
                    val itId = item["id"] as? String
                    val itUrl = item["url"] as? String
                    if (itId != null && itUrl != null) {
                        val liked = hasUserLiked(requesterUId, itId)
                        val likes = getPictureLikes(itId)
                        PicQuickRef(picId = itId, url = itUrl, liked = liked, likes = likes)
                    } else null
                } else null
            }?.sortedByDescending { it.likes } ?: emptyList()

            val userAchievements = getUserAchievements(uId).getOrNull()


            Result.success(
                User(
                    uId = id,
                    username = username,
                    profileImagePath = profileImagePath,
                    imagesCount = imagesCount,
                    picturesTaken = pictureRefs,
                    markersPhotographedLvl = userAchievements?.let {
                        calcAchievementRank(AchievementType.LOCATION_VISITED, it.markersPhotographed)
                    },
                    picturesTakenLvl = userAchievements?.let {
                        calcAchievementRank(AchievementType.PHOTO_TAKEN, it.picturesTaken)
                    },
                    likesReceivedLvl = userAchievements?.let {
                        calcAchievementRank(AchievementType.LIKES, it.likesReceived)
                    },
                    mostLikedPicturesLvl = userAchievements?.let {
                        calcAchievementRank(AchievementType.FIRST_PLACE, it.mostLikedPictures)
                    },
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMarkersInRange(point: GeoPoint, rangeMeters: Int): List<Marker> {
        return try {
            val db = FirebaseFirestore.getInstance()

            // Calcolo bounding box (min/max lat/lng)
            val boundingBox = getBoundingBox(point.latitude, point.longitude, rangeMeters)

            val querySnapshot = db.collection("markers")
                .whereGreaterThanOrEqualTo("latitude", boundingBox.minLat)
                .whereLessThanOrEqualTo("latitude", boundingBox.maxLat)
                .whereGreaterThanOrEqualTo("longitude", boundingBox.minLng)
                .whereLessThanOrEqualTo("longitude", boundingBox.maxLng)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { doc ->
                try {
                    val raw = doc.toObject(MarkerRaw::class.java)
                    raw?.let {
                        val markerPoint = GeoPoint(it.latitude, it.longitude)
                        if (isWithinRange(point, markerPoint, rangeMeters)) {
                            it.toMarker().copy(id = doc.id)
                        } else null
                    }
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e("dbRepo", "Errore recuperando i marker", e)
            emptyList()
        }
    }

    override suspend fun addMarker(point: GeoPoint, name: String?): String {
        return try {
            val newMarker = mapOf(
                "latitude" to point.latitude,
                "longitude" to point.longitude,
                "name" to name,
                "imagesCount" to 0,
                "picturesTaken" to emptyList<Map<String, String>>(),
                "mostLikedPicId" to null,
                "mostLikedPicURL" to null,
                "mostLikedPicUserId" to null,
                "mostLikedPicTimeStamp" to null,
                "mostLikedPicLikes" to 0
            )

            val docRef = db.collection("markers").document()
            docRef.set(newMarker, SetOptions.merge()).await()

            docRef.id

        } catch (e: Exception) {
            Log.e("dbRepo", "Errore aggiungendo marker", e)
            ""
        }
    }

    override suspend fun getMarkerExtendedInfo(
        markerId: String,
        requesterUId: String
    ): Result<ExtendedMarker> {
        return try {
            val snapshot = db.collection("markers")
                .document(markerId)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Marker con id $markerId non trovato"))
            }

            val id = snapshot.id
            val name = snapshot.getString("name")
            val latitude = snapshot.getDouble("latitude") ?: 0.0
            val longitude = snapshot.getDouble("longitude") ?: 0.0
            val coordinates = GeoPoint(latitude, longitude)

            val mostLikedPicId = snapshot.getString("mostLikedPicId")
            val mostLikedPicURL = snapshot.getString("mostLikedPicURL")
            val mostLikedPicUserId = snapshot.getString("mostLikedPicUserId")
            val mostLikedPicLikes = snapshot.getLong("mostLikedPicLikes")?.toInt() ?: 0
            val imagesCount = snapshot.getLong("imagesCount")?.toInt() ?: 0
            val mostLikedPicTimeStamp = snapshot
                .getTimestamp("mostLikedPicTimeStamp")
                ?.let { getFormattedImageDate(it) } ?: "Data sconosciuta"

            val rawList = snapshot.get("picturesTaken") as? List<*>
            val pictureRefs = rawList?.mapNotNull { item ->
                if (item is Map<*, *>) {
                    val itId = item["id"] as? String
                    val itUrl = item["url"] as? String
                    if (itId != null && itUrl != null) {
                        val liked = hasUserLiked(requesterUId, itId)
                        val likes = getPictureLikes(itId)
                        PicQuickRef(picId = itId, url = itUrl, liked = liked, likes = likes)
                    } else null
                } else null
            }?.filter { it.picId != mostLikedPicId }
                ?.sortedByDescending { it.likes } ?: emptyList()

            val mostLikedPicUserImage = mostLikedPicUserId?.let { getUserImagePath(it) }
            val mostLikedPicUsername = mostLikedPicUserId?.let { getUsername(it) }

            Result.success(
                ExtendedMarker(
                    id = id,
                    name = name,
                    coordinates = coordinates,
                    mostLikedPicId = mostLikedPicId,
                    mostLikedPicURL = mostLikedPicURL,
                    mostLikedPicUserId = mostLikedPicUserId,
                    mostLikedPicLikes = mostLikedPicLikes,
                    mostLikedPicUserImage = mostLikedPicUserImage,
                    mostLikedPicUsername = mostLikedPicUsername,
                    mostLikedPicTimeStamp = mostLikedPicTimeStamp,
                    imagesCount = imagesCount,
                    picturesTaken = pictureRefs
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }



    override suspend fun addImage(
        markerId: String,
        uId: String,
        timeStamp: LocalDateTime,
        imgURL: String
    ): String {
        return try {
            val timestamp =
                Timestamp(Date.from(timeStamp.atZone(ZoneId.systemDefault()).toInstant()))
            val pictureRef = db.collection("pictures").document()

            val userRef = db.collection("users").document(uId)
            val markerRef = db.collection("markers").document(markerId)

            db.runTransaction { transaction ->
                // Letture
                val userSnap = transaction.get(userRef)
                val markerSnap = transaction.get(markerRef)

                val username = userSnap.getString("username") ?: "Unknown"
                val authorImageUrl = userSnap.getString("profileImagePath") ?: ""

                val markerName = markerSnap.getString("name")
                    ?: run {
                        val lat = markerSnap.getDouble("latitude")?.let { String.format(Locale.US,"%.4f", it) } ?: "0.0"
                        val lng = markerSnap.getDouble("longitude")?.let { String.format(Locale.US,"%.4f", it) } ?: "0.0"
                        "$lat, $lng"
                    }

                val newPicture = Picture(
                    id = pictureRef.id,
                    uId = uId,
                    authorUsername = username,
                    authorImageUrl = authorImageUrl,
                    markerName = markerName,
                    markerId = markerId,
                    url = imgURL,
                    timeStamp = timestamp,
                    likes = 0
                )

                val pictureData = mapOf(
                    "id" to pictureRef.id,
                    "url" to imgURL
                )

                val existingPics = markerSnap.get("picturesTaken") as? List<*>

                // Scritture
                transaction.set(pictureRef, newPicture)
                transaction.update(userRef, "picsCount", FieldValue.increment(1))
                transaction.update(userRef, "picturesTaken", FieldValue.arrayUnion(pictureData))
                transaction.update(markerRef, "imagesCount", FieldValue.increment(1))

                if (existingPics == null || existingPics.isEmpty()) {
                    transaction.update(
                        markerRef, mapOf(
                            "picturesTaken" to FieldValue.arrayUnion(pictureData),
                            "mostLikedPicId" to pictureRef.id,
                            "mostLikedPicURL" to imgURL,
                            "mostLikedPicUserId" to uId,
                            "mostLikedPicLikes" to 0,
                            "mostLikedPicTimeStamp" to timestamp
                        )
                    )
                } else {
                    transaction.update(
                        markerRef,
                        "picturesTaken",
                        FieldValue.arrayUnion(pictureData)
                    )
                }
            }.await()

            pictureRef.id

        } catch (e: Exception) {
            Log.e("dbRepo", "Errore nella transazione per aggiungere immagine", e)
            ""
        }
    }


    override suspend fun likeImage(uId: String, picId: String): Result<Boolean> {
        return try {
            val userDocRef = db.collection("users").document(uId)
            val snapshot = userDocRef.get().await()

            val imageDocRef = db.collection("pictures").document(picId)
            val imageSnap = imageDocRef.get().await()

            val markerId = imageSnap.getString("markerId") ?: return Result.failure(Exception("Marker ID mancante"))
            val markerDocRef = db.collection("markers").document(markerId)

            var liked = false

            if (snapshot.exists()) {
                val rawLikes = snapshot.get("likes") as? List<*>
                val likes = rawLikes?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()

                if (likes.contains(picId)) {
                    // Rimuovi like
                    likes.remove(picId)
                    userDocRef.update("likes", likes).await()
                    imageDocRef.update("likes", FieldValue.increment(-1)).await()
                    liked = false
                } else {
                    val likerUsername = getUsername(uId)
                    val receiverId = imageSnap.getString("uid")
                    likes.add(picId)
                    userDocRef.update("likes", likes).await()
                    imageDocRef.update("likes", FieldValue.increment(1)).await()
                    //Like notification

                    receiverId?.let{
                        addNotification(
                            receiverUId = receiverId,
                            title = "New Like!",
                            type = "like",
                            message = "$likerUsername has liked your image ! Go and check your new spot " +
                                    "in the marker scoreboard!",
                            isRead = false,
                            markerId = markerId
                        )
                    }
                    liked = true
                }
            } else {
                userDocRef.set(mapOf("likes" to listOf(picId))).await()
                imageDocRef.update("likes", FieldValue.increment(1)).await()
                liked = true
            }

            //Marker update
            val imagesQuerySnap = db.collection("pictures")
                .whereEqualTo("markerId", markerId)
                .get()
                .await()

            //Old most liked user image
            val oldTopUserId = markerDocRef.get().await()
                .getString("mostLikedPicUserId")

            //Search for the most liked image
            val mostLikedImage = imagesQuerySnap.documents.maxByOrNull {
                it.getLong("likes") ?: 0L
            }

            if (mostLikedImage != null) {
                val mostLikedPicId = mostLikedImage.id
                val mostLikedPicURL = mostLikedImage.getString("url") ?: ""
                val mostLikedPicLikes = mostLikedImage.getLong("likes")?.toInt() ?: 0
                val mostLikedPicUserId = mostLikedImage.getString("uid") ?: ""
                val mostLikedPicTimeStamp = mostLikedImage.getTimestamp("timeStamp") ?: Timestamp.now()

                if (mostLikedPicUserId != oldTopUserId) {
                    val newLeaderUsername = getUsername(mostLikedPicUserId)
                    addNotification(
                        receiverUId = mostLikedPicUserId,
                        title = "You're on Top!",
                        type = "leaderboard",
                        message = "Your image is now the most liked for a Marker! Great job, $newLeaderUsername!",
                        isRead = false,
                        markerId = markerId
                    )
                }

                markerDocRef.update(
                    mapOf(
                        "mostLikedPicId" to mostLikedPicId,
                        "mostLikedPicURL" to mostLikedPicURL,
                        "mostLikedPicLikes" to mostLikedPicLikes,
                        "mostLikedPicUserId" to mostLikedPicUserId,
                        "mostLikedPicTimeStamp" to mostLikedPicTimeStamp
                    )
                ).await()
            }

            Log.w("Likes", liked.toString())
            Result.success(liked)

        } catch (e: Exception) {
            Log.w("Likes", e)
            Result.failure(e)
        }
    }


    override suspend fun hasUserLiked(uId: String, picId: String): Boolean {
        return try {
            val snapshot = db
                .collection("users")
                .document(uId)
                .get()
                .await()

            if (snapshot.exists()) {
                val rawLikes = snapshot.get("likes") as? List<*>
                val likes = rawLikes?.filterIsInstance<String>()
                likes?.contains(picId) == true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    private suspend fun getPictureLikes(picId: String): Int {
        return try {
            val imageDoc = db
                .collection("pictures")
                .document(picId)
                .get()
                .await()

            if (imageDoc.exists()) {
                imageDoc.getLong("likes")?.toInt() ?: 0
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun getFormattedImageDate(timestamp: Timestamp): String {
        return try {
            timestamp.toDate().toInstant()?.let { instant ->
                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                localDateTime.format(formatter)
            } ?: "Data non disponibile"
        } catch (e: Exception) {
            "Data non disponibile"
        }
    }

    override suspend fun getPictureExtendedInfo(picId : String) : Result<PictureFormatted> {
        return try {
            val snapshot = db.collection("pictures")
                .document(picId)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Picture with id $picId  picId"))
            }

            val id = snapshot.id?:""
            val uId = snapshot.getString("uId")?:""
            val markerId = snapshot.getString("markerId")?:""
            val url = snapshot.getString("url")?:""
            val timeStamp = snapshot.getTimestamp("timeStamp")?.let { getFormattedImageDate(it) }?:""
            val likes : Int = snapshot.getLong("likes")?.toInt() ?: 0
            val authorUsername = snapshot.getString("authorUsername")?:""
            val authorImageUrl = snapshot.getString("authorImageUrl")?:""
            val markerName = snapshot.getString("markerName")?:""


            Result.success(
                PictureFormatted(
                    id = id,
                    uId = uId,
                    markerId = markerId,
                    url = url,
                    timeStamp = timeStamp,
                    likes = likes,
                    authorUsername = authorUsername,
                    authorImageUrl = authorImageUrl,
                    markerName = markerName
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addNotification(
        receiverUId: String,
        title: String,
        type: String,
        message: String,
        isRead: Boolean,
        markerId: String?
    ): Result<String> {
        return try {
            val notificationData = mapOf(
                "title" to title,
                "message" to message,
                "timestamp" to Timestamp.now(),
                "read" to isRead,
                "type" to type,
                "markerId" to markerId
            )

            val notificationsRef = db.collection("users")
                .document(receiverUId)
                .collection("notifications")

            val documentRef = notificationsRef.add(notificationData).await()

            Result.success(documentRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun readNotification(uId: String, notificationId: String): Result<Boolean> {
        return try {
            val notificationRef = db.collection("users")
                .document(uId)
                .collection("notifications")
                .document(notificationId)

            notificationRef.update("read", true).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun hasUnreadNotifications(uId: String): Boolean {
        return try {
            val notificationsRef = db.collection("users")
                .document(uId)
                .collection("notifications")

            val unreadQuery = notificationsRef
                .whereEqualTo("read", false)
                .limit(1)

            val result = unreadQuery.get().await()
            !result.isEmpty
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getUserAchievements(uId: String): Result<UserAchievements> {
        return try {
            val userRef = db.collection("users").document(uId)
            val userSnap = userRef.get().await()

            val picsCount = userSnap.getLong("picsCount")?.toInt() ?: 0

            val markersSnap = db.collection("markers")
                .whereEqualTo("mostLikedPicUserId", uId)
                .get()
                .await()

            val picsSnap = db.collection("pictures")
                .whereEqualTo("uid", uId)
                .get()
                .await()
            val distinctMarkerMostLiked = picsSnap.documents
                .mapNotNull { it.getString("markerId") }
                .toSet()
            val mostLikedPictures = distinctMarkerMostLiked.size

            val likesReceived = picsSnap.documents.sumOf { it.getLong("likes") ?: 0L }.toInt()
            val distinctMarkerIds = picsSnap.documents
                .mapNotNull { it.getString("markerId") }
                .toSet()
            val markersPhotographed = distinctMarkerIds.size

            Result.success(
                UserAchievements(
                    likesReceived = likesReceived,
                    picturesTaken = picsCount,
                    markersPhotographed = markersPhotographed,
                    mostLikedPictures = mostLikedPictures
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun changeThemePreferenceString(userId: String, pref : String) {
        db.collection("users")
            .document(userId)
            .update("themePreference", pref)
            .await()
    }

    override suspend fun getThemePreferenceString(userId: String): String? {
        val snapshot = db.collection("users")
            .document(userId)
            .get()
            .await()

        return if (snapshot.exists()) snapshot.getString("themePreference") else "SYSTEM"
    }

    override suspend fun getNotifications(uId: String): List<NotificationItem> {
        return try {
            val notificationsRef = db.collection("users")
                .document(uId)
                .collection("notifications")
                .orderBy("timestamp", Query.Direction.DESCENDING)

            val snapshot = notificationsRef.get().await()

            val items = snapshot.documents.mapNotNull { doc ->
                try {
                    val id = doc.id
                    val title = doc.getString("title") ?: ""
                    val message = doc.getString("message") ?: ""
                    val timestamp = doc.getTimestamp("timestamp")
                    val timeFormatted = timestamp?.let { getFormattedImageDate(it) } ?: "Unknown"
                    val isRead = doc.getBoolean("read") ?: false
                    val type = doc.getString("type") ?: ""
                    val referredMarkerId = doc.getString("markerId") ?: ""

                    NotificationItem(
                        id = id,
                        title = title,
                        message = message,
                        time = timeFormatted,
                        isRead = isRead,
                        type = type,
                        referredMarkerId = referredMarkerId
                    )
                } catch (e: Exception) {
                    null
                }
            }

            items
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun deleteNotification(uId: String, notificationId: String): Result<Boolean> {
        return try {
            val notificationRef = db.collection("users")
                .document(uId)
                .collection("notifications")
                .document(notificationId)

            notificationRef.delete().await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
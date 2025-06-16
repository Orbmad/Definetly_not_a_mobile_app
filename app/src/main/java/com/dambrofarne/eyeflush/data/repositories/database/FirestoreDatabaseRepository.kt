package com.dambrofarne.eyeflush.data.repositories.database

import android.util.Log
import com.dambrofarne.eyeflush.utils.getBoundingBox
import com.dambrofarne.eyeflush.utils.isWithinRange
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Source
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date

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
            db.collection("users")
                .document(uId)
                .set(emptyMap<String, Any>())
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

    override suspend fun getUserExtendedInfo(uId: String): Result<User> {
        return try {
            val snapshot = db.collection("users")
                .document(uId)
                .get()
                .await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("User con id $uId non trovato"))
            }

            val id = snapshot.id
            val username = snapshot.getString("username")
            val score = snapshot.getLong("imagesCount")?.toInt() ?: 0
            val imagesCount = snapshot.getLong("imagesCount")?.toInt() ?: 0

            val rawList = snapshot.get("picturesTaken") as? List<*>
            val pictureRefs = rawList?.mapNotNull { item ->
                if (item is Map<*, *>) {
                    val itId = item["id"] as? String
                    val itUrl = item["url"] as? String
                    if (itId != null && itUrl != null) {
                        val liked = hasUserLiked(uId, itId)
                        val likes = getPictureLikes(itId)
                        PicQuickRef(picId = itId, url = itUrl, liked = liked, likes = likes)
                    } else null
                } else null
            }?.sortedByDescending { it.likes } ?: emptyList()

            Result.success(
                User(
                    uId = id,
                    username = username,
                    score = score,
                    imagesCount = imagesCount,
                    picturesTaken = pictureRefs
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

            val mostLikedPicUserImage = mostLikedPicUserId?.let { getUserImagePath(it) }
            val mostLikedPicUsername = mostLikedPicUserId?.let { getUsername(it) }
            val mostLikedPicTimeStamp = mostLikedPicId?.let { getFormattedImageDate(it) }

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

            val newPicture = Picture(
                id = pictureRef.id,
                uId = uId,
                markerId = markerId,
                url = imgURL,
                timeStamp = timestamp,
                likes = 0
            )

            val pictureData = mapOf(
                "id" to pictureRef.id,
                "url" to imgURL
            )
            val userRef = db.collection("users").document(uId)
            val markerRef = db.collection("markers").document(markerId)

            db.runTransaction { transaction ->
                //Letture
                val markerSnap = transaction.get(markerRef)
                val existingPics = markerSnap.get("picturesTaken") as? List<*>

                //Scritture
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
                            "mostLikedPicTimeStamp" to Timestamp(Date.from(timeStamp.atZone(ZoneId.systemDefault()).toInstant()))
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

    override suspend fun likeImage(uId: String, picId: String): Result<String> {
        return try {
            val userDocRef = db.collection("users").document(uId)
            val imageDocRef = db.collection("images").document(picId)

            val snapshot = userDocRef.get().await()

            if (snapshot.exists()) {
                val rawLikes = snapshot.get("likes") as? List<*>
                val likes = rawLikes?.filterIsInstance<String>()?.toMutableList() ?: mutableListOf()

                if (!likes.contains(picId)) {
                    likes.add(picId)
                    userDocRef.update("likes", likes).await()
                    imageDocRef.update("likes", FieldValue.increment(1)).await()
                }
            } else {
                userDocRef.set(mapOf("likes" to listOf(picId))).await()
                imageDocRef.update("likes", FieldValue.increment(1)).await()
            }

            Result.success("Operazione completata")
        } catch (e: Exception) {
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
                .collection("images")
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

    private suspend fun getFormattedImageDate(picId: String): String {
        return try {
            val imageDoc = db
                .collection("images")
                .document(picId)
                .get()
                .await()

            val timestamp = imageDoc.getTimestamp("timestamp")
            timestamp?.toDate()?.toInstant()?.let { instant ->
                val localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
                val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                localDateTime.format(formatter)
            } ?: "Data non disponibile"
        } catch (e: Exception) {
            "Data non disponibile"
        }
    }
}
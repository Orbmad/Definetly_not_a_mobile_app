package com.dambrofarne.eyeflush.data.repositories.database

import android.util.Log
import com.dambrofarne.eyeflush.utils.getBoundingBox
import com.dambrofarne.eyeflush.utils.isWithinRange
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import org.osmdroid.util.GeoPoint
import java.time.LocalDateTime
import java.time.ZoneId
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

    override suspend fun addMarker(point: GeoPoint): String {
        return try {
            val newMarker = MarkerRaw(
                latitude = point.latitude,
                longitude = point.longitude
            )

            val docRef = db.collection("markers")
                .add(newMarker)
                .await()
            docRef.id

        } catch (e: Exception) {
            Log.e("dbRepo", "Errore aggiungendo marker", e)
            ""
        }
    }

    override suspend fun addMarker(point: GeoPoint, name: String): String {
        return try {
            val newMarker = MarkerRaw(
                latitude = point.latitude,
                longitude = point.longitude,
                name = name
            )

            val docRef = db.collection("markers")
                .add(newMarker)
                .await()
            docRef.id

        } catch (e: Exception) {
            Log.e("dbRepo", "Errore aggiungendo marker", e)
            ""
        }
    }

    override suspend fun addImage(
        markerId: String,
        uId: String,
        timeStamp: LocalDateTime,
        imgURL: String
    ): String {
        return try {
            val timestamp = Timestamp(Date.from(timeStamp.atZone(ZoneId.systemDefault()).toInstant()))
            val pictureRef = db.collection("pictures").document()

            val newPicture = Picture(
                id = pictureRef.id,
                uId = uId,
                markerId = markerId,
                url = imgURL,
                timeStamp = timestamp,
                likes = 0
            )

            val pictureData = mapOf("id" to pictureRef.id, "url" to imgURL)
            val userRef = db.collection("users").document(uId)
            val markerRef = db.collection("markers").document(markerId)

            db.runTransaction { transaction ->
                // Salvo la nuova immagine
                transaction.set(pictureRef, newPicture)

                // Aggiorno l'utente
                transaction.update(userRef, "picsCount", FieldValue.increment(1))
                transaction.update(userRef, "picturesTaken", FieldValue.arrayUnion(pictureData))

                // Recupero dati del marker
                val markerSnap = transaction.get(markerRef)

                val existingPics = markerSnap.get("picturesTaken") as? List<*>

                // Se il marker non ha ancora immagini, aggiornio anche l'immagine più popolare
                if (existingPics == null || existingPics.isEmpty()) {
                    transaction.update(markerRef, mapOf(
                        "picturesTaken" to FieldValue.arrayUnion(pictureData),
                        "mostLikedPicId" to pictureRef.id,
                        "mostLikedPicURL" to imgURL
                    ))
                } else {
                    transaction.update(markerRef, "picturesTaken", FieldValue.arrayUnion(pictureData))
                }
            }.await()

            pictureRef.id

        } catch (e: Exception) {
            Log.e("dbRepo", "Errore nella transazione per aggiungere immagine", e)
            ""
        }
    }


}
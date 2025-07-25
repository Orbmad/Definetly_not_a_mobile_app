package com.dambrofarne.eyeflush.data.repositories.imagestoring

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.dambrofarne.eyeflush.BuildConfig
import com.dambrofarne.eyeflush.utils.cropToCenterAspectRatio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class ImgurImageStoringRepository(private val context: Context) : ImageStoringRepository {

    private val clientId = BuildConfig.IMGUR_CLIENT_ID

    override suspend fun uploadImage(uri: Uri): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                    ?: return@withContext Result.failure(Exception("Bitmap not valid."))

                uploadImage(bitmap)
            } catch (e: Exception) {
                Log.w("ImageUpload", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun uploadImage(file: File): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ?: return@withContext Result.failure(Exception("Bitmap not valid."))

                uploadImage(bitmap)
            } catch (e: Exception) {
                Log.w("ImageUpload", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun uploadCroppedImage(file: File): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    ?: return@withContext Result.failure(Exception("Bitmap not valid."))

                val cropped = cropToCenterAspectRatio(bitmap, 4f / 5f)
                uploadImage(cropped)
            } catch (e: Exception) {
                Log.w("ImageUpload", e)
                Result.failure(e)
            }
        }
    }

    private suspend fun uploadImage(bitmap: Bitmap): Result<String> {
        val base64Image = encodeImageToBase64(bitmap)
        return uploadImageToImgur(base64Image)
    }


    override suspend fun getDrawableImage(uri: Uri): Result<Drawable> {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = when (uri.scheme) {
                    "http", "https" -> URL(uri.toString()).openStream()
                    else -> context.contentResolver.openInputStream(uri)
                } ?: return@withContext Result.failure(Exception("Unable to open input stream for $uri"))

                val drawable = Drawable.createFromStream(inputStream, uri.toString())
                    ?: return@withContext Result.failure(Exception("Unable to create drawable from $uri"))

                Result.success(drawable)

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private fun encodeImageToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private suspend fun uploadImageToImgur(base64Image: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL("https://api.imgur.com/3/image")
                val connection = url.openConnection() as HttpsURLConnection

                connection.apply {
                    requestMethod = "POST"
                    doInput = true
                    doOutput = true
                    setRequestProperty("Authorization", "Client-ID $clientId")
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                }

                val body = "image=${URLEncoder.encode(base64Image, "UTF-8")}&type=base64"

                connection.outputStream.use { it.write(body.toByteArray()) }

                val responseCode = connection.responseCode
                val stream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: throw Exception("Unknown error. No further info.")
                }

                val responseText = stream.bufferedReader().use { it.readText() }

                if (responseCode in 200..299) {
                    val json = JSONObject(responseText)
                    val link = json.getJSONObject("data").getString("link")
                    Result.success(link)
                } else {
                    Result.failure(Exception("Imgur Error: $responseCode\n$responseText"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}

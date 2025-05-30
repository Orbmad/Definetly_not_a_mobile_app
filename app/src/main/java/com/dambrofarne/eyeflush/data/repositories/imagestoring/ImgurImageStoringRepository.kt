package com.dambrofarne.eyeflush.data.repositories.imagestoring

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import com.dambrofarne.eyeflush.BuildConfig
import com.google.api.LogProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.net.URL
import java.net.URLEncoder
import javax.net.ssl.HttpsURLConnection

class ImgurImageStoringRepository(private val context: Context) : ImageStoringRepository {

    private val CLIENT_ID = BuildConfig.IMGUR_CLIENT_ID;

    override suspend fun uploadImage(uri: Uri): Result<String> {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
                ?: return Result.failure(Exception("Bitmap non valido."))

            val base64Image = encodeImageToBase64(bitmap)
            uploadImageToImgur(base64Image)

        } catch (e: Exception) {
            Result.failure(e)
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
                    setRequestProperty("Authorization", "Client-ID $CLIENT_ID")
                    setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
                }

                val body = "image=${URLEncoder.encode(base64Image, "UTF-8")}&type=base64"

                connection.outputStream.use { it.write(body.toByteArray()) }

                val responseCode = connection.responseCode
                val stream = if (responseCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: throw Exception("Errore sconosciuto. Nessun body di errore disponibile.")
                }

                val responseText = stream.bufferedReader().use { it.readText() }

                if (responseCode in 200..299) {
                    val json = JSONObject(responseText)
                    val link = json.getJSONObject("data").getString("link")
                    Result.success(link)
                } else {
                    Result.failure(Exception("Errore Imgur: $responseCode\n$responseText"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}

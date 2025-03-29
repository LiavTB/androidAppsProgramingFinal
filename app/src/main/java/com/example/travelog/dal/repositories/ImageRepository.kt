package com.example.travelog.dal.repositories

import android.content.Context
import android.net.Uri
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.travelog.BuildConfig


object ImageRepository {

    // Cloudinary configuration (for unsigned upload, only cloud name and preset are needed)
    private const val CLOUD_NAME = "dlx18pjmf"
    private const val UPLOAD_PRESET = "your_upload_preset"
    private const val BASE_URL = "https://res.cloudinary.com/$CLOUD_NAME/image/upload"

    /**
     * Builds the full URL for a given image publicId.
     * @param publicId the identifier of the image (e.g. "sample.jpg" or "folder/sample")
     * @param transformation optional transformation parameters, e.g. "w_300,h_300,c_fill"
     */
    fun getImageUrl(publicId: String, transformation: String? = null): String {
        val transformationSegment = transformation?.let { "$it/" } ?: ""
        return "$BASE_URL/$transformationSegment$publicId"
    }

    /**
     * Uploads an image (from a Uri) to Cloudinary and returns the secure URL.
     * This is a suspend function so it can be called from a coroutine.
     *
     * @param context Application context (needed to open the InputStream)
     * @param imageUri Uri of the image to upload
     * @return the secure URL of the uploaded image or null if upload fails
     */
    suspend fun uploadImage(context: Context, imageUri: Uri): String? = withContext(Dispatchers.IO) {
        try {
            // Prepare the Cloudinary configuration for unsigned upload
            val config = mapOf(
                "cloud_name" to "dlx18pjmf",
            "api_key" to BuildConfig.CLOUDINARY_API_KEY,
            "api_secret" to "MRVkC0klIe-G9sR3B7KvIsb_V70" // Click 'View API Keys' above to copy your API secret
            )
            val cloudinary = Cloudinary(config)

            // Open an InputStream from the image Uri
            context.contentResolver.openInputStream(imageUri)?.use { inputStream ->
                // Upload the image. The options here ensure we treat the resource as an image.
                val uploadResult = cloudinary.uploader().upload(inputStream, ObjectUtils.asMap("resource_type", "image"))
                // Cloudinary returns a map containing, among others, the "secure_url"
                return@withContext uploadResult["secure_url"] as? String
            }
            return@withContext null
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext null
        }
    }
}

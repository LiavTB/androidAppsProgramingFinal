//package com.example.myapplication.dal.repositories
//
//import android.content.Context
//import android.net.Uri
//import androidx.core.net.toUri
//import com.bumptech.glide.Glide
//import com.example.myapplication.dal.room.AppDatabase
//import com.example.myapplication.models.Image
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.tasks.await
//
//class ImageRepository(private val context: Context) {
//    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
//    private val localDb = AppDatabase.getDatabase(context)
//
//    companion object {
//        const val IMAGES_REF = "images"
//    }
//
//    suspend fun uploadImage(imageUri: Uri, imageId: String) {
//        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")
//        imageRef.putFile(imageUri).await()
//
//        localDb.imageDao().insertAll(Image(imageId, imageUri.toString()))
//    }
//
//    suspend fun getImageRemoteUri(imageId: String): Uri {
//        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")
//
//        return imageRef.downloadUrl.await()
//    }
//
//    fun downloadAndCacheImage(uri: Uri, imageId: String): String {
//        val file = Glide.with(context)
//            .asFile()
//            .load(uri)
//            .submit()
//            .get()
//
//        localDb.imageDao().insertAll(Image(imageId, file.absolutePath))
//
//        return file.absolutePath
//    }
//
//    fun getImageLocalUri(imageId: String): String {
//        return localDb.imageDao().getImageById(imageId).value?.uri ?: ""
//    }
//
//    suspend fun getImagePathById(imageId: String): String {
//        val image = localDb.imageDao().getImageById(imageId).value
//
//        if (image != null) return image.uri
//
//        val remoteUri = getImageRemoteUri(imageId)
//        val localPath = downloadAndCacheImage(remoteUri, imageId)
//
//        localDb.imageDao().insertAll(Image(imageId, localPath))
//
//        return localPath
//    }
//
//    suspend fun deleteImage(imageId: String) {
//        val imageRef = storage.reference.child("$IMAGES_REF/$imageId")
//        imageRef.delete().await()
//
//        deleteLocalImage(imageId)
//    }
//
//    private fun deleteLocalImage(imageId: String) {
//        val image = localDb.imageDao().getImageById(imageId).value
//        image?.let {
//            val file = Glide.with(context)
//                .asFile()
//                .load(it.uri)
//                .submit()
//                .get()
//
//            if (file.exists()) {
//                file.delete()
//            }
//
//            localDb.imageDao().deleteImage(imageId)
//        }
//    }
//}
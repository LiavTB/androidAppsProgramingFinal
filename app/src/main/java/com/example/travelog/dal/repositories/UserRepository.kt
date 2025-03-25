//package com.example.myapplication.dal.repositories
//
//import android.content.Context
//import android.net.Uri
//import androidx.core.net.toUri
//import com.example.myapplication.dal.room.AppDatabase
//import com.example.myapplication.models.User
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.storage.FirebaseStorage
//import kotlinx.coroutines.tasks.await
//
//class UserRepository(private val context: Context) {
//    companion object {
//        const val USERS_COLLECTION = "users"
//        const val IMAGES_REF = "images"
//    }
//
//    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
//    private val localDb = AppDatabase.getDatabase(context)
//    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
//    private val imageRepository = ImageRepository(context)
//
//    suspend fun saveUserInDB(user: User) {
//        val newUser = user.copy()
//        newUser.imageUri = null
//
//        db.collection(USERS_COLLECTION)
//            .document(newUser.id)
//            .set(newUser.json)
//            .await()
//
//        localDb.userDao().insertAll(newUser)
//    }
//
//    suspend fun saveUserImage(imageUri: String, userId: String) =
//        imageRepository.uploadImage(imageUri.toUri(), userId)
//
//    suspend fun getUserById(userId: String): User {
//        var user = localDb.userDao().getUserById(userId)
//
//        if (user != null) return user.apply { imageUri = imageRepository.getImagePathById(userId) };
//
//        user = getUserFromFireStore(userId)
//        localDb.userDao().insertAll(user)
//
//        return user.apply { imageUri = imageRepository.getImagePathById(userId) }
//    }
//
//    private suspend fun getUserFromFireStore(userId: String): User{
//        val user = db.collection(USERS_COLLECTION)
//            .document(userId)
//            .get()
//            .await()
//            .toObject(User::class.java)
//
//        user?.id = userId
//        user?.imageUri = imageRepository.downloadAndCacheImage(imageRepository.getImageRemoteUri(userId), userId)
//
//        return user!!
//    }
//
//    private suspend fun getUserImageUri(userId: String): Uri = imageRepository.getImageRemoteUri(userId)
//}
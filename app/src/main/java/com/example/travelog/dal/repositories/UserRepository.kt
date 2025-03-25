package com.example.travelog.dal.repositories

import android.content.Context
import android.net.Uri
import com.example.travelog.dal.room.AppDatabase
import com.example.travelog.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val firebaseAuth = FirebaseAuth.getInstance()

    private val db = AppDatabase.getDatabase(context)

    // Fetch a user from the local cache; if not available, load from Firebase and cache it.
    suspend fun getUser(userId: String): UserEntity? {
        var user = db.userDao().getUser(userId)
        if (user == null) {
            val snapshot = firestore.collection("users").document(userId).get().await()
            if (snapshot.exists()) {
                user = snapshot.toObject(UserEntity::class.java)
                user?.let { db.userDao().insertUser(it) }
            }
        }
        return user
    }

    // Update user in Firebase, the local cache and Firebase Auth (if the current user).
    suspend fun updateUser(user: UserEntity) {
        firestore.collection("users").document(user.userId).set(user).await()
        db.userDao().updateUser(user)
        firebaseAuth.currentUser?.let { current ->
            if (current.uid == user.userId) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(user.fullName)
                    .setPhotoUri(Uri.parse(user.profileImg))
                    .build()
                current.updateProfile(profileUpdates).await()
            }
        }
    }

    suspend fun deleteUser(user: UserEntity) {
        firestore.collection("users").document(user.userId).delete().await()
        db.userDao().deleteUser(user)
    }

    // Add a new user to Firestore and the local database
    suspend fun addUser(user: UserEntity) {
        // Save to Firestore
        firestore.collection("users").document(user.userId).set(user).await()
        // Cache in local Room database
        db.userDao().insertUser(user)

        // Update Firebase Auth profile if it's the current user
        firebaseAuth.currentUser?.let { currentUser ->
            if (currentUser.uid == user.userId) {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(user.fullName)
                    .setPhotoUri(Uri.parse(user.profileImg))
                    .build()
                currentUser.updateProfile(profileUpdates).await()
            }
        }
    }
}


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
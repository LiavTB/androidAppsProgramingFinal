//package com.example.myapplication.dal.repositories
//
//import android.content.Context
//import androidx.core.net.toUri
//import androidx.lifecycle.LiveData
//import com.example.myapplication.dal.room.AppDatabase
//import com.example.myapplication.models.Review
//import com.example.myapplication.models.User
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.google.firebase.firestore.Query
//import kotlinx.coroutines.tasks.await
//
//class ReviewsRepository(private val context: Context) {
//    private val db = FirebaseFirestore.getInstance()
//    private val auth = FirebaseAuth.getInstance()
//    private val localDb = AppDatabase.getDatabase(context)
//    private val imageRepository = ImageRepository(context)
//
//    suspend fun getMyReviews(): List<Review> {
//        val userId = auth.currentUser?.uid ?: return emptyList()
//        val snapshot = db.collection("reviews")
//            .whereEqualTo("userId", userId)
//            .get()
//            .await()
//        return snapshot.toObjects(Review::class.java)
//    }
//
//    suspend fun deleteReview(reviewId: String) {
//        db.collection("reviews").document(reviewId).delete().await()
//        localDb.reviewDao().deleteReview(reviewId)
//        imageRepository.deleteImage(reviewId)
//    }
//
//    suspend fun saveReviewInDB(review: Review): Review {
//        val newReview = saveReviewInFireStore(review)
//
//        localDb.reviewDao().insertAll(newReview)
//
//        return newReview.apply { imageUri = review.imageUri }
//    }
//
//    private suspend fun saveReviewInFireStore(review: Review): Review {
//        val updatedReview = review.copy(
//            timestamp = System.currentTimeMillis()
//        )
//        updatedReview.imageUri = null
//
//        val documentRef = if (review.id.isNotEmpty())
//            db.collection("reviews").document(review.id)
//        else
//            db.collection("reviews").document().also { updatedReview.id = it.id }
//
//
//        documentRef.set(updatedReview.json).await()
//        return updatedReview
//    }
//
//    suspend fun uploadReviewImage(imageUri: String, reviewId: String) {
//        imageRepository.uploadImage(imageUri.toUri(), reviewId)
//    }
//
//    fun getAllCachedReviews(isLoggedUserReviews: Boolean): LiveData<List<Review>> {
//        return if (!isLoggedUserReviews) localDb.reviewDao().getAllReviews()
//        else localDb.reviewDao().getAllReviewsOfUser(auth.currentUser!!.uid)
//    }
//
//    suspend fun getAllReviews(isLoggedUserReviews: Boolean): List<Review> {
//        val reviewsRef = db.collection("reviews")
//            .orderBy("timestamp", Query.Direction.DESCENDING)
//
//        if (isLoggedUserReviews) reviewsRef.whereEqualTo("userId", auth.currentUser!!.uid)
//
//        val reviews = reviewsRef.get().await().documents.map { document ->
//            document.toObject(Review::class.java)!!.apply { id = document.id }
//        }
//
//        localDb.reviewDao().insertAll(*reviews.toTypedArray())
//        return reviews
//    }
//
//    suspend fun getReviewById(reviewId: String): Review {
//        var review = localDb.reviewDao().getReviewById(reviewId)
//            .apply { imageUri = imageRepository.getImagePathById(reviewId) }
//
//        if (review != null) return review;
//
//        review = getReviewFromFireStore(reviewId)
//        localDb.reviewDao().insertAll(review)
//
//        return review.apply { imageUri = imageRepository.getImagePathById(reviewId) }
//    }
//
//    private suspend fun getReviewFromFireStore(reviewId: String): Review {
//        val review = db.collection("reviews")
//            .document(reviewId)
//            .get()
//            .await()
//            .toObject(Review::class.java)
//
//        review?.id = reviewId
//        review?.imageUri = imageRepository.downloadAndCacheImage(
//            imageRepository.getImageRemoteUri(reviewId),
//            reviewId
//        )
//
//        return review!!
//    }
//}
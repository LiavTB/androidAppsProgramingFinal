//package com.example.myapplication.dal.room.dao
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.myapplication.models.Review
//import com.example.myapplication.models.User
//
//@Dao
//interface ReviewDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg review: Review)
//
//    @Query("SELECT * FROM reviews WHERE id = :reviewId")
//    fun getReviewById(reviewId: String): Review
//
//    @Query("SELECT * FROM reviews ORDER BY timestamp DESC")
//    fun getAllReviews(): LiveData<List<Review>>
//
//    @Query("SELECT * FROM reviews WHERE userId = :userId ORDER BY timestamp DESC")
//    fun getAllReviewsOfUser(userId: String): LiveData<List<Review>>
//
//    @Query("DELETE FROM reviews WHERE id = :reviewId")
//    fun deleteReview(reviewId: String)
//
//    @Query("UPDATE reviews SET image_uri = :imageUri WHERE id = :reviewId")
//    suspend fun updateImageUri(reviewId: String, imageUri: String)
//}
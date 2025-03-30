package com.example.travelog.dal.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import com.example.travelog.models.PostEntity

@Dao
interface PostDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertPost(post: PostEntity)

    @Update
    suspend fun updatePost(post: PostEntity)

    @Delete
    suspend fun deletePost(post: PostEntity)

    @Query("SELECT * FROM posts WHERE id = :postId")
    suspend fun getPost(postId: Int): PostEntity?

    @Query("SELECT * FROM posts WHERE description LIKE :query OR locationTag LIKE :query")
    suspend fun searchPosts(query: String): List<PostEntity>

    @Query("SELECT * FROM posts WHERE tripId = :tripId")
    suspend fun getAllPostsByTrip(tripId: String): List<PostEntity>
}

package com.example.travelog.dal.repositories


import android.content.Context
import com.example.travelog.dal.room.AppDatabase
import com.example.travelog.models.PostEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class PostRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    private val db = AppDatabase.getDatabase(context)

    suspend fun getPost(postId: Int): PostEntity? {
        var post = db.postDao().getPost(postId)
        if (post == null) {
            val snapshot = firestore.collection("posts").document(postId.toString()).get().await()
            if (snapshot.exists()) {
                post = snapshot.toObject(PostEntity::class.java)
                post?.let { db.postDao().insertPost(it) }
            }
        }
        return post
    }

    suspend fun updatePost(post: PostEntity) {
        firestore.collection("posts").document(post.id.toString()).set(post).await()
        db.postDao().updatePost(post)
    }

    suspend fun deletePost(post: PostEntity) {
        firestore.collection("posts").document(post.id.toString()).delete().await()
        db.postDao().deletePost(post)
    }

    suspend fun addPost(post: PostEntity) {
        // Save to Firestore
        firestore.collection("posts").document(post.id.toString()).set(post).await()
        // Cache in local Room database
        db.postDao().insertPost(post)
    }
}

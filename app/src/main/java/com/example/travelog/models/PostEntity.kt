package com.example.travelog.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tripId: Int,     // Foreign key linked to TripEntity.id
    val userId: String,  // Foreign key linked to UserEntity.userId
    val photo: String,   // URL to externally hosted photo
    val description: String,
    val locationTag: String,
    val date: Long       // Timestamp for post creation
)

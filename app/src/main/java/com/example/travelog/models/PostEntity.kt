package com.example.travelog.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "posts")
data class PostEntity(
    @PrimaryKey() val id: String = 0.toString(),
    val tripId: String,     // Foreign key linked to TripEntity.id
    val userId: String,  // Foreign key linked to UserEntity.userId
    val photo: String,   // URL to externally hosted photo
    val description: String,
    val locationTag: List<String>,
    val date: Long       // Timestamp for post creation
) {
    // No-argument constructor for Firestore
    constructor() : this("", "", "", "", "", emptyList(), 0) {
        // Empty constructor for Firestore
    }
}

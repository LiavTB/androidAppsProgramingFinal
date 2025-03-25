package com.example.travelog.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val userId: String, // Firebase Auth uid
    val email: String,
    val fullName: String,
    val profileImg: String // URL to profile image
)

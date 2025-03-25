package com.example.travelog.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trips")
data class TripEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,  // Foreign key linked to UserEntity.userId
    val name: String,
    val destination: String,
    val startDate: Long  // Timestamp (ms since epoch)
)

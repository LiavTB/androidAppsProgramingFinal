package com.example.travelog.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class Image(
    @PrimaryKey
    val id: String = "",
    @ColumnInfo(name = "uri")
    val uri: String = ""
)
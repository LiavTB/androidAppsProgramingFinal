package com.example.travelog.dal.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.travelog.models.Image

@Dao
interface ImageDao {
    @Query("SELECT * FROM images WHERE id = :id")
    fun getImageById(id: String): LiveData<Image>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg images: Image)

    @Query("DELETE FROM images WHERE id = :id")
    fun deleteImage(id: String)
}
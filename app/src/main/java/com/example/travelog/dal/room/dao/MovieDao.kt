//package com.example.myapplication.dal.room.dao
//
//import androidx.lifecycle.LiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.myapplication.models.Movie
//import com.example.myapplication.models.User
//
//@Dao
//interface MovieDao {
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg movie: Movie)
//
//    @Query("SELECT * FROM movies WHERE id = :movieId")
//    fun getMovieById(movieId: Int): Movie
//}
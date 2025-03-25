//package com.example.myapplication.dal.room
//
//import androidx.lifecycle.LiveData
//import androidx.lifecycle.MutableLiveData
//import androidx.room.Dao
//import androidx.room.Insert
//import androidx.room.OnConflictStrategy
//import androidx.room.Query
//import com.example.myapplication.models.User
//
//@Dao
//interface UserDao {
//    @Query("SELECT * FROM users")
//    fun getAll(): List<User>
//
//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    fun insertAll(vararg users: User)
//
//    @Query("SELECT * FROM users WHERE id = :userId")
//    fun getUserById(userId: String): User?
//}
package com.example.travelog.dal.room.dao
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

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import com.example.travelog.models.UserEntity

@Dao
interface UserDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun deleteUser(user: UserEntity)

    @Query("SELECT * FROM users WHERE userId = :userId")
    suspend fun getUser(userId: String): UserEntity?

    @Query("SELECT * FROM users WHERE fullName LIKE :query OR email LIKE :query")
    suspend fun searchUsers(query: String): List<UserEntity>
}

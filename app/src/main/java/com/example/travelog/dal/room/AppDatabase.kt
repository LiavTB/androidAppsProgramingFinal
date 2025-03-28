package com.example.travelog.dal.room


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.travelog.dal.room.dao.ImageDao
import com.example.travelog.dal.room.dao.PostDao
import com.example.travelog.dal.room.dao.TripDao
import com.example.travelog.dal.room.dao.UserDao
import com.example.travelog.models.Image
import com.example.travelog.models.UserEntity
import com.example.travelog.models.TripEntity
import com.example.travelog.models.PostEntity

@Database(entities = [UserEntity::class, TripEntity::class, PostEntity::class, Image::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun tripDao(): TripDao
    abstract fun postDao(): PostDao
    abstract fun imageDao(): ImageDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context?): AppDatabase {
            requireNotNull(context?.applicationContext) { "context cant be null"}

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context?.applicationContext!!,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

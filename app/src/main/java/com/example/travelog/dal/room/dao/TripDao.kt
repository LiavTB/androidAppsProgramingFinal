package com.example.travelog.dal.room.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Update
import androidx.room.Delete
import androidx.room.Query
import com.example.travelog.models.TripEntity

@Dao
interface TripDao {
    @Insert(onConflict = androidx.room.OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: TripEntity)

    @Update
    suspend fun updateTrip(trip: TripEntity)

    @Delete
    suspend fun deleteTrip(trip: TripEntity)

    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTrip(tripId: Int): TripEntity?

    @Query("SELECT * FROM trips WHERE name LIKE :query OR destination LIKE :query")
    suspend fun searchTrips(query: String): List<TripEntity>

    @Query("SELECT * FROM trips WHERE userId = :userId")
    suspend fun getTripsByUserId(userId: String): List<TripEntity>
}

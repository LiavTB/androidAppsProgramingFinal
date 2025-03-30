package com.example.travelog.dal.repositories


import android.content.Context
import com.example.travelog.dal.room.AppDatabase
import com.example.travelog.models.TripEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TripRepository(private val context: Context) {

    private val firestore = FirebaseFirestore.getInstance()

    private val db = AppDatabase.getDatabase(context)

    suspend fun getTrip(tripId: String): TripEntity? {
        var trip = db.tripDao().getTrip(tripId)
        if (trip == null) {
            val snapshot = firestore.collection("trips").document(tripId.toString()).get().await()
            if (snapshot.exists()) {
                trip = snapshot.toObject(TripEntity::class.java)
                trip?.let { db.tripDao().insertTrip(it) }
            }
        }
        return trip
    }

    suspend fun updateTrip(trip: TripEntity) {
        firestore.collection("trips").document(trip.id.toString()).set(trip).await()
        db.tripDao().updateTrip(trip)
    }

    suspend fun deleteTrip(trip: TripEntity) {
        firestore.collection("trips").document(trip.id.toString()).delete().await()
        db.tripDao().deleteTrip(trip)
    }

    suspend fun addTrip(trip: TripEntity) {
        // Check if the trip already exists
        val existingTrip = db.tripDao().getTrip(trip.id)
        if (existingTrip != null) {
            // Update the existing trip
            firestore.collection("trips").document(trip.id.toString()).set(trip).await()
            db.tripDao().updateTrip(trip)
        } else {
            // Save to Firestore
            firestore.collection("trips").document(trip.id.toString()).set(trip).await()
            // Cache in local Room database
            db.tripDao().insertTrip(trip)
        }
    }

    suspend fun getTripsByUserId(userId: String): List<TripEntity> {
        var trips = db.tripDao().getTripsByUserId(userId)
        if (trips.isEmpty()) {
            val snapshot = firestore.collection("trips").whereEqualTo("userId", userId).get().await()
            trips = snapshot.toObjects(TripEntity::class.java)
            trips.forEach { db.tripDao().insertTrip(it) }
        }
        return trips
    }
}

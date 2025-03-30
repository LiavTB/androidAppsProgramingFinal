package com.example.travelog.ui.tripCreate


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.dal.repositories.TripRepository
import com.example.travelog.models.TripEntity
import com.example.travelog.utils.Time
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID

// Sealed class representing the trip creation state.
sealed class TripCreationState {
    object Idle : TripCreationState()
    object Loading : TripCreationState()
    object Success : TripCreationState()
    data class Error(val message: String?) : TripCreationState()
}

class TripCreateViewModel(application: Application) : AndroidViewModel(application) {

    private val _createTime: Long = Time.getEpochTime()
    // Two-way bound properties for user input.
    val tripName = MutableLiveData("")
    val tripDestination = MutableLiveData("")
    val tripStartDate = MutableLiveData(
        Time.formatEpochTime(_createTime)
    )
    val tripId = MutableLiveData("")

    // LiveData for the creation state.
    val tripCreationState = MutableLiveData<TripCreationState>(TripCreationState.Idle)

    val isLoading = MutableLiveData<Boolean>()

    private val tripRepository: TripRepository = TripRepository(application)

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    // Called when the user taps "Create Trip".
    fun createTrip() {
        // Validate fields.
        if (tripName.value.isNullOrBlank() ||
            tripDestination.value.isNullOrBlank() ||
            tripStartDate.value.isNullOrBlank()
        ) {
            tripCreationState.value = TripCreationState.Error("All fields are required.")
            return
        }

        tripCreationState.value = TripCreationState.Loading

        viewModelScope.launch {
            try {
                isLoading.value = true
                if (currentUserId.isNullOrBlank())
                    throw Exception("User not authenticated")

                val tripIdToUse = if (tripId.value.isNullOrBlank()) UUID.randomUUID().toString() else tripId.value!!

                // Create a new TripEntity with the user input
                val trip = TripEntity(
                    id = tripIdToUse,
                    name = tripName.value!!,
                    destination = tripDestination.value!!,
                    startDate = _createTime,
                    userId = currentUserId
                )

                // Save the trip using the repository
                tripRepository.addTrip(trip)

                // Update state on success
                tripCreationState.value = TripCreationState.Success
            } catch (e: Exception) {
                // Handle any errors
                tripCreationState.value = TripCreationState.Error(e.message)
            }
            finally {
                isLoading.value = false
            }
        }
    }
}

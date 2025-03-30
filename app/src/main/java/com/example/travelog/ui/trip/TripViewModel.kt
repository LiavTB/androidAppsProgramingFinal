package com.example.travelog.ui.trip

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.dal.repositories.TripRepository
import com.example.travelog.models.TripEntity
import kotlinx.coroutines.launch

// Sealed class for update state.
sealed class TripUpdateState {
    object Idle : TripUpdateState()
    object Loading : TripUpdateState()
    object Success : TripUpdateState()
    data class Error(val message: String?) : TripUpdateState()
}

class TripViewModel(application: Application) : AndroidViewModel(application) {

    val tripName = MutableLiveData<String>("")
    val tripDestination = MutableLiveData<String>("")
    val tripStartDate = MutableLiveData<String>("")
    val tripUpdateState = MutableLiveData<TripUpdateState>(TripUpdateState.Idle)

    private val tripRepository = TripRepository(application)
    private var currentTrip: TripEntity? = null

    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            tripRepository.getTrip(tripId)?.let { trip ->
                currentTrip = trip
                tripName.value = trip.name
                tripDestination.value = trip.destination
                tripStartDate.value = trip.startDate.toString() // Format as needed.
            }
        }
    }

    fun updateTrip() {
        if (tripName.value.isNullOrBlank() || tripDestination.value.isNullOrBlank()) {
            tripUpdateState.value = TripUpdateState.Error("All fields are required.")
            return
        }
        tripUpdateState.value = TripUpdateState.Loading
        viewModelScope.launch {
            try {
                currentTrip?.let { trip ->
                    val updatedTrip = trip.copy(
                        name = tripName.value!!,
                        destination = tripDestination.value!!
                    )
                    tripRepository.updateTrip(updatedTrip)
                    tripUpdateState.value = TripUpdateState.Success
                }
            } catch (e: Exception) {
                tripUpdateState.value = TripUpdateState.Error(e.message)
            }
        }
    }
}

package com.example.travelog.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.dal.repositories.TripRepository
import com.example.travelog.models.TripEntity
import kotlinx.coroutines.launch

class HomeFeedViewModel(application: Application) : AndroidViewModel(application) {

    private val _trips = MutableLiveData<List<TripEntity>>()
    val trips: LiveData<List<TripEntity>> get() = _trips

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val tripRepository = TripRepository(application)

    init {
        loadAllTrips()
    }

    private fun loadAllTrips() {
        _isLoading.value = true
        viewModelScope.launch {
            _trips.value = tripRepository.getAllTrips()
            _isLoading.value = false
        }
    }
}

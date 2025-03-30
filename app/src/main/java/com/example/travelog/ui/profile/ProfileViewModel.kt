package com.example.travelog.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.dal.repositories.TripRepository
import com.example.travelog.dal.repositories.UserRepository
import com.example.travelog.models.TripEntity
import com.example.travelog.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // Existing profile properties.
    private val _profilePicture = MutableLiveData<String>().apply { value = "" }
    val profilePicture: LiveData<String> get() = _profilePicture

    private val _fullName = MutableLiveData<String>().apply { value = "Your Name" }
    val profileName: LiveData<String> get() = _fullName

    // New property for the user's trips.
    private val _trips = MutableLiveData<List<TripEntity>>()
    val trips: LiveData<List<TripEntity>> get() = _trips

    // Navigation events.
    private val _navigateToAddTrip = MutableLiveData<Boolean?>()
    val navigateToAddTrip: LiveData<Boolean?> get() = _navigateToAddTrip

    private val _navigateToEditProfile = MutableLiveData<Boolean>()
    val navigateToEditProfile: LiveData<Boolean> get() = _navigateToEditProfile

    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    private val userRepository: UserRepository = UserRepository(application)
    private val tripRepository = TripRepository(application)
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadUser()
        loadUserTrips()
    }

    private fun loadUser() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val user: UserEntity? = userRepository.getUser(userId)
                user?.let {
                    _fullName.value = it.fullName
                    if (!it.profileImg.isNullOrBlank()) {
                        _profilePicture.value = it.profileImg
                    }
                }
            }
        }
    }

    // Load trips for the current user.
    private fun loadUserTrips() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val tripsList = tripRepository.getTripsByUserId(userId)
                _trips.value = tripsList
            }
        }
    }

    // Navigation functions.
    fun onEditProfileClicked() {
        _navigateToEditProfile.value = true
    }
    fun onEditProfileNavigated() {
        _navigateToEditProfile.value = false
    }
    fun onLogoutClicked() {
        FirebaseAuth.getInstance().signOut()
        _logoutEvent.value = true
    }
    fun onLogoutHandled() {
        _logoutEvent.value = false
    }
    fun onAddTripClicked() {
        _navigateToAddTrip.value = true
    }
    fun onAddTripNavigated() {
        _navigateToAddTrip.value = null
    }
}

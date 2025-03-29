package com.example.travelog.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.R
import com.example.travelog.dal.repositories.UserRepository
import com.example.travelog.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    // LiveData for the profile picture URL.
    // If no URL is available, the binding adapter will fall back to the placeholder.
    private val _profilePicture = MutableLiveData<String>().apply { value = "" }
    val profilePicture: LiveData<String> get() = _profilePicture

    // LiveData for the profile name.
    private val _fullName = MutableLiveData<String>().apply { value = "Your Name" }
    val profileName: LiveData<String> get() = _fullName

    private val _navigateToAddTrip = MutableLiveData<Boolean?>()

    private val userRepository: UserRepository =
        UserRepository(application)

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadUser()
    }

    // Load user data from the repository.
    private fun loadUser() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val user: UserEntity? = userRepository.getUser(userId)
                user?.let {
                    _fullName.value = it.fullName
                    // If a profile image URL exists, update _profilePicture.
                    if (!it.profileImg.isNullOrBlank()) {
                        _profilePicture.value = it.profileImg
                    }
                }
            }
        }
    }

    // LiveData event for navigating to the Edit Profile screen.
    private val _navigateToEditProfile = MutableLiveData<Boolean>()
    val navigateToEditProfile: LiveData<Boolean> get() = _navigateToEditProfile

    // LiveData event for navigating to the Login screen after logout.
    private val _logoutEvent = MutableLiveData<Boolean>()
    val logoutEvent: LiveData<Boolean> get() = _logoutEvent

    // Called when the Edit Profile button is clicked.
    fun onEditProfileClicked() {
        _navigateToEditProfile.value = true
    }

    // Called after navigation has been handled.
    fun onEditProfileNavigated() {
        _navigateToEditProfile.value = false
    }

    // Called when the Logout button is clicked.
    fun onLogoutClicked() {
        FirebaseAuth.getInstance().signOut()
        _logoutEvent.value = true
    }

    // Called after logout navigation is handled.
    fun onLogoutHandled() {
        _logoutEvent.value = false
    }

    val navigateToAddTrip: LiveData<Boolean?>
        get() = _navigateToAddTrip

    fun onAddTripClicked() {
        _navigateToAddTrip.value = true
    }

    fun onAddTripNavigated() {
        _navigateToAddTrip.value = null
    }
}

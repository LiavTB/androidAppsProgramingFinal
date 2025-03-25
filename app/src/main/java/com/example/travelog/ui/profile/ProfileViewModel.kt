package com.example.travelog.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.travelog.R
import com.google.firebase.auth.FirebaseAuth

class ProfileViewModel : ViewModel() {

    // LiveData for the profile picture (here using a drawable resource ID).
    private val _profilePicture = MutableLiveData<Int>().apply {
        value = R.drawable.profile_placeholder
    }
    val profilePicture: LiveData<Int> get() = _profilePicture

    // LiveData for the profile name.
    private val _profileName = MutableLiveData<String>().apply {
        value = "Your Name"
    }
    val profileName: LiveData<String> get() = _profileName

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
        // Sign out from Firebase. This clears the current user.
        FirebaseAuth.getInstance().signOut()
        // Trigger logout event to let the fragment know to navigate away.
        _logoutEvent.value = true
    }

    // Called after logout navigation is handled.
    fun onLogoutHandled() {
        _logoutEvent.value = false
    }
}

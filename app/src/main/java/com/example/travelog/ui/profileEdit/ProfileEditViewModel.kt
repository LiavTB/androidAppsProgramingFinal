package com.example.travelog.ui.profileEdit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.dal.repositories.UserRepository
import com.example.travelog.models.UserEntity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

// Sealed class representing the update state.
sealed class ProfileUpdateState {
    object Idle : ProfileUpdateState()
    object Loading : ProfileUpdateState()
    object Success : ProfileUpdateState()
    data class Error(val message: String?) : ProfileUpdateState()
}

class ProfileEditViewModel(application: Application) : AndroidViewModel(application) {

    // Two-way bound LiveData for the user's full name and profile image URL.
    val fullName = MutableLiveData("")
    val profileImg = MutableLiveData("")
    val email = MutableLiveData("")

    // LiveData to represent the update state (for progress, success, or error).
    val updateState = MutableLiveData<ProfileUpdateState>(ProfileUpdateState.Idle)

    private val userRepository: UserRepository =
        UserRepository(application)

    // Retrieve the current user ID from FirebaseAuth.
    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    init {
        loadUser()
    }

    // Load user data from the local repository (or Firebase if not cached).
    private fun loadUser() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                val user: UserEntity? = userRepository.getUser(userId)
                user?.let {
                    fullName.value = it.fullName
                    profileImg.value = it.profileImg
                    email.value = it.email
                }
            }
        }
    }

    // Update user data via the repository.
    fun updateUser() {
        currentUserId?.let { userId ->
            viewModelScope.launch {
                updateState.value = ProfileUpdateState.Loading
                try {
                    // Build an updated UserEntity using the values from the input fields.
                    val updatedUser = UserEntity(
                        userId = userId,
                        fullName = fullName.value ?: "",
                        profileImg = profileImg.value ?: "",
                        // Include other fields if needed (for example, email)
                        email = ""  // Adjust as necessary
                    )
                    // Update the user in Firebase, local cache, and Firebase Auth.
                    userRepository.updateUser(updatedUser)
                    updateState.value = ProfileUpdateState.Success
                } catch (e: Exception) {
                    updateState.value = ProfileUpdateState.Error(e.message)
                }
            }
        }
    }
}

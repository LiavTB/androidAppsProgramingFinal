package com.example.travelog.ui.register

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.models.UserEntity
import com.example.travelog.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import com.example.travelog.dal.repositories.UserRepository
import com.example.travelog.dal.repositories.ImageRepository

// Sealed class representing various registration states.
sealed class RegisterResult {
    object Idle : RegisterResult()
    object Loading : RegisterResult()
    object Success : RegisterResult()
    data class Error(val exception: Exception?) : RegisterResult()
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    // LiveData for user input
    val fullName = MutableLiveData("")
    val email = MutableLiveData("")
    val password = MutableLiveData("")
    val confirmPassword = MutableLiveData("")

    // Flags for input validation
    val isFullNameValid = MutableLiveData(true)
    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)
    val isConfirmPasswordValid = MutableLiveData(true)

    // Computed property to check if the entire form is valid
    val isFormValid: Boolean
        get() = isFullNameValid.value!! &&
                isEmailValid.value!! &&
                isPasswordValid.value!! &&
                isConfirmPasswordValid.value!!

    // Private mutable LiveData for registration state, exposed as immutable LiveData.
    private val _registerState = MutableLiveData<RegisterResult>(RegisterResult.Idle)
    val registerState: LiveData<RegisterResult> get() = _registerState

    // FirebaseAuth instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val userRepository: UserRepository =
        UserRepository(application)

    // NEW: LiveData to hold the Cloudinary URL of the uploaded profile image.
    val profileImageUrl = MutableLiveData<String?>()

    /**
     * Uploads the selected profile image to Cloudinary.
     * This function is image‑related.
     */
    fun uploadProfileImage(imageUri: Uri) {
        viewModelScope.launch {
            val resultUrl = ImageRepository.uploadImage(getApplication(), imageUri)
            profileImageUrl.value = resultUrl
        }
    }

    // Initiates the registration process.
    fun register() {
        validateForm()
        if (!isFormValid) {
            _registerState.value = RegisterResult.Error(null)
            return
        }

        // Ensure the password and confirm password match.
        if (password.value != confirmPassword.value) {
            _registerState.value = RegisterResult.Error(Exception("Passwords do not match"))
            return
        }

        _registerState.value = RegisterResult.Loading

        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(email.value!!, password.value!!).await()
                    val user = auth.currentUser

                    // Determine photoUri from the Cloudinary URL if available.
                    val photoUri = profileImageUrl.value?.let { Uri.parse(it) }

                    // Update the Firebase user profile with display name and photo URL.
                    user?.updateProfile(
                        UserProfileChangeRequest.Builder()
                            .setDisplayName(fullName.value)
                            .setPhotoUri(photoUri)
                            .build()
                    )?.await()

                    // Create and save user in repository using the Cloudinary URL (if any)
                    user?.let {
                        val userEntity = UserEntity(
                            userId = it.uid,
                            fullName = fullName.value ?: "",
                            email = email.value ?: "",
                            profileImg = photoUri?.toString() ?: ""
                        )
                        userRepository.addUser(userEntity)
                    }
                }
                withContext(Dispatchers.Main) {
                    _registerState.value = RegisterResult.Success
                }
            } catch (e: Exception) {
                Log.e("Register", "Error registering user", e)
                withContext(Dispatchers.Main) {
                    _registerState.value = RegisterResult.Error(e)
                }
            }
        }
    }

    // Validates the input fields using the static Validator.
    private fun validateForm() {
        isFullNameValid.value = !fullName.value.isNullOrBlank()
        isEmailValid.value = Validator.validateEmail(email.value ?: "")
        isPasswordValid.value = Validator.validatePassword(password.value ?: "")
        isConfirmPasswordValid.value = (confirmPassword.value == password.value && !confirmPassword.value.isNullOrBlank())
    }
}

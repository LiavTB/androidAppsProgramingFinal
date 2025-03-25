package com.example.travelog.ui.login

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travelog.utils.Validator
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// Sealed class representing various login states.
sealed class LoginResult {
    object Idle : LoginResult()
    object Loading : LoginResult()
    object Success : LoginResult()
    data class Error(val exception: Exception?) : LoginResult()
}

class LoginViewModel : ViewModel() {
    // LiveData to store user input for email and password.
    val email = MutableLiveData("")
    val password = MutableLiveData("")

    // LiveData flags to indicate if email and password are valid.
    val isEmailValid = MutableLiveData(true)
    val isPasswordValid = MutableLiveData(true)

    // Computed property to check if the form is valid.
    val isFormValid: Boolean
        get() = isEmailValid.value!! && isPasswordValid.value!!

    // Private mutable LiveData for login state; exposed as immutable LiveData.
    private val _loginState = MutableLiveData<LoginResult>(LoginResult.Idle)
    val loginState: LiveData<LoginResult> get() = _loginState

    // FirebaseAuth instance.
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()


    init {
        // If the user is already signed in, update login state immediately.
        if (auth.currentUser != null) {
            _loginState.value = LoginResult.Success
        }
    }

    // Function to initiate the login process.
    fun login() {
        // Validate the input fields.
        validateForm()
        if (!isFormValid) {
            _loginState.value = LoginResult.Error(null)
            return
        }

        // Update the login state to Loading to show progress.
        _loginState.value = LoginResult.Loading

        // Launch a coroutine to perform the Firebase sign-in.
        viewModelScope.launch {
            try {
                // Sign in using Firebase Auth on the IO dispatcher.
                withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email.value!!, password.value!!).await()
                }
                // On success, update login state on the Main dispatcher.
                withContext(Dispatchers.Main) {
                    _loginState.value = LoginResult.Success
                }
            } catch (e: Exception) {
                // Log error and update login state to Error on the Main dispatcher.
                Log.e("Login", "Error signing in user", e)
                withContext(Dispatchers.Main) {
                    _loginState.value = LoginResult.Error(e)
                }
            }
        }
    }

    // Function to validate email and password inputs.
    private fun validateForm() {
        isEmailValid.value = Validator.validateEmail(email.value!!)
        isPasswordValid.value = Validator.validatePassword(password.value!!)
    }
}

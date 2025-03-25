package com.example.travelog.utils

import android.util.Patterns

/**
 * A simple validator class to validate email and password inputs.
 */
class Validator {
    companion object {
        /**
         * Validates the email string using Android's built-in Patterns.
         * @return true if email is not empty and matches a valid email pattern.
         */
        @JvmStatic
        fun validateEmail(email: String): Boolean {
            return email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
        }

        /**
         * Validates the password.
         * @return true if password is not empty and at least 6 characters long.
         */
        @JvmStatic
        fun validatePassword(password: String): Boolean {
            return password.isNotEmpty() && password.length >= 6
        }
    }
}
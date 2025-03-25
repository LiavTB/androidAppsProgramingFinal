package com.example.travelog.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.travelog.databinding.FragmentLoginBinding
import com.example.travelog.R
import com.example.travelog.utils.Alert
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException

class Login : Fragment() {

    private val viewModel: LoginViewModel by viewModels()
    private lateinit var registerLink: View
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using Data Binding.
        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false
        )
        // Bind the ViewModel and lifecycle owner.
        bindViews(binding)
        // Observe changes in the login state.
        observeLoginState()
        // Setup the registration link.
        setupRegisterLink(binding)
        // Setup the login button click listener.
        setupLoginButton(binding)

        return binding.root
    }

    // Bind the ViewModel to the layout for data binding.
    private fun bindViews(binding: FragmentLoginBinding) {
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
    }

    // Observe the login state LiveData and update UI accordingly.
    private fun observeLoginState() {
        viewModel.loginState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is LoginResult.Loading -> {
                    // Show progress while the login process is running.
                    showProgressBar()
                }
                is LoginResult.Success -> {
                    // Hide progress, show success alert, and navigate to the feed fragment.
                    showLoginButton()
                    Alert("Login", "Login Successful", requireContext()).show()
                    // TODO - shpuld redirect to home page
                    findNavController().navigate(R.id.login_to_profile)
                }
                is LoginResult.Error -> {
                    // Hide progress and display an error message.
                    showLoginButton()
                    handleLoginError(result.exception)
                }
                else -> {
                    // Idle state; no action required.
                }
            }
        }
    }

    // Setup the registration link to navigate to the registration screen.
    private fun setupRegisterLink(binding: FragmentLoginBinding) {
        registerLink = binding.root.findViewById(R.id.register_link)
        registerLink.setOnClickListener {
            findNavController().navigate(R.id.login_to_register)
        }
    }

    // Setup the login button to trigger the login process in the ViewModel.
    private fun setupLoginButton(binding: FragmentLoginBinding) {
        loginButton = binding.root.findViewById(R.id.login_button)
        progressBar = binding.root.findViewById(R.id.progress_bar)
        loginButton.setOnClickListener {
            viewModel.login()
        }
    }

    // Handle specific login errors and display appropriate error messages.
    private fun handleLoginError(error: Exception?) {
        when (error) {
            is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                Alert("Login Error", "User not found", requireContext()).show()
            }
            else -> {
                Alert("Login Error", "An error occurred", requireContext()).show()
            }
        }
    }

    // Helper function to show the login button and hide the progress bar.
    private fun showLoginButton() {
        loginButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    // Helper function to hide the login button and show the progress bar.
    private fun showProgressBar() {
        loginButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }
}

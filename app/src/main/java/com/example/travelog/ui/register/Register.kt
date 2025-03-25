package com.example.travelog.ui.register

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
import com.example.travelog.databinding.FragmentRegisterBinding
import com.example.travelog.R
import com.example.travelog.utils.Alert


class Register : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using Data Binding.
        val binding: FragmentRegisterBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        // Bind the ViewModel and lifecycle owner.
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setupButtons(binding)
        observeRegisterState()

        return binding.root
    }

    // Set up click listeners for the buttons.
    private fun setupButtons(binding: FragmentRegisterBinding) {
        registerButton = binding.root.findViewById(R.id.btn_signup)
        loginButton = binding.root.findViewById(R.id.btn_login)
        progressBar = binding.root.findViewById(R.id.progress_bar)

        registerButton.setOnClickListener {
            viewModel.register()
        }
        loginButton.setOnClickListener {
            // Navigate back to the login fragment.
            findNavController().navigate(R.id.action_register_to_login)
        }
    }

    // Observe the registration state and update the UI accordingly.
    private fun observeRegisterState() {
        viewModel.registerState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is RegisterResult.Loading -> {
                    showProgressBar()
                }
                is RegisterResult.Success -> {
                    hideProgressBar()
                    Alert("Registration", "Registration Successful", requireContext()).show()
                    // Navigate to the feed fragment or wherever is appropriate. TODO - maybe redirect to home????
                    findNavController().navigate(R.id.action_register_to_login)
                }
                is RegisterResult.Error -> {
                    hideProgressBar()
                    // Customize error handling if needed.
                    val errorMessage = result.exception?.message ?: "Registration failed. Check your input."
                    Alert("Registration Error", errorMessage, requireContext()).show()
                }
                else -> {
                    // Idle state: no action required.
                }
            }
        }
    }

    // Helper to show the progress bar and hide the register button.
    private fun showProgressBar() {
        registerButton.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
    }

    // Helper to hide the progress bar and show the register button.
    private fun hideProgressBar() {
        registerButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }
}

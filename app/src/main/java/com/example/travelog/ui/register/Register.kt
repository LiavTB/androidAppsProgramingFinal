package com.example.travelog.ui.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView  // Import ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.travelog.R
import com.example.travelog.databinding.FragmentRegisterBinding
import com.example.travelog.utils.Alert

class Register : Fragment() {

    private val viewModel: RegisterViewModel by viewModels()
    private lateinit var registerButton: Button
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    // New: Reference to the profile image view.
    private lateinit var profileImageView: ImageView

    // ActivityResultLauncher for selecting an image.
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Get the image Uri from the returned intent
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                // Load the selected image into the profileImageView as preview
                Glide.with(requireContext()).load(it).into(profileImageView)
                // Call the ViewModel to upload the image to Cloudinary
                viewModel.uploadProfileImage(it)
            }
        }
    }

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

        // Set up non-image buttons.
        setupButtons(binding)

        // Initialize the profile image view and set its click listener for image selection.
        profileImageView = binding.root.findViewById(R.id.profileImageView)
        profileImageView.setOnClickListener {
            // Launch image picker intent
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }

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
                    // Navigate to the feed fragment or wherever is appropriate.
                    findNavController().navigate(R.id.action_register_to_login)
                }
                is RegisterResult.Error -> {
                    hideProgressBar()
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

package com.example.travelog.ui.profileEdit

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.travelog.R
import com.example.travelog.databinding.FragmentEditProfileBinding
import com.example.travelog.utils.Alert

class ProfileEdit : Fragment() {

    private val viewModel: ProfileEditViewModel by viewModels()
    private lateinit var profileImageView: ImageView

    // ActivityResultLauncher for selecting an image.
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                // Load the selected image into the profileImageView as preview.
                Glide.with(requireContext()).load(it).into(profileImageView)
                // Call the ViewModel to upload/update the profile image.
                viewModel.updateProfileImage(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using data binding.
        val binding: FragmentEditProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_profile, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Find the ImageView and set a click listener for image selection.
        profileImageView = binding.root.findViewById(R.id.profileImageView)
        profileImageView.setOnClickListener {
            // Launch image picker intent.
            val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
            imagePickerLauncher.launch(intent)
        }

        // When the Save button is clicked, call updateUser in the ViewModel.
        binding.btnSave.setOnClickListener {
            viewModel.updateUser()
        }

        // Observe update state changes to show progress, display errors, or navigate on success.
        viewModel.updateState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ProfileUpdateState.Loading -> {
                    // Optionally show a loading indicator.
                }
                is ProfileUpdateState.Success -> {
                    Alert("Profile", "Profile updated successfully", requireContext()).show()
                    findNavController().navigate(R.id.action_editProfile_to_profile)
                }
                is ProfileUpdateState.Error -> {
                    Alert("Error", state.message ?: "An error occurred", requireContext()).show()
                }
                else -> { /* Idle state – do nothing */ }
            }
        })

        return binding.root
    }
}

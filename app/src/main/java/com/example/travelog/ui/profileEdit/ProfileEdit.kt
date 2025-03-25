package com.example.travelog.ui.profileEdit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.travelog.R
import com.example.travelog.databinding.FragmentEditProfileBinding
import com.example.travelog.utils.Alert

class ProfileEditFragment : Fragment() {

    private val viewModel: ProfileEditViewModel by viewModels()

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

        // When the Save button is clicked, call updateUser in the ViewModel.
        binding.btnSave.setOnClickListener {
            viewModel.updateUser()
        }

        // Observe update state changes to show progress, display errors, or navigate on success.
        viewModel.updateState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ProfileUpdateState.Loading -> {
                    // Optionally show a progress indicator.
                }
                is ProfileUpdateState.Success -> {
                    // Optionally show a success message.
                    Alert("Profile", "Profile updated successfully", requireContext()).show()
                    // Navigate back to the Profile screen.
                    findNavController().navigate(R.id.action_editProfile_to_profile)
                }
                is ProfileUpdateState.Error -> {
                    // Display an error alert.
                    Alert("Error", state.message ?: "An error occurred", requireContext()).show()
                }
                else -> { /* Idle state – do nothing */ }
            }
        })

        return binding.root
    }
}

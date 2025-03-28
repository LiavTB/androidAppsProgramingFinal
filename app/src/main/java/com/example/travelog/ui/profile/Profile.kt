package com.example.travelog.ui.profile

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
import com.example.travelog.databinding.FragmentProfileBinding

class Profile : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate layout using data binding.
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Observe the event for editing the profile.
        viewModel.navigateToEditProfile.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate == true) {

                findNavController().navigate(R.id.action_profile_to_editProfile)
                viewModel.onEditProfileNavigated()
            }
        })

        // Observe the logout event.
        viewModel.logoutEvent.observe(viewLifecycleOwner, Observer { logout ->
            if (logout == true) {
                // Navigate to the login screen after logout.
                findNavController().navigate(R.id.action_profile_to_login)
                viewModel.onLogoutHandled()
            }
        })

        viewModel.navigateToAddTrip.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate == true) {
                findNavController().navigate(R.id.action_profile_to_addTrip)
                viewModel.onAddTripNavigated()
            }
        })

        return binding.root
    }
}

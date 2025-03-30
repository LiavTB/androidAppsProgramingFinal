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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelog.R
import com.example.travelog.databinding.FragmentProfileBinding

class Profile : Fragment() {

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Initialize the TripAdapter (using the unified item layout) with empty data.
        val adapter = TripAdapter(
            trips = emptyList(),
            onTripClicked = { trip ->
                // When a trip is tapped, navigate to the Trip Detail page.
                val bundle = Bundle().apply { putString("tripId", trip.id) }
                findNavController().navigate(R.id.action_profile_to_trip_detail, bundle)
            },
            onEditClicked = { /* Not used here because editing is inside Trip Detail */ }
        )
        binding.rvProfileTrips.adapter = adapter

        // Set the LayoutManager.
        binding.rvProfileTrips.layoutManager = LinearLayoutManager(requireContext())

        // Observe trips LiveData.
        viewModel.trips.observe(viewLifecycleOwner) { trips ->
            adapter.updateTrips(trips)
        }

        // Existing navigation events for Edit Profile, Add Trip, Logout.
        viewModel.navigateToEditProfile.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate == true) {
                findNavController().navigate(R.id.action_profile_to_editProfile)
                viewModel.onEditProfileNavigated()
            }
        })
        viewModel.logoutEvent.observe(viewLifecycleOwner, Observer { logout ->
            if (logout == true) {
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

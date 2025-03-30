package com.example.travelog.ui.tripCreate

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
import com.example.travelog.databinding.FragmentTripCreateBinding
import com.example.travelog.utils.Alert

class TripCreate : Fragment() {

    private val viewModel: TripCreateViewModel by viewModels()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentTripCreateBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_trip_create, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner


        // Retrieve the data from the bundle
        arguments?.let {
            viewModel.tripName.value = it.getString("tripName")
            viewModel.tripDestination.value = it.getString("tripDestination")
            viewModel.tripId.value = it.getString("tripId")
            viewModel.tripStartDate.value = it.getString("tripDate")
        }


        // Set up the create trip button click.
        binding.btnCreateTrip.setOnClickListener {
            viewModel.createTrip()
        }

        // Observe the trip creation state.
        viewModel.tripCreationState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is TripCreationState.Loading -> {
                }
                is TripCreationState.Success -> {
                    Alert("Trip", "Trip created successfully", requireContext()).show()
                    // Navigate to a trip list or details screen.
                    findNavController().navigate(R.id.action_tripCreate_to_profile)
                }
                is TripCreationState.Error -> {
                    Alert("Error", state.message ?: "An error occurred", requireContext()).show()
                }
                else -> { /* Idle state, do nothing */ }
            }
        })

        return binding.root
    }
}

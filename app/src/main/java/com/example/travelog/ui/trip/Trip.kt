package com.example.travelog.ui.trip

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.fragment.app.Fragment
import com.example.travelog.databinding.FragmentTripViewBinding

class Trip : Fragment() {

    private lateinit var binding: FragmentTripViewBinding
    private lateinit var viewModel: TripViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTripViewBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(TripViewModel::class.java)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Get tripId from arguments and load the trip.
        val tripId = arguments?.getString("tripId")
        tripId?.let { viewModel.loadTrip(it) }

        binding.btnUpdateTrip.setOnClickListener {
            viewModel.updateTrip()
        }

        // Observe update state to handle success or errors (implementation details as needed).
        viewModel.tripUpdateState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is TripUpdateState.Success -> {
                    // Optionally show success alert and navigate back.
                }
                is TripUpdateState.Error -> {
                    // Optionally show error alert.
                }
                else -> { /* No action needed */ }
            }
        }
        return binding.root
    }
}

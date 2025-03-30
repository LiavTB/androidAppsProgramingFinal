package com.example.travelog.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.travelog.R
import com.example.travelog.databinding.FragmentHomeBinding
import com.example.travelog.ui.profile.TripAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeFeedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Set up TripAdapter
        val tripAdapter = TripAdapter(
            trips = emptyList(),
            onTripClicked = { trip ->
                val bundle = Bundle().apply { putString("tripId", trip.id) }
                findNavController().navigate(R.id.action_home_to_trip_detail, bundle)
            },
            onEditClicked = { /* handle edit if needed */ }
        )
        binding.rvHomeTrips.apply {
            adapter = tripAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        // Observe trips and update adapter.
        viewModel.trips.observe(viewLifecycleOwner) { trips ->
            tripAdapter.updateTrips(trips)
        }

        // Observe loading status to show/hide the ProgressBar.
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

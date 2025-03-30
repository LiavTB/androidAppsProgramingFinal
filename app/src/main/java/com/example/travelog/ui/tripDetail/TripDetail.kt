package com.example.travelog.ui.tripDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.travelog.R
import com.example.travelog.databinding.FragmentTripDetailBinding
import com.example.travelog.utils.Time

class TripDetailFragment : Fragment() {

    private lateinit var binding: FragmentTripDetailBinding
    private lateinit var viewModel: TripDetailViewModel
    private lateinit var postAdapter: PostAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTripDetailBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[TripDetailViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Get the tripId from arguments.
        val tripId = arguments?.getString("tripId")
        tripId?.let { viewModel.loadTripAndPosts(it) }

        // Setup posts RecyclerView.
        postAdapter = PostAdapter(emptyList(),viewModel.tripName.value ?: "")
        binding.rvTripPosts.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }

        // Observe posts LiveData.
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            val currentTripName = viewModel.tripName.value ?: ""
            postAdapter.updatePosts(posts, currentTripName)
        }

        // Show/hide the Edit button based on ownership.
        viewModel.isOwner.observe(viewLifecycleOwner) { owner ->
            binding.btnDetailEditTrip.visibility = if (owner) View.VISIBLE else View.GONE
        }

        // Edit button action.
        binding.btnDetailEditTrip.setOnClickListener {
            val bundle = Bundle().apply {
                putString("tripName", viewModel.tripName.value)
                putString("tripDestination", viewModel.tripDestination.value)
                putString("tripDate", viewModel.tripDate.value)
                putString("tripId", viewModel.localTripId.value)
            }
            findNavController().navigate(R.id.action_tripDetail_to_trip_create, bundle)
        }

        return binding.root
    }
}

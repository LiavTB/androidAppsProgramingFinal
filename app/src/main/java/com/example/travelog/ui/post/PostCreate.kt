package com.example.travelog.ui.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.travelog.R
import com.example.travelog.databinding.FragmentPostCreateBinding
import com.example.travelog.utils.Alert
import com.google.android.material.chip.Chip

class PostCreate : Fragment() {

    private val viewModel: PostCreateViewModel by viewModels()

    // Image picker launcher to select a photo.
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val imageUri: Uri? = result.data?.data
            imageUri?.let {
                viewModel.onPhotoSelected(it)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding: FragmentPostCreateBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post_create, container, false
        )
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        // Check for editing arguments.
        arguments?.let {
            if (it.containsKey("postId")) {
                val post = com.example.travelog.models.PostEntity(
                    id = it.getString("postId")!!,
                    description = it.getString("description")!!,
                    photo = it.getString("photo")!!,
                    locationTag = it.getStringArrayList("locationTags")?.toList() ?: listOf(),
                    tripId = it.getString("tripId")!!,
                    userId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: "",
                    date = it.getLong("date", com.example.travelog.utils.Time.getEpochTime())
                )
                viewModel.initForEdit(post)
            }
        }

        // Setup the AutoCompleteTextView for location query.
        val actvLocation = binding.root.findViewById<AutoCompleteTextView>(R.id.actv_location)
        val locationAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            mutableListOf<String>()
        )
        actvLocation.setAdapter(locationAdapter)
        actvLocation.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.fetchLocationSuggestions(s.toString())
            }
        })
        viewModel.locationSuggestions.observe(viewLifecycleOwner, Observer { suggestions ->
            locationAdapter.clear()
            locationAdapter.addAll(suggestions)
            locationAdapter.notifyDataSetChanged()
        })
        actvLocation.setOnItemClickListener { _, _, position, _ ->
            val selected = locationAdapter.getItem(position)
            actvLocation.setText(selected)
            viewModel.locationQuery.value = selected
        }

        viewModel.selectedLocationTags.observe(viewLifecycleOwner, Observer { tags ->
            val chipGroup = binding.root.findViewById<com.google.android.material.chip.ChipGroup>(R.id.cg_location_tags)
            chipGroup.removeAllViews()
            tags.forEach { tag ->
                val chip = Chip(requireContext())
                chip.text = tag
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    val currentTags = viewModel.selectedLocationTags.value ?: mutableListOf()
                    currentTags.remove(tag)
                    viewModel.selectedLocationTags.value = currentTags
                }
                chipGroup.addView(chip)
            }
        })

        val spinnerTrip = binding.root.findViewById<Spinner>(R.id.spinner_trip)
        val tripAdapter = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_spinner_item,
            mutableListOf()
        )
        tripAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTrip.adapter = tripAdapter
        viewModel.userTrips.observe(viewLifecycleOwner, Observer { trips ->
            tripAdapter.clear()
            trips.forEach { trip ->
                tripAdapter.add(trip.name)
            }
            tripAdapter.notifyDataSetChanged()
        })
        spinnerTrip.setOnItemSelectedListener(object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: android.widget.AdapterView<*>?) {
                viewModel.selectedTrip.value = null
            }
            override fun onItemSelected(
                parent: android.widget.AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val trips = viewModel.userTrips.value ?: listOf()
                if (trips.isNotEmpty()) {
                    viewModel.selectedTrip.value = trips[position]
                }
            }
        })

        viewModel.selectPhotoEvent.observe(viewLifecycleOwner, Observer { event ->
            if (event == true) {
                val intent = Intent(Intent.ACTION_PICK).apply { type = "image/*" }
                imagePickerLauncher.launch(intent)
            }
        })

        viewModel.publishState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is PostPublishState.Loading -> { }
                is PostPublishState.Success -> {
                    Alert("Post", "Post published successfully", requireContext()).show()
                    findNavController().popBackStack()  // Return to the previous screen.
                }
                is PostPublishState.Error -> {
                    Alert("Error", state.message ?: "An error occurred", requireContext()).show()
                }
                else -> {}
            }
        })

        return binding.root
    }
}

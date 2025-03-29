package com.example.travelog.ui.post

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.travelog.dal.repositories.PostRepository
import com.example.travelog.dal.repositories.TripRepository
import com.example.travelog.models.PostEntity
import com.example.travelog.models.TripEntity
import com.example.travelog.utils.Time
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.UUID
import com.example.travelog.dal.repositories.ImageRepository  // New import for Cloudinary
import com.example.travelog.dal.services.NominationApi

// Sealed class representing the publish post state.
sealed class PostPublishState {
    object Idle : PostPublishState()
    object Loading : PostPublishState()
    object Success : PostPublishState()
    data class Error(val message: String?) : PostPublishState()
}

class PostCreateViewModel(application: Application) : AndroidViewModel(application) {

    // Two-way bound properties for post inputs.
    val description = MutableLiveData("")
    val selectedPhotoUri = MutableLiveData<String>("")

    // For location tags: current query and selected tags.
    val locationQuery = MutableLiveData("")
    val selectedLocationTags = MutableLiveData<MutableList<String>>(mutableListOf())
    val locationSuggestions = MutableLiveData<List<String>>(listOf())

    // List of user's trips and the selected trip.
    val userTrips = MutableLiveData<List<TripEntity>>(listOf())
    val selectedTrip = MutableLiveData<TripEntity?>(null)
    val selectedTripPosition = MutableLiveData<Int>(-1)

    // LiveData for the publishing state.
    val publishState = MutableLiveData<PostPublishState>(PostPublishState.Idle)

    // Event to trigger photo selection.
    val selectPhotoEvent = MutableLiveData<Boolean>(false)

    val isLoading = MutableLiveData<Boolean>()

    private val currentUserId: String? = FirebaseAuth.getInstance().currentUser?.uid

    private val tripRepository: TripRepository = TripRepository(application)
    private val postRepository: PostRepository = PostRepository(application)

    init {
        loadUserTrips()
    }

    // Called when the "Select Photo" button is clicked.
    fun onSelectPhotoClicked() {
        selectPhotoEvent.value = true
    }

    // --- Updated: Called after a photo is selected.
    // Now accepts a Uri and uses the CloudinaryRepository to upload the image.
    fun onPhotoSelected(imageUri: Uri) {
        viewModelScope.launch {
            val uploadedUrl = ImageRepository.uploadImage(getApplication(), imageUri)
            if (uploadedUrl != null) {
                selectedPhotoUri.value = uploadedUrl
            }
        }
    }

    // Fetch autocomplete suggestions from the Nominatim API.
    fun fetchLocationSuggestions(query: String) {
        viewModelScope.launch {
            locationSuggestions.value = NominationApi.autoCompleteName(query)
        }
    }

    // Adds the current location query as a tag.
    fun addLocationTag() {
        val query = locationQuery.value?.trim() ?: ""
        if (query.isNotEmpty()) {
            val currentTags = selectedLocationTags.value ?: mutableListOf()
            if (!currentTags.contains(query)) {
                currentTags.add(query)
                selectedLocationTags.value = currentTags
            }
            locationQuery.value = ""
        }
    }

    // Load user's trips.
    private fun loadUserTrips() {
        viewModelScope.launch {
            tripRepository.getTripsByUserId(currentUserId ?: "").let { trips ->
                userTrips.value = trips
            }
        }
    }

    // Method called when a trip is selected in the spinner.
    fun onTripSelected(position: Int) {
        if (position >= 0 && position < (userTrips.value?.size ?: 0)) {
            selectedTripPosition.value = position
            selectedTrip.value = userTrips.value?.get(position)
        }
    }

    // Called when the "Publish" button is tapped.
    fun publishPost() {
        try {
            isLoading.value = true

            if (description.value.isNullOrBlank() ||
                selectedPhotoUri.value.isNullOrBlank() ||   // Now image is required
                selectedLocationTags.value.isNullOrEmpty() ||
                selectedTrip.value == null
            ) {
                publishState.value =
                    PostPublishState.Error("Please fill in all fields, add at least one location tag, select a photo, and select a trip.")
                return
            }
            publishState.value = PostPublishState.Loading
            viewModelScope.launch {

                val post = PostEntity(
                    id = UUID.randomUUID().toString(),
                    description = description.value!!,
                    photo = selectedPhotoUri.value!!,
                    locationTag = selectedLocationTags.value!!.toList(),
                    tripId = selectedTrip.value!!.id,
                    userId = currentUserId!!,
                    date = Time.getEpochTime()
                )

                postRepository.addPost(post)
                publishState.value = PostPublishState.Success
            }
        } catch (e: Exception) {
            publishState.value = PostPublishState.Error(e.message)
        } finally {
            isLoading.value = false
        }
    }
}

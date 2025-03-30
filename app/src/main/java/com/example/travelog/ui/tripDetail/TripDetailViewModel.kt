package com.example.travelog.ui.tripDetail

import android.app.Application
import androidx.lifecycle.*
import com.example.travelog.dal.repositories.PostRepository
import com.example.travelog.dal.repositories.TripRepository
import com.example.travelog.dal.repositories.UserRepository
import com.example.travelog.models.PostEntity
import com.example.travelog.models.TripEntity
import com.example.travelog.models.UserEntity
import com.example.travelog.utils.Time
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class TripDetailViewModel(application: Application) : AndroidViewModel(application) {

    val tripName = MutableLiveData("")
    val tripDestination = MutableLiveData("")
    val tripDate = MutableLiveData("")
    val localTripId = MutableLiveData("")
    val isOwner = MutableLiveData(false)
    val userData = MutableLiveData<UserEntity>()

    private val _posts = MutableLiveData<List<PostEntity>>()
    val posts: LiveData<List<PostEntity>> get() = _posts

    private val tripRepo = TripRepository(application)
    private val postRepo = PostRepository(application)
    private val userRepo = UserRepository(application)

    fun loadTripAndPosts(tripId: String) {
        viewModelScope.launch {
            // Load trip details.
            val trip: TripEntity? = tripRepo.getTrip(tripId)
            trip?.let {
                tripName.value = it.name
                tripDestination.value = it.destination ?: ""
                tripDate.value = Time.formatEpochTime(it.startDate)
                isOwner.value = (it.userId == FirebaseAuth.getInstance().currentUser?.uid)
                localTripId.value = it.id

                val user = userRepo.getUser(it.userId)
                userData.value = user ?: UserEntity()
            }
            // Load posts for this trip.
            // If your PostRepository does not have a getPostsByTripId, you can filter from getAllPosts().
            val tripPosts = postRepo.getAllPostsByTrip(tripId)
            _posts.value = tripPosts
        }
    }

    fun deletePost(post: PostEntity) {
        viewModelScope.launch {
            try {
                // Call the repository to delete the post.
                postRepo.deletePost(post)
                // Reload the posts for the current trip.
                localTripId.value?.let { tripId ->
                    _posts.value = postRepo.getAllPostsByTrip(tripId)
                }
            } catch (e: Exception) {
                // Optionally handle error, e.g. show a message.
                e.printStackTrace()
            }
        }
    }
}

package com.example.travelog.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelog.databinding.ItemTripBinding
import com.example.travelog.models.TripEntity
import com.google.firebase.auth.FirebaseAuth

class TripAdapter(
    private var trips: List<TripEntity>,
    private val onTripClicked: (TripEntity) -> Unit,
    private val onEditClicked: (TripEntity) -> Unit
) : RecyclerView.Adapter<TripAdapter.TripViewHolder>() {

    inner class TripViewHolder(val binding: ItemTripBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TripViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTripBinding.inflate(inflater, parent, false)
        return TripViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TripViewHolder, position: Int) {
        val trip = trips[position]
        holder.binding.trip = trip
        holder.binding.root.setOnClickListener { onTripClicked(trip) }
//        // Show edit button only if the current user is the owner.
//        if (trip.userId == FirebaseAuth.getInstance().currentUser?.uid) {
//            holder.binding.btnEditTrip.visibility = View.VISIBLE
//            holder.binding.btnEditTrip.setOnClickListener { onEditClicked(trip) }
//        } else {
//            holder.binding.btnEditTrip.visibility = View.GONE
//        }
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = trips.size

    // Method to update adapter data.
    fun updateTrips(newTrips: List<TripEntity>) {
        trips = newTrips
        notifyDataSetChanged()
    }
}

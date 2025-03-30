package com.example.travelog.ui.tripDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelog.databinding.ItemPostBinding
import com.example.travelog.models.PostEntity
import com.example.travelog.models.UserEntity

class PostAdapter(
    private var posts: List<PostEntity>,
    private var parentTripName: String,
    private var userData: UserEntity,
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    inner class PostViewHolder(val binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPostBinding.inflate(inflater, parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.binding.post = post
        holder.binding.tripName = parentTripName
        holder.binding.userData = userData
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<PostEntity>, newParentTripName: String, userDataa: UserEntity) {
        posts = newPosts
        parentTripName = newParentTripName
        userData = userDataa
        notifyDataSetChanged()
    }
}

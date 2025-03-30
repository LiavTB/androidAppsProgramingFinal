package com.example.travelog.ui.tripDetail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.travelog.databinding.ItemPostBinding
import com.example.travelog.models.PostEntity

class PostAdapter(
    private var posts: List<PostEntity>,
    private var parentTripName: String,
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
        holder.binding.executePendingBindings()
    }

    override fun getItemCount(): Int = posts.size

    fun updatePosts(newPosts: List<PostEntity>, newParentTripName: String) {
        posts = newPosts
        parentTripName = newParentTripName
        notifyDataSetChanged()
    }
}

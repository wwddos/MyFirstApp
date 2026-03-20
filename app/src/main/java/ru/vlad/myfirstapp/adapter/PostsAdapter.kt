package ru.vlad.myfirstapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import ru.vlad.myfirstapp.databinding.CardPostBinding
import ru.vlad.myfirstapp.dto.Post

class PostsAdapter(
    private val onLikeClickListener: (Post) -> Unit,
    private val onShareClickListener: (Post) -> Unit
) : ListAdapter<Post, PostViewHolder>(PostDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding, onLikeClickListener, onShareClickListener)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position)  // getItem предоставляет ListAdapter
        holder.bind(post)
    }
}

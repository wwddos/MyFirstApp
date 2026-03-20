package ru.vlad.myfirstapp.adapter
import androidx.recyclerview.widget.DiffUtil
import ru.vlad.myfirstapp.dto.Post

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {

    // Проверяем, являются ли два поста одной и той же сущностью (по id)
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    // Проверяем, одинаковое ли у них содержимое (все поля)
    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}


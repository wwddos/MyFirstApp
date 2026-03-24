package ru.vlad.myfirstapp.adapter

import android.view.View
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import ru.vlad.myfirstapp.R
import ru.vlad.myfirstapp.databinding.CardPostBinding
import ru.vlad.myfirstapp.dto.Post
import java.text.DecimalFormat

class PostViewHolder(
    private val binding: CardPostBinding,
    private val listener: OnPostInteractionListener
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published
            content.text = post.content

            likeCount.text = formatCount(post.likes)
            shareCount.text = formatCount(post.shares)
            viewsCount.text = formatCount(post.views)

            // Устанавливаем иконку лайка
            like.setImageResource(
                if (post.likedByMe) R.drawable.ic_favorite
                else R.drawable.ic_favorite_border
            )

            // Обработчики кликов
            like.setOnClickListener {
                listener.onLike(post)
            }

            share.setOnClickListener {
                listener.onShare(post)
            }

            avatar.setOnClickListener {
                listener.onAvatarClick(post)
            }

            // Кнопка меню с PopupMenu
            menu.setOnClickListener { view ->
                showPopupMenu(view, post)
            }
        }
    }

    private fun showPopupMenu(anchor: View, post: Post) {
        PopupMenu(anchor.context, anchor).apply {
            // Загружаем меню из ресурса
            inflate(R.menu.post_menu)

            // Обрабатываем выбор пунктов
            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.edit -> {
                        listener.onEdit(post)
                        true
                    }
                    R.id.remove -> {
                        listener.onRemove(post)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }

    private fun formatCount(count: Int): String {
        return when {
            count >= 1_000_000 -> {
                val millions = count / 1_000_000.0
                if (millions % 1.0 == 0.0) {
                    "${millions.toInt()}M"
                } else {
                    DecimalFormat(".").format(millions) + "M"
                }
            }
            count >= 10_000 -> "${count / 1000}K"
            count >= 1_000 -> {
                val thousands = count / 1000.0
                if (thousands % 1.0 == 0.0) {
                    "${thousands.toInt()}K"
                } else {
                    DecimalFormat(".").format(thousands) + "K"
                }
            }
            else -> count.toString()
        }
    }
}



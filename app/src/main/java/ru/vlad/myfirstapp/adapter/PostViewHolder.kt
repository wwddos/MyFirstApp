package ru.vlad.myfirstapp.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import ru.vlad.myfirstapp.R
import ru.vlad.myfirstapp.databinding.CardPostBinding
import ru.vlad.myfirstapp.databinding.ItemVideoBinding
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

            like.isChecked = post.likedByMe
            like.text = formatCount(post.likes)
            share.text = formatCount(post.shares)
            views.text = formatCount(post.views)

            // Обработка видео
            if (post.video.isNullOrBlank()) {
                // Если видео нет, скрываем контейнер
                videoContainer.removeAllViews()
                videoContainer.visibility = View.GONE
            } else {
                // Если видео есть, показываем контейнер и наполняем его
                videoContainer.visibility = View.VISIBLE
                videoContainer.removeAllViews()

                // Инфлейтим layout видео
                val videoBinding = ItemVideoBinding.inflate(
                    LayoutInflater.from(itemView.context),
                    videoContainer,
                    true
                )

                // Устанавливаем текст видео (можно показать короткую ссылку)
                videoBinding.videoUrl.text = post.video

                // Обработка клика на весь блок видео
                videoContainer.setOnClickListener {
                    openVideo(post.video!!)
                }
            }

            // Обработчики кликов
            like.setOnClickListener { listener.onLike(post) }
            share.setOnClickListener { listener.onShare(post) }
            avatar.setOnClickListener { listener.onAvatarClick(post) }

            // Кнопка меню
            menu.setOnClickListener { view ->
                showPopupMenu(view, post)
            }
            root.setOnClickListener {
                listener.onPostClick(post)
            }

            // Обработчики для интерактивных элементов должны вызывать stopPropagation
            // чтобы не срабатывал клик на root
            like.setOnClickListener {
                listener.onLike(post)
                it.stopPropagation()  // предотвращаем всплытие события
            }

            share.setOnClickListener {
                listener.onShare(post)
                it.stopPropagation()
            }

            avatar.setOnClickListener {
                listener.onAvatarClick(post)
                it.stopPropagation()
            }

            menu.setOnClickListener { view ->
                showPopupMenu(view, post)
                // menu не должен вызывать onPostClick
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

    private fun openVideo(videoUrl: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl))
            // Проверяем, есть ли приложение, которое может обработать этот Intent
            if (intent.resolveActivity(itemView.context.packageManager) != null) {
                itemView.context.startActivity(intent)
            } else {
                Toast.makeText(itemView.context, R.string.error_no_video_app, Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(itemView.context, R.string.error_cannot_open_video, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun View.stopPropagation() {
        isClickable = true
        setOnClickListener { }
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







package ru.vlad.myfirstapp
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import ru.vlad.myfirstapp.adapter.PostsAdapter
import ru.vlad.myfirstapp.databinding.ActivityMainBinding
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: PostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Создаем адаптер с обработчиками
        val adapter = PostsAdapter(
            onLikeClickListener = { post ->
                viewModel.likeById(post.id)
                Toast.makeText(this, "Лайк поста ${post.id}", Toast.LENGTH_SHORT).show()
            },
            onShareClickListener = { post ->
                viewModel.shareById(post.id)
                Toast.makeText(this, "Репост поста ${post.id}", Toast.LENGTH_SHORT).show()
            }
        )

        // Устанавливаем адаптер для RecyclerView
        binding.list.adapter = adapter

        // Наблюдаем за изменениями данных
        viewModel.data.observe(this) { posts ->
            // submitList сам вызовет DiffUtil и обновит только изменившиеся элементы
            adapter.submitList(posts)
        }
    }
}


package ru.vlad.myfirstapp
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import ru.vlad.myfirstapp.adapter.OnPostInteractionListener
import ru.vlad.myfirstapp.adapter.PostsAdapter
import ru.vlad.myfirstapp.databinding.ActivityMainBinding
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.viewmodel.PostViewModel

class MainActivity : AppCompatActivity() {

        private lateinit var binding: ActivityMainBinding
        private val viewModel: PostViewModel by viewModels()


        private var editingPostId: Long = 0L

        private val interactionListener = object : OnPostInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post.id)
            }

            override fun onShare(post: Post) {
                viewModel.shareById(post.id)
                Toast.makeText(this@MainActivity, "Репост +1", Toast.LENGTH_SHORT).show()
            }

            override fun onEdit(post: Post) {

                editingPostId = post.id

                binding.content.setText(post.content)
                binding.content.setSelection(binding.content.text.length)

                binding.content.requestFocus()
                showKeyboard(binding.content)

                binding.cancelGroup.visibility = View.VISIBLE
            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
                Toast.makeText(this@MainActivity, "Пост удален", Toast.LENGTH_SHORT).show()
            }

            override fun onAvatarClick(post: Post) {
                Toast.makeText(this@MainActivity, "Профиль: ${post.author}", Toast.LENGTH_SHORT)
                    .show()
                viewModel.increaseViews(post.id)
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)

            val adapter = PostsAdapter(interactionListener)
            binding.list.adapter = adapter

            viewModel.data.observe(this) { posts ->
                adapter.submitList(posts)
            }

            binding.content.addTextChangedListener { text ->

                viewModel.changeContent(text.toString())
            }

            // Кнопка сохранения
            binding.save.setOnClickListener {
                val text = binding.content.text.toString()
                if (text.isBlank()) {
                    Toast.makeText(this, "Введите текст поста", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                // Если редактируем существующий пост
                if (editingPostId != 0L) {
                    // Получаем текущий пост из ViewModel, обновляем его контент и сохраняем
                    viewModel.saveEditedPost(editingPostId, text)
                    editingPostId = 0L
                } else {
                    // Создаем новый пост
                    viewModel.changeContent(text)
                    viewModel.save()
                }

                // Очищаем поле ввода
                binding.content.text.clear()
                // Скрываем панель отмены
                binding.cancelGroup.visibility = View.GONE
                // Скрываем клавиатуру
                hideKeyboard(binding.content)
            }

            // Кнопка отмены редактирования
            binding.cancel.setOnClickListener {
                // Очищаем ID редактируемого поста
                editingPostId = 0L
                // Очищаем поле ввода
                binding.content.text.clear()
                // Скрываем панель отмены
                binding.cancelGroup.visibility = View.GONE
                // Скрываем клавиатуру
                hideKeyboard(binding.content)
                // Отменяем редактирование в ViewModel
                viewModel.cancelEdit()
            }
        }

        private fun hideKeyboard(view: View) {
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }

        private fun showKeyboard(view: View) {
            val imm =
                getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.showSoftInput(view, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
        }
    }




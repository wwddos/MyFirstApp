package ru.vlad.myfirstapp.viewmodel


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.repository.PostRepository
import ru.vlad.myfirstapp.repository.PostRepositoryFileImpl
import ru.vlad.myfirstapp.repository.PostRepositoryInMemoryImpl

class PostViewModel(application: Application) : AndroidViewModel(application) {

    // Используем файловую реализацию с передачей контекста приложения
    private val repository: PostRepository = PostRepositoryFileImpl(application)

    val data: LiveData<List<Post>> = repository.getAll()

    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        published = ""
    )

    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post> = _edited

    private val _editingMode = MutableLiveData(false)
    val editingMode: LiveData<Boolean> = _editingMode

    fun likeById(id: Long) = repository.likeById(id)
    fun shareById(id: Long) = repository.shareById(id)
    fun increaseViews(id: Long) = repository.increaseViews(id)
    fun removeById(id: Long) = repository.removeById(id)

    fun save() {
        _edited.value?.let { post ->
            if (post.content.isNotBlank()) {
                repository.save(post)
            }
        }
        _edited.value = empty
        _editingMode.value = false
    }

    fun edit(post: Post) {
        _edited.value = post
        _editingMode.value = true
    }

    fun changeContent(content: String) {
        val text = content.trim()
        _edited.value?.let { post ->
            if (post.content != text) {
                _edited.value = post.copy(content = text)
            }
        }
    }

    fun cancelEdit() {
        _edited.value = empty
        _editingMode.value = false
    }
}




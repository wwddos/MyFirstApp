package ru.vlad.myfirstapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.repository.PostRepository
import ru.vlad.myfirstapp.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {

    fun saveEditedPost(postId: Long, newContent: String) {
        // Получаем текущие данные о посте
        val currentPosts = data.value ?: return
        val existingPost = currentPosts.find { it.id == postId } ?: return

        // Создаем обновленный пост с новым контентом
        val updatedPost = existingPost.copy(content = newContent)

        // Сохраняем через репозиторий
        repository.save(updatedPost)

        // Сбрасываем режим редактирования
        _edited.value = empty
        _editingMode.value = false
    }


    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    // Пустой пост для создания нового
    private val empty = Post(
        id = 0,
        author = "",
        content = "",
        published = ""
    )

    // Список всех постов
    val data: LiveData<List<Post>> = repository.getAll()

    // Редактируемый пост
    private val _edited = MutableLiveData(empty)
    val edited: LiveData<Post> = _edited

    // Флаг, показываем ли панель отмены
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
        // Сбрасываем режим редактирования
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



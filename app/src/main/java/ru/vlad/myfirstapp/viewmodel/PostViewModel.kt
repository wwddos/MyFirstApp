package ru.vlad.myfirstapp.viewmodel


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.repository.PostRepository
import ru.vlad.myfirstapp.repository.PostRepositoryInMemoryImpl

class PostViewModel : ViewModel() {
    init {
        println("ViewModel: created")
    }

    override fun onCleared() {
        super.onCleared()
        println("ViewModel: cleared")
    }

    // Создаем экземпляр репозитория
    private val repository: PostRepository = PostRepositoryInMemoryImpl()

    // Данные, доступные для наблюдения
    val data: LiveData<Post> = repository.get()

    // Методы для вызова из Activity
    fun like() = repository.like()
    fun share() = repository.share()
    fun increaseViews() = repository.increaseViews()
}

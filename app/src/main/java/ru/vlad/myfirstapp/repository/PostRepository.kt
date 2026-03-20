package ru.vlad.myfirstapp.repository

import androidx.lifecycle.LiveData
import ru.vlad.myfirstapp.dto.Post

interface PostRepository {
    // Возвращает LiveData, на которую можно подписаться
    fun get(): LiveData<Post>

    // Лайк/дизлайк
    fun like()

    // Репост (увеличение счетчика)
    fun share()

    // Изменение просмотров (может пригодиться позже)
    fun increaseViews()
}

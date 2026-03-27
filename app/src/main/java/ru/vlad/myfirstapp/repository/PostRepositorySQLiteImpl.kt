package ru.vlad.myfirstapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ru.vlad.myfirstapp.dto.Post
import ru.vlad.myfirstapp.db.PostDao

class PostRepositorySQLiteImpl(
    private val dao: PostDao
) : PostRepository {

    private var posts = emptyList<Post>()
    private val _data = MutableLiveData(posts)

    init {
        posts = dao.getAll()
        _data.value = posts
    }

    override fun getAll(): LiveData<List<Post>> = _data

    override fun save(post: Post) {
        val saved = if (post.id == 0L) {
            dao.insert(post)
        } else {
            dao.update(post)
        }
        posts = if (post.id == 0L) {
            listOf(saved) + posts
        } else {
            posts.map { if (it.id == saved.id) saved else it }
        }
        _data.value = posts
    }
    override fun likeById(id: Long) {
        dao.likeById(id)
        posts = posts.map {
            if (it.id == id) {
                val newLiked = !it.likedByMe
                it.copy(
                    likedByMe = newLiked,
                    likes = if (newLiked) it.likes + 1 else it.likes - 1
                )
            } else it
        }
        _data.value = posts
    }

    override fun shareById(id: Long) {
        dao.shareById(id)
        posts = posts.map {
            if (it.id == id) it.copy(shares = it.shares + 1) else it
        }
        _data.value = posts
    }

    override fun increaseViews(id: Long) {
        dao.increaseViews(id)
        posts = posts.map {
            if (it.id == id) it.copy(views = it.views + 1) else it
        }
        _data.value = posts
    }

    override fun removeById(id: Long) {
        dao.delete(id)
        posts = posts.filter { it.id != id }
        _data.value = posts
    }
}
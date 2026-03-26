package ru.vlad.myfirstapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.vlad.myfirstapp.dto.Post
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostRepositorySharedPrefsImpl(
    private val context: Context
) : PostRepository {

    private val gson = Gson()
    private val prefs = context.getSharedPreferences("posts_repo", Context.MODE_PRIVATE)
    private val type = object : TypeToken<List<Post>>() {}.type
    private val key = "posts"

    private var nextId = 1L
    private val currentUserId = 1L
    private val currentUserName = "Я"

    private var posts = emptyList<Post>()
    private val _data = MutableLiveData(posts)

    init {
        loadData()
    }

    override fun getAll(): LiveData<List<Post>> = _data

    override fun likeById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(
                    likedByMe = !post.likedByMe,
                    likes = if (post.likedByMe) post.likes - 1 else post.likes + 1
                )
            } else {
                post
            }
        }
        _data.value = posts
        saveData()
    }

    override fun shareById(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(shares = post.shares + 1)
            } else {
                post
            }
        }
        _data.value = posts
        saveData()
    }

    override fun increaseViews(id: Long) {
        posts = posts.map { post ->
            if (post.id == id) {
                post.copy(views = post.views + 1)
            } else {
                post
            }
        }
        _data.value = posts
        saveData()
    }

    override fun save(post: Post) {
        posts = if (post.id == 0L) {
            val newPost = post.copy(
                id = nextId++,
                author = currentUserName,
                authorId = currentUserId,
                published = formatDate(Date()),
                likedByMe = false,
                likes = 0,
                shares = 0,
                views = 0
            )
            listOf(newPost) + posts
        } else {
            posts.map { existingPost ->
                if (existingPost.id == post.id) {
                    existingPost.copy(content = post.content)
                } else {
                    existingPost
                }
            }
        }
        _data.value = posts
        saveData()
    }

    override fun removeById(id: Long) {
        posts = posts.filter { it.id != id }
        _data.value = posts
        saveData()
    }

    private fun loadData() {
        val json = prefs.getString(key, null)
        if (json != null) {
            try {
                val loadedPosts: List<Post> = gson.fromJson(json, type)
                if (loadedPosts.isNotEmpty()) {
                    posts = loadedPosts
                    nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                    _data.value = posts
                    return
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        createInitialData()
        saveData()
    }

    private fun saveData() {
        prefs.edit().putString(key, gson.toJson(posts)).apply()
    }

    private fun createInitialData() {
        // Аналогично файловой реализации
        posts = listOf(
            Post(
                id = nextId++,
                author = "Нетология. Университет интернет-профессий",
                authorId = 2,
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась...",
                published = "21 мая в 18:36",
                likedByMe = false,
                likes = 999,
                shares = 25,
                views = 5700,
                video = null
            )
            // ... остальные посты
        )
        _data.value = posts
    }

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("d MMM в HH:mm", Locale("ru"))
        return format.format(date)
    }
}

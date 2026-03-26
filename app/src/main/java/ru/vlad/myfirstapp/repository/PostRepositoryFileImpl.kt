package ru.vlad.myfirstapp.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.vlad.myfirstapp.dto.Post
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class PostRepositoryFileImpl(
    private val context: Context
) : PostRepository {

    private val gson = Gson()
    private val filename = "posts.json"

    private val type = object : TypeToken<List<Post>>() {}.type

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
            // Создание нового поста
            val newPost = post.copy(
                id = generateNextId(),
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
            // Обновление существующего поста
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
        val file = getPostsFile()
        if (!file.exists()) {
            createInitialData()
            saveData()
            return
        }

        try {
            context.openFileInput(filename).bufferedReader().use { reader ->
                val loadedPosts: List<Post> = gson.fromJson(reader, type)
                if (loadedPosts.isNotEmpty()) {
                    posts = loadedPosts
                    nextId = (posts.maxOfOrNull { it.id } ?: 0) + 1
                    _data.value = posts
                } else {
                    createInitialData()
                    saveData()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            createInitialData()
            saveData()
        }
    }

    private fun saveData() {
        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE).bufferedWriter().use { writer ->
                gson.toJson(posts, writer)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun getPostsFile(): File = context.filesDir.resolve(filename)

    private fun createInitialData() {
        posts = listOf(
            Post(
                id = generateNextId(),
                author = "Нетология. Университет интернет-профессий",
                authorId = 2,
                content = "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению.",
                published = "21 мая в 18:36",
                likedByMe = false,
                likes = 999,
                shares = 25,
                views = 5700,
                video = null
            ),
            Post(
                id = generateNextId(),
                author = "Android Dev",
                authorId = 3,
                content = "Вышел новый релиз Android Studio! Теперь с поддержкой Gemini AI и улучшенным композером.",
                published = "22 мая в 10:15",
                likedByMe = false,
                likes = 342,
                shares = 89,
                views = 2300,
                video = "https://www.youtube.com/watch?v=WhWc3b3KhnY"
            ),
            Post(
                id = generateNextId(),
                author = "Kotlin Weekly",
                authorId = 4,
                content = "Kotlin 2.0.0 released! Что нового в языке? Смотрим обновления компилятора и стандартной библиотеки.",
                published = "23 мая в 09:42",
                likedByMe = true,
                likes = 1250,
                shares = 420,
                views = 8900,
                video = null
            )
        )
        _data.value = posts
    }


    private fun generateNextId(): Long = nextId++

    private fun formatDate(date: Date): String {
        val format = SimpleDateFormat("d MMM в HH:mm", Locale("ru"))
        return format.format(date)
    }
}

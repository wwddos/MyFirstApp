package ru.vlad.myfirstapp.dto
data class Post(
    val id: Long,
    val author: String,
    val authorId: Long = 0,
    val content: String,
    val published: String,
    val likedByMe: Boolean = false,
    val likes: Int = 0,
    val shares: Int = 0,
    val views: Int = 0,
    val video: String? = null
)


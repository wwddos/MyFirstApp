package ru.vlad.myfirstapp.db

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import ru.vlad.myfirstapp.dto.Post

class PostDaoImpl(private val db: SQLiteDatabase) : PostDao {

    override fun getAll(): List<Post> {
        val posts = mutableListOf<Post>()

        val cursor = db.query(
            PostContract.TABLE_NAME,
            PostContract.Columns.ALL_COLUMNS,
            null, null, null, null,
            "${BaseColumns._ID} DESC"
        )

        cursor.use {
            while (it.moveToNext()) {
                posts.add(mapCursorToPost(it))
            }
        }

        return posts
    }

    override fun getById(id: Long): Post? {
        val cursor = db.query(
            PostContract.TABLE_NAME,
            PostContract.Columns.ALL_COLUMNS,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return cursor.use {
            if (it.moveToFirst()) mapCursorToPost(it) else null
        }
    }

    override fun insert(post: Post): Post {
        val values = postToContentValues(post).apply {
            remove(BaseColumns._ID)
        }

        val newId = db.insert(PostContract.TABLE_NAME, null, values)
        return getById(newId) ?: post.copy(id = newId)
    }

    override fun update(post: Post): Post {
        val values = postToContentValues(post).apply {
            remove(BaseColumns._ID)
        }

        db.update(
            PostContract.TABLE_NAME,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(post.id.toString())
        )
        return getById(post.id) ?: post
    }

    override fun delete(id: Long) {
        db.delete(
            PostContract.TABLE_NAME,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun likeById(id: Long) {
        val post = getById(id) ?: return

        val newLikedByMe = !post.likedByMe
        val newLikes = if (newLikedByMe) post.likes + 1 else post.likes - 1

        val values = ContentValues().apply {
            put(PostContract.Columns.LIKED_BY_ME, if (newLikedByMe) 1 else 0)
            put(PostContract.Columns.LIKES, newLikes)
        }

        db.update(
            PostContract.TABLE_NAME,
            values,
            "${BaseColumns._ID} = ?",
            arrayOf(id.toString())
        )
    }

    override fun shareById(id: Long) {
        db.execSQL(
            "UPDATE ${PostContract.TABLE_NAME} SET ${PostContract.Columns.SHARES} = ${PostContract.Columns.SHARES} + 1 WHERE ${BaseColumns._ID} = ?",
            arrayOf(id)
        )
    }

    override fun increaseViews(id: Long) {
        db.execSQL(
            "UPDATE ${PostContract.TABLE_NAME} SET ${PostContract.Columns.VIEWS} = ${PostContract.Columns.VIEWS} + 1 WHERE ${BaseColumns._ID} = ?",
            arrayOf(id)
        )
    }

    private fun mapCursorToPost(cursor: Cursor): Post {
        val id = cursor.getLong(cursor.getColumnIndexOrThrow(BaseColumns._ID))
        val author = cursor.getString(cursor.getColumnIndexOrThrow(PostContract.Columns.AUTHOR))
        val authorId = cursor.getLong(cursor.getColumnIndexOrThrow(PostContract.Columns.AUTHOR_ID))
        val content = cursor.getString(cursor.getColumnIndexOrThrow(PostContract.Columns.CONTENT))
        val published = cursor.getString(cursor.getColumnIndexOrThrow(PostContract.Columns.PUBLISHED))
        val likedByMe = cursor.getInt(cursor.getColumnIndexOrThrow(PostContract.Columns.LIKED_BY_ME)) != 0
        val likes = cursor.getInt(cursor.getColumnIndexOrThrow(PostContract.Columns.LIKES))
        val shares = cursor.getInt(cursor.getColumnIndexOrThrow(PostContract.Columns.SHARES))
        val views = cursor.getInt(cursor.getColumnIndexOrThrow(PostContract.Columns.VIEWS))
        val video = cursor.getString(cursor.getColumnIndexOrThrow(PostContract.Columns.VIDEO))

        return Post(
            id = id,
            author = author,
            authorId = authorId,
            content = content,
            published = published,
            likedByMe = likedByMe,
            likes = likes,
            shares = shares,
            views = views,
            video = video
        )
    }

    private fun postToContentValues(post: Post): ContentValues {
        return ContentValues().apply {
            put(BaseColumns._ID, post.id)
            put(PostContract.Columns.AUTHOR, post.author)
            put(PostContract.Columns.AUTHOR_ID, post.authorId)
            put(PostContract.Columns.CONTENT, post.content)
            put(PostContract.Columns.PUBLISHED, post.published)
            put(PostContract.Columns.LIKED_BY_ME, if (post.likedByMe) 1 else 0)
            put(PostContract.Columns.LIKES, post.likes)
            put(PostContract.Columns.SHARES, post.shares)
            put(PostContract.Columns.VIEWS, post.views)
            if (post.video != null) {
                put(PostContract.Columns.VIDEO, post.video)
            } else {
                putNull(PostContract.Columns.VIDEO)
            }
        }
    }
}
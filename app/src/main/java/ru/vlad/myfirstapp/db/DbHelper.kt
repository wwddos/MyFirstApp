package ru.vlad.myfirstapp.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class DbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "myfirstapp.db"
        private const val DATABASE_VERSION = 1

        private val SQL_CREATE_POSTS = """
            CREATE TABLE ${PostContract.TABLE_NAME} (
                ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${PostContract.Columns.AUTHOR} TEXT NOT NULL,
                ${PostContract.Columns.AUTHOR_ID} INTEGER NOT NULL,
                ${PostContract.Columns.CONTENT} TEXT NOT NULL,
                ${PostContract.Columns.PUBLISHED} TEXT NOT NULL,
                ${PostContract.Columns.LIKED_BY_ME} INTEGER NOT NULL DEFAULT 0,
                ${PostContract.Columns.LIKES} INTEGER NOT NULL DEFAULT 0,
                ${PostContract.Columns.SHARES} INTEGER NOT NULL DEFAULT 0,
                ${PostContract.Columns.VIEWS} INTEGER NOT NULL DEFAULT 0,
                ${PostContract.Columns.VIDEO} TEXT
            )
        """.trimIndent()
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_POSTS)
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${PostContract.TABLE_NAME}")
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase) {
        // Первый пост (без видео)
        val contentValues1 = ContentValues().apply {
            put(PostContract.Columns.AUTHOR, "Нетология. Университет интернет-профессий")
            put(PostContract.Columns.AUTHOR_ID, 2)
            put(PostContract.Columns.CONTENT, "Привет, это новая Нетология! Когда-то Нетология начиналась с интенсивов по онлайн-маркетингу. Затем появились курсы по дизайну, разработке, аналитике и управлению.")
            put(PostContract.Columns.PUBLISHED, "21 мая в 18:36")
            put(PostContract.Columns.LIKED_BY_ME, 0)
            put(PostContract.Columns.LIKES, 999)
            put(PostContract.Columns.SHARES, 25)
            put(PostContract.Columns.VIEWS, 5700)
            putNull(PostContract.Columns.VIDEO)
        }
        db.insert(PostContract.TABLE_NAME, null, contentValues1)

        // Второй пост с видео
        val contentValues2 = ContentValues().apply {
            put(PostContract.Columns.AUTHOR, "Android Dev")
            put(PostContract.Columns.AUTHOR_ID, 3)
            put(PostContract.Columns.CONTENT, "Вышел новый релиз Android Studio! Теперь с поддержкой Gemini AI и улучшенным композером.")
            put(PostContract.Columns.PUBLISHED, "22 мая в 10:15")
            put(PostContract.Columns.LIKED_BY_ME, 0)
            put(PostContract.Columns.LIKES, 342)
            put(PostContract.Columns.SHARES, 89)
            put(PostContract.Columns.VIEWS, 2300)
            put(PostContract.Columns.VIDEO, "https://www.youtube.com/watch?v=WhWc3b3KhnY")
        }
        db.insert(PostContract.TABLE_NAME, null, contentValues2)
    }
}
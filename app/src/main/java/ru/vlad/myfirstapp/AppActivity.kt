package ru.vlad.myfirstapp.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import ru.vlad.myfirstapp.R

class AppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app)

        // Обработка входящего интента
        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if (!text.isNullOrBlank()) {
                // Переходим к фрагменту создания поста с текстом
                val bundle = Bundle().apply {
                    putString("postContent", text)
                }
                findNavController(R.id.nav_host_fragment).navigate(
                    R.id.newPostFragment,
                    bundle
                )
            }
        }
    }
}









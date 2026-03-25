package ru.vlad.myfirstapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract

class EditPostContract : ActivityResultContract<String?, String?>() {

    override fun createIntent(context: Context, input: String?): Intent {
        // Создаем Intent для запуска EditPostActivity
        return Intent(context, EditPostActivity::class.java).apply {
            // Если передан непустой текст, значит это редактирование
            if (!input.isNullOrBlank()) {
                putExtra(Intent.EXTRA_TEXT, input)
            }
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        // Обрабатываем результат
        return when {
            resultCode != Activity.RESULT_OK -> null
            intent == null -> null
            else -> intent.getStringExtra(Intent.EXTRA_TEXT)
        }
    }
}

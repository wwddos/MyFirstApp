package ru.vlad.myfirstapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val format = SimpleDateFormat("d MMM в HH:mm", Locale("ru"))

    fun formatDate(date: Date): String {
        return format.format(date)
    }

    fun currentDateTime(): String {
        return formatDate(Date())
    }
}

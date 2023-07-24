package com.example.notebycolor.util

import androidx.compose.ui.graphics.Color
import com.example.notebycolor.AppColor
import com.example.notebycolor.appColors
import java.text.SimpleDateFormat
import java.util.Date

fun convertLongToTime(time: Long): String {
    val date = Date(time * 1000)
    val format = SimpleDateFormat("MMM dd")
    return format.format(date)
}

fun colorNameToColor(name: String): Color {
    appColors.forEach { item ->
        if (name == item.name) return item.color
    }

    return Color.Black
}
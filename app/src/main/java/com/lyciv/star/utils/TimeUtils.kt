package com.lyciv.star.utils

import androidx.compose.ui.graphics.Color
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TimeUtils {
    private val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    private val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
    
    fun getCurrentTime(): String {
        return timeFormat.format(Calendar.getInstance().time)
    }
    
    fun getCurrentDate(): String {
        return dateFormat.format(Calendar.getInstance().time)
    }
    
    fun getTimeBasedColors(): TimeColors {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        
        return when (hour) {
            in 5..11 -> TimeColors(
                primary = Color(0xFFFFB74D),  // Morning - Orange
                secondary = Color(0xFFFFF3E0),
                blur = Color(0x80FFE0B2)
            )
            in 12..16 -> TimeColors(
                primary = Color(0xFF64B5F6),  // Afternoon - Light Blue
                secondary = Color(0xFFE3F2FD),
                blur = Color(0x80BBDEFB)
            )
            in 17..19 -> TimeColors(
                primary = Color(0xFFFF8A65),  // Evening - Deep Orange
                secondary = Color(0xFFFBE9E7),
                blur = Color(0x80FFCCBC)
            )
            else -> TimeColors(
                primary = Color(0xFF9575CD),  // Night - Purple
                secondary = Color(0xFFEDE7F6),
                blur = Color(0x80B39DDB)
            )
        }
    }
    
    data class TimeColors(
        val primary: Color,
        val secondary: Color,
        val blur: Color
    )
}

package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectures")
data class Lecture(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val doctorName: String,
    val hallNumber: String,
    val hallName: String,
    val dayOfWeek: Int, // 1 = Sunday, 2 = Monday, 3 = Tuesday, 4 = Wednesday, 5 = Thursday, 6 = Friday, 7 = Saturday
    val startTimeMinutes: Int, // e.g., 540 for 9:00 AM
    val endTimeMinutes: Int,   // e.g., 660 for 11:00 AM
    val cardColorHex: String = "#00F0FF",
    val requiredTools: String = "",
    val isAttended: Boolean = false,
    val synced: Boolean = false
) {
    fun formatStartTime(): String = formatMinutes(startTimeMinutes)
    fun formatEndTime(): String = formatMinutes(endTimeMinutes)

    private fun formatMinutes(minutes: Int): String {
        val hours24 = minutes / 60
        val mins = minutes % 60
        val amPm = if (hours24 >= 12) "م" else "ص"
        val hours12 = when {
            hours24 == 0 -> 12
            hours24 > 12 -> hours24 - 12
            else -> hours24
        }
        return String.format("%02d:%02d %s", hours12, mins, amPm)
    }
}

package app.lessafakerny.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lectures")
data class Lecture(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String = "",
    val doctorName: String = "",
    val hallNumber: String = "",
    val hallName: String = "",
    val dayOfWeek: Int = 1,
    val startTimeMinutes: Int = 0,
    val endTimeMinutes: Int = 0,
    val cardColorHex: String = "#00E5FF",
    val requiredTools: String = "",
    val isAttended: Boolean = false,
    val synced: Boolean = false
) {
    constructor() : this(0, "", "", "", "", 1, 0, 0, "#00E5FF", "", false, false)

    fun formatStartTime(is24Hour: Boolean = false, language: String = "ar"): String =
        app.lessafakerny.android.util.formatTimeMinutes(startTimeMinutes, is24Hour, language)

    fun formatEndTime(is24Hour: Boolean = false, language: String = "ar"): String =
        app.lessafakerny.android.util.formatTimeMinutes(endTimeMinutes, is24Hour, language)
}

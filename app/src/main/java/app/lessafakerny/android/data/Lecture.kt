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
        return String.format(java.util.Locale.US, "%02d:%02d %s", hours12, mins, amPm)
    }
}

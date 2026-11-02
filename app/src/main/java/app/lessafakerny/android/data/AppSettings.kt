package app.lessafakerny.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1,
    val notificationsEnabled: Boolean = true,
    val lectureRemindersEnabled: Boolean = true,
    val remind30MinBefore: Boolean = true,
    val remind15MinBefore: Boolean = true,
    val remind5MinBefore: Boolean = true,
    val remindAtStart: Boolean = true,
    val examRemindersEnabled: Boolean = true,
    val remindExam24hBefore: Boolean = true,
    val remindExam2hBefore: Boolean = true,
    val remindExam30mBefore: Boolean = true,
    val soundTone: String = "SHORT", // "SHORT" or "LONG"
    val vibrationEnabled: Boolean = true,
    val language: String = "ar", // "ar" or "en"
    val activeThemeId: String = "NEON_MIDNIGHT",
    val unlockedThemeIds: String = "NEON_MIDNIGHT",
    val is24HourFormat: Boolean = false,
    val userRole: String = "STUDENT" // "STUDENT" or "DOCTOR"
)

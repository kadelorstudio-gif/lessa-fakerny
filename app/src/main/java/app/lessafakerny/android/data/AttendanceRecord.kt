package app.lessafakerny.android.data

import androidx.room.Entity

/**
 * Stores attendance against specific calendar date occurrences (YYYY-MM-DD).
 * Attending one week does NOT overwrite future or past weeks.
 */
@Entity(
    tableName = "attendance_records",
    primaryKeys = ["lectureId", "dateString"]
)
data class AttendanceRecord(
    val lectureId: Long,
    val dateString: String, // e.g. "2026-10-30"
    val status: String = "PRESENT", // "PRESENT", "ABSENT"
    val timestamp: Long = System.currentTimeMillis()
)

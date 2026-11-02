package app.lessafakerny.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String = "",
    val examDateMillis: Long = 0L,
    val hallLocation: String = "",
    val allowedTools: String = "",
    val cardColorHex: String = "#8B5CF6",
    val synced: Boolean = false
) {
    constructor() : this(0, "", 0L, "", "", "#8B5CF6", false)
}

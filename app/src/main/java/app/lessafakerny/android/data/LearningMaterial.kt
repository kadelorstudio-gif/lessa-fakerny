package app.lessafakerny.android.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "learning_materials")
data class LearningMaterial(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String = "",
    val subjectName: String = "",
    val content: String = "",
    val resourceLink: String = "",
    val addedDateMillis: Long = System.currentTimeMillis(),
    val authorRole: String = "STUDENT" // "STUDENT" or "DOCTOR"
) {
    constructor() : this(0, "", "", "", "", System.currentTimeMillis(), "STUDENT")
}

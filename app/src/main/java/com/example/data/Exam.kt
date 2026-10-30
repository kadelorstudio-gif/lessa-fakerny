package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectName: String,
    val examDateMillis: Long,
    val hallLocation: String,
    val allowedTools: String,
    val cardColorHex: String = "#9D4EDD",
    val synced: Boolean = false
)

package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams ORDER BY examDateMillis ASC")
    fun getAllExams(): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE examDateMillis >= :currentMillis ORDER BY examDateMillis ASC")
    fun getUpcomingExams(currentMillis: Long): Flow<List<Exam>>

    @Query("SELECT * FROM exams WHERE id = :id")
    suspend fun getExamById(id: Long): Exam?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExam(exam: Exam): Long

    @Update
    suspend fun updateExam(exam: Exam)

    @Delete
    suspend fun deleteExam(exam: Exam)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExams(exams: List<Exam>)
}

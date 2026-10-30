package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LectureDao {
    @Query("SELECT * FROM lectures ORDER BY dayOfWeek ASC, startTimeMinutes ASC")
    fun getAllLectures(): Flow<List<Lecture>>

    @Query("SELECT * FROM lectures WHERE dayOfWeek = :dayOfWeek ORDER BY startTimeMinutes ASC")
    fun getLecturesForDay(dayOfWeek: Int): Flow<List<Lecture>>

    @Query("SELECT * FROM lectures WHERE id = :id")
    suspend fun getLectureById(id: Long): Lecture?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLecture(lecture: Lecture): Long

    @Update
    suspend fun updateLecture(lecture: Lecture)

    @Delete
    suspend fun deleteLecture(lecture: Lecture)

    @Query("UPDATE lectures SET isAttended = :isAttended WHERE id = :id")
    suspend fun updateAttendance(id: Long, isAttended: Boolean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLectures(lectures: List<Lecture>)
}

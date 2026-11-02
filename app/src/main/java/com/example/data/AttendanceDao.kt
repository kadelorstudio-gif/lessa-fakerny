package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {
    @Query("SELECT * FROM attendance_records WHERE dateString = :dateString")
    fun getAttendanceForDate(dateString: String): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records")
    fun getAllAttendanceRecords(): Flow<List<AttendanceRecord>>

    @Query("SELECT * FROM attendance_records WHERE lectureId = :lectureId AND dateString = :dateString LIMIT 1")
    suspend fun getRecord(lectureId: Long, dateString: String): AttendanceRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordAttendance(record: AttendanceRecord)

    @Query("DELETE FROM attendance_records WHERE lectureId = :lectureId AND dateString = :dateString")
    suspend fun removeAttendance(lectureId: Long, dateString: String)

    @Query("DELETE FROM attendance_records WHERE lectureId = :lectureId")
    suspend fun deleteForLecture(lectureId: Long)

    @Query("DELETE FROM attendance_records")
    suspend fun clearAll()
}

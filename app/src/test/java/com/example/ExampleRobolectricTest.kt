package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.AttendanceRecord
import com.example.notifications.ReminderScheduler
import com.example.util.LocaleManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("لسه فاكرني؟", appName)
    }

    @Test
    fun `attendance records are stored per occurrence date`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = AppDatabase.getDatabase(context)
        val attendanceDao = db.attendanceDao()

        val lectureId = 42L
        val todayDate = "2026-10-30"
        val nextWeekDate = "2026-11-06"

        // Record attendance for today
        attendanceDao.recordAttendance(
            AttendanceRecord(lectureId = lectureId, dateString = todayDate, status = "PRESENT")
        )

        val todayRecord = attendanceDao.getRecord(lectureId, todayDate)
        val nextWeekRecord = attendanceDao.getRecord(lectureId, nextWeekDate)

        assertNotNull(todayRecord)
        assertEquals("PRESENT", todayRecord?.status)
        assertNull(nextWeekRecord) // Verifies attending this week does not mark next week!
    }

    @Test
    fun `calculateNextLectureStartMillis always returns a future timestamp`() {
        val now = System.currentTimeMillis()
        val nextStart = ReminderScheduler.calculateNextLectureStartMillis(
            dayOfWeek = Calendar.MONDAY,
            startTimeMinutes = 540 // 9:00 AM
        )
        assertTrue(nextStart >= now - 60000L)
    }

    @Test
    fun `localization manager maps English and Arabic correctly`() {
        val arabic = LocaleManager.getStrings("ar")
        val english = LocaleManager.getStrings("en")

        assertEquals("الرئيسية", arabic.navHome)
        assertEquals("Home", english.navHome)

        assertEquals("الجدول", arabic.navTimetable)
        assertEquals("Timetable", english.navTimetable)

        assertEquals("الامتحانات", arabic.navExams)
        assertEquals("Exams", english.navExams)

        assertEquals("الإعدادات", arabic.navSettings)
        assertEquals("Settings", english.navSettings)

        assertEquals("Kadelor Studio", arabic.studioNameLabel)
        assertEquals("Kadelor Studio", english.studioNameLabel)

        assertEquals("Kadelor-studio@gmail.com", arabic.studioEmailLabel)
        assertEquals("Kadelor-studio@gmail.com", english.studioEmailLabel)

        assertEquals("الأحد", LocaleManager.getLocalizedDayName(Calendar.SUNDAY, "ar"))
        assertEquals("Sunday", LocaleManager.getLocalizedDayName(Calendar.SUNDAY, "en"))
    }
}

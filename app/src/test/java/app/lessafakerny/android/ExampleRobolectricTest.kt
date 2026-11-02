package app.lessafakerny.android

import app.lessafakerny.android.notifications.ReminderScheduler
import app.lessafakerny.android.util.LocaleManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class ExampleRobolectricTest {

    @Test
    fun calculateNextLectureStartMillis_alwaysReturnsFutureOrCurrentTimestamp() {
        val now = System.currentTimeMillis()
        val nextStart = ReminderScheduler.calculateNextLectureStartMillis(
            dayOfWeek = Calendar.MONDAY,
            startTimeMinutes = 540 // 9:00 AM
        )
        assertTrue(nextStart >= now - 60000L)
    }

    @Test
    fun localizationManager_mapsEnglishAndArabicCorrectly() {
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

package app.lessafakerny.android.util

import java.util.Calendar
import java.util.Locale

fun getArabicDayName(dayOfWeek: Int): String {
    return LocaleManager.getLocalizedDayName(dayOfWeek, "ar")
}

fun getLocalizedDayName(dayOfWeek: Int, lang: String): String {
    return LocaleManager.getLocalizedDayName(dayOfWeek, lang)
}

/**
 * Formats minutes from midnight into 12-hour or 24-hour localized format.
 * - 12h Arabic: "02:30 م" or "09:15 ص"
 * - 12h English: "02:30 PM" or "09:15 AM"
 * - 24h: "14:30" or "09:15"
 */
fun formatTimeMinutes(minutes: Int, is24Hour: Boolean = false, language: String = "ar"): String {
    val totalMinutes = (minutes % 1440 + 1440) % 1440
    val hours24 = totalMinutes / 60
    val mins = totalMinutes % 60
    return if (is24Hour) {
        String.format(Locale.US, "%02d:%02d", hours24, mins)
    } else {
        val isEn = language.equals("en", ignoreCase = true)
        val amPm = if (hours24 >= 12) (if (isEn) "PM" else "م") else (if (isEn) "AM" else "ص")
        val hours12 = when {
            hours24 == 0 -> 12
            hours24 > 12 -> hours24 - 12
            else -> hours24
        }
        String.format(Locale.US, "%02d:%02d %s", hours12, mins, amPm)
    }
}

fun formatExamTime(dateMillis: Long, is24Hour: Boolean = false, language: String = "ar"): String {
    val cal = Calendar.getInstance().apply { timeInMillis = dateMillis }
    val minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    return formatTimeMinutes(minutes, is24Hour, language)
}

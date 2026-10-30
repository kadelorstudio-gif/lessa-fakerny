package com.example.util

import java.util.Calendar

fun getArabicDayName(dayOfWeek: Int): String {
    return LocaleManager.getLocalizedDayName(dayOfWeek, "ar")
}

fun getLocalizedDayName(dayOfWeek: Int, lang: String): String {
    return LocaleManager.getLocalizedDayName(dayOfWeek, lang)
}

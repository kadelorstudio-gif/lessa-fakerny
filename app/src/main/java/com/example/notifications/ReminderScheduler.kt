package com.example.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.AppSettings
import com.example.data.Exam
import com.example.data.Lecture
import com.example.util.LocaleManager
import java.util.Calendar

object ReminderScheduler {
    private const val TAG = "ReminderScheduler"

    fun calculateNextLectureStartMillis(dayOfWeek: Int, startTimeMinutes: Int): Long {
        val now = Calendar.getInstance()
        val currentDay = now.get(Calendar.DAY_OF_WEEK)
        val currentMinutes = now.get(Calendar.HOUR_OF_DAY) * 60 + now.get(Calendar.MINUTE)

        var daysToAdd = (dayOfWeek - currentDay + 7) % 7
        if (daysToAdd == 0 && currentMinutes >= startTimeMinutes) {
            daysToAdd = 7
        }

        val target = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, daysToAdd)
            set(Calendar.HOUR_OF_DAY, startTimeMinutes / 60)
            set(Calendar.MINUTE, startTimeMinutes % 60)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return target.timeInMillis
    }

    fun scheduleLectureReminders(context: Context, lecture: Lecture, settings: AppSettings?) {
        if (settings?.notificationsEnabled == false || settings?.lectureRemindersEnabled == false) {
            cancelLectureReminders(context, lecture.id)
            return
        }

        val strings = LocaleManager.getStrings(settings?.language ?: "ar")
        val startMillis = calculateNextLectureStartMillis(lecture.dayOfWeek, lecture.startTimeMinutes)
        val now = System.currentTimeMillis()

        val reminders = listOf(
            Triple(
                30 * 60 * 1000L,
                1,
                Pair(
                    strings.notifLectureSoonTitle,
                    String.format(strings.notifLectureSoonBody, 30, lecture.subjectName, lecture.hallName)
                )
            ),
            Triple(
                15 * 60 * 1000L,
                2,
                Pair(
                    strings.notifLectureSoonTitle,
                    String.format(strings.notifLectureSoonBody, 15, lecture.subjectName, lecture.hallName)
                )
            ),
            Triple(
                5 * 60 * 1000L,
                3,
                Pair(
                    strings.notifLectureSoonTitle,
                    String.format(strings.notifLectureSoonBody, 5, lecture.subjectName, lecture.hallName)
                )
            ),
            Triple(
                0L,
                4,
                Pair(
                    strings.notifLectureStartTitle,
                    String.format(strings.notifLectureStartBody, lecture.subjectName, lecture.hallName)
                )
            )
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val toneKey = settings?.soundTone ?: "DEFAULT"
        val isSilent = toneKey.equals("SILENT", ignoreCase = true)
        val enableVibration = settings?.vibrationEnabled ?: true
        val channelId = NotificationHelper.getLectureChannelId(toneKey)

        reminders.forEach { (offsetMillis, index, copy) ->
            val isEnabled = when (index) {
                1 -> settings?.remind30MinBefore ?: true
                2 -> settings?.remind15MinBefore ?: true
                3 -> settings?.remind5MinBefore ?: true
                4 -> settings?.remindAtStart ?: true
                else -> true
            }

            val triggerTime = startMillis - offsetMillis
            val requestCode = (lecture.id * 100 + index).toInt()

            if (isEnabled && triggerTime > now) {
                val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                    putExtra("EXTRA_ID", requestCode)
                    putExtra("EXTRA_CHANNEL_ID", channelId)
                    putExtra("EXTRA_TONE", toneKey)
                    putExtra("EXTRA_TITLE", copy.first)
                    putExtra("EXTRA_BODY", copy.second)
                    putExtra(NotificationHelper.EXTRA_TARGET_SCREEN, "HOME")
                    putExtra("EXTRA_IS_SILENT", isSilent)
                    putExtra("EXTRA_ENABLE_VIBRATION", enableVibration)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                        } catch (se: Exception) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                        }
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    }
                    Log.d(TAG, "Scheduled lecture reminder $requestCode for ${lecture.subjectName} with tone $toneKey at $triggerTime")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to schedule lecture alarm $requestCode", e)
                }
            } else {
                cancelAlarm(context, requestCode)
            }
        }
    }

    fun cancelLectureReminders(context: Context, lectureId: Long) {
        for (index in 1..4) {
            val requestCode = (lectureId * 100 + index).toInt()
            cancelAlarm(context, requestCode)
        }
    }

    fun scheduleExamReminders(context: Context, exam: Exam, settings: AppSettings?) {
        if (settings?.notificationsEnabled == false || settings?.examRemindersEnabled == false) {
            cancelExamReminders(context, exam.id)
            return
        }

        val strings = LocaleManager.getStrings(settings?.language ?: "ar")
        val now = System.currentTimeMillis()
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val toneKey = settings?.soundTone ?: "DEFAULT"
        val isSilent = toneKey.equals("SILENT", ignoreCase = true)
        val enableVibration = settings?.vibrationEnabled ?: true
        val channelId = NotificationHelper.getExamChannelId(toneKey)

        val reminders = listOf(
            Triple(
                24 * 3600 * 1000L,
                1,
                Pair(
                    strings.notifExamTomorrowTitle,
                    String.format(strings.notifExamTomorrowBody, exam.subjectName, exam.hallLocation)
                )
            ),
            Triple(
                2 * 3600 * 1000L,
                2,
                Pair(
                    strings.notifExamSoonTitle,
                    String.format(strings.notifExamSoonBody, exam.subjectName, exam.hallLocation)
                )
            ),
            Triple(
                30 * 60 * 1000L,
                3,
                Pair(
                    strings.notifExamSoonTitle,
                    String.format(strings.notifExamSoonBody, exam.subjectName, exam.hallLocation)
                )
            )
        )

        reminders.forEach { (offsetMillis, index, copy) ->
            val isEnabled = when (index) {
                1 -> settings?.remindExam24hBefore ?: true
                2 -> settings?.remindExam2hBefore ?: true
                3 -> settings?.remindExam30mBefore ?: true
                else -> true
            }

            val triggerTime = exam.examDateMillis - offsetMillis
            val requestCode = (100000 + exam.id * 10 + index).toInt()

            if (isEnabled && triggerTime > now) {
                val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
                    putExtra("EXTRA_ID", requestCode)
                    putExtra("EXTRA_CHANNEL_ID", channelId)
                    putExtra("EXTRA_TONE", toneKey)
                    putExtra("EXTRA_TITLE", copy.first)
                    putExtra("EXTRA_BODY", copy.second)
                    putExtra(NotificationHelper.EXTRA_TARGET_SCREEN, "EXAMS")
                    putExtra("EXTRA_IS_SILENT", isSilent)
                    putExtra("EXTRA_ENABLE_VIBRATION", enableVibration)
                }

                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        try {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                        } catch (se: Exception) {
                            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                        }
                    } else {
                        alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
                    }
                    Log.d(TAG, "Scheduled exam reminder $requestCode for ${exam.subjectName} with tone $toneKey at $triggerTime")
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to schedule exam alarm $requestCode", e)
                }
            } else {
                cancelAlarm(context, requestCode)
            }
        }
    }

    fun cancelExamReminders(context: Context, examId: Long) {
        for (index in 1..3) {
            val requestCode = (100000 + examId * 10 + index).toInt()
            cancelAlarm(context, requestCode)
        }
    }

    private fun cancelAlarm(context: Context, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun reconcileAllReminders(
        context: Context,
        lectures: List<Lecture>,
        exams: List<Exam>,
        settings: AppSettings?
    ) {
        val toneKey = settings?.soundTone ?: "DEFAULT"
        NotificationHelper.createNotificationChannels(context, toneKey)
        lectures.forEach { scheduleLectureReminders(context, it, settings) }
        exams.forEach { scheduleExamReminders(context, it, settings) }
    }
}

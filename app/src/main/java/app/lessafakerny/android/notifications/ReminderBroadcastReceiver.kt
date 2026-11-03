package app.lessafakerny.android.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import app.lessafakerny.android.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val notificationId = intent.getIntExtra("EXTRA_ID", 1001)
        val toneKey = intent.getStringExtra("EXTRA_TONE") ?: "DEFAULT"
        val channelId = intent.getStringExtra("EXTRA_CHANNEL_ID") ?: NotificationHelper.getLectureChannelId(toneKey)
        val title = intent.getStringExtra("EXTRA_TITLE") ?: "تذكير دراسي 🎓"
        val body = intent.getStringExtra("EXTRA_BODY") ?: "ميعاد المحاضرة يقترب"
        val targetScreen = intent.getStringExtra(NotificationHelper.EXTRA_TARGET_SCREEN)
        val isSilent = intent.getBooleanExtra("EXTRA_IS_SILENT", toneKey.equals("SILENT", ignoreCase = true))
        val enableVibration = intent.getBooleanExtra("EXTRA_ENABLE_VIBRATION", true)
        val lectureId = intent.getLongExtra("EXTRA_LECTURE_ID", -1L)

        NotificationHelper.createNotificationChannels(context, toneKey)
        NotificationHelper.showNotification(
            context = context,
            notificationId = notificationId,
            channelId = channelId,
            title = title,
            body = body,
            targetScreen = targetScreen,
            isSilent = isSilent,
            enableVibration = enableVibration,
            toneKey = toneKey
        )

        // Reschedule next week's alarm for this lecture automatically
        if (lectureId != -1L) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val lecture = db.lectureDao().getLectureById(lectureId)
                    val settings = db.appSettingsDao().getSettings().firstOrNull()
                    if (lecture != null) {
                        ReminderScheduler.scheduleLectureReminders(context, lecture, settings)
                    }
                } catch (e: Exception) {
                    Log.e("ReminderBroadcastReceiver", "Failed to reschedule lecture $lectureId", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

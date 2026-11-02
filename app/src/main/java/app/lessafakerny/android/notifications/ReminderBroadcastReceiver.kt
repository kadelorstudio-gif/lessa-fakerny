package app.lessafakerny.android.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

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
    }
}

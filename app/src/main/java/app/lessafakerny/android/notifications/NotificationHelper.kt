package app.lessafakerny.android.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.os.Build
import androidx.core.app.NotificationCompat
import app.lessafakerny.android.MainActivity
import app.lessafakerny.android.R

object NotificationHelper {
    const val CHANNEL_LECTURES_BASE = "channel_lectures"
    const val CHANNEL_EXAMS_BASE = "channel_exams"
    const val CHANNEL_GENERAL = "channel_general"

    const val EXTRA_TARGET_SCREEN = "EXTRA_TARGET_SCREEN"

    fun getLectureChannelId(toneKey: String): String {
        val safeKey = toneKey.lowercase().trim()
        return "${CHANNEL_LECTURES_BASE}_$safeKey"
    }

    fun getExamChannelId(toneKey: String): String {
        val safeKey = toneKey.lowercase().trim()
        return "${CHANNEL_EXAMS_BASE}_$safeKey"
    }

    /**
     * Creates or updates notification channels configured with the user-selected tone.
     * In Android 8.0+, sound is a property of the NotificationChannel. By utilizing a tone-keyed
     * channel identifier, any change in tone by the user takes immediate effect for upcoming
     * notifications without requiring user manual system intervention.
     */
    fun createNotificationChannels(context: Context, activeToneKey: String = "DEFAULT") {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val soundUri = RingtoneCatalog.getSoundUri(context, activeToneKey)
            val isSilent = activeToneKey.equals("SILENT", ignoreCase = true)

            // 1. Lecture Channel for active tone
            val lectureChannelId = getLectureChannelId(activeToneKey)
            val lectureChannel = NotificationChannel(
                lectureChannelId,
                if (isSilent) "تذكير المحاضرات (صامت)" else "تذكير المحاضرات",
                if (isSilent) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات مواعيد المحاضرات الجامعية قبل البدء"
                if (isSilent) {
                    setSound(null, null)
                    enableVibration(false)
                } else {
                    if (soundUri != null) {
                        setSound(soundUri, audioAttributes)
                    }
                    enableVibration(true)
                }
            }

            // 2. Exam Channel for active tone
            val examChannelId = getExamChannelId(activeToneKey)
            val examChannel = NotificationChannel(
                examChannelId,
                if (isSilent) "تذكير الامتحانات (صامت)" else "تذكير الامتحانات",
                if (isSilent) NotificationManager.IMPORTANCE_DEFAULT else NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "تنبيهات مواعيد الاختبارات والامتحانات النهائية والنصفية"
                if (isSilent) {
                    setSound(null, null)
                    enableVibration(false)
                } else {
                    if (soundUri != null) {
                        setSound(soundUri, audioAttributes)
                    }
                    enableVibration(true)
                }
            }

            // 3. General Announcements Channel
            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL,
                "تنبيهات عامة",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "إشعارات عامة وتحديثات التطبيق"
            }

            manager.createNotificationChannel(lectureChannel)
            manager.createNotificationChannel(examChannel)
            manager.createNotificationChannel(generalChannel)
        }
    }

    fun showNotification(
        context: Context,
        notificationId: Int,
        channelId: String,
        title: String,
        body: String,
        targetScreen: String? = null,
        isSilent: Boolean = false,
        enableVibration: Boolean = true,
        toneKey: String = "DEFAULT"
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            if (targetScreen != null) {
                putExtra(EXTRA_TARGET_SCREEN, targetScreen)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Ensure target channel exists
        createNotificationChannels(context, toneKey)

        val soundUri = RingtoneCatalog.getSoundUri(context, toneKey)

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.app_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(if (isSilent) NotificationCompat.PRIORITY_DEFAULT else NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        if (isSilent) {
            builder.setSilent(true)
            builder.setSound(null)
            builder.setVibrate(longArrayOf(0))
        } else {
            if (soundUri != null) {
                builder.setSound(soundUri)
            }
            if (enableVibration) {
                builder.setVibrate(longArrayOf(0, 250, 200, 250))
            } else {
                builder.setVibrate(longArrayOf(0))
            }
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(notificationId, builder.build())
    }

    fun playPreviewTone(context: Context, tone: String) {
        RingtonePreviewPlayer.play(context, tone)
    }

    fun stopPreviewTone() {
        RingtonePreviewPlayer.stop()
    }
}

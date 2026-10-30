package com.example.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val lectures = db.lectureDao().getAllLectures().firstOrNull() ?: emptyList()
                    val exams = db.examDao().getAllExams().firstOrNull() ?: emptyList()
                    val settings = db.appSettingsDao().getSettings().firstOrNull()

                    ReminderScheduler.reconcileAllReminders(context, lectures, exams, settings)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

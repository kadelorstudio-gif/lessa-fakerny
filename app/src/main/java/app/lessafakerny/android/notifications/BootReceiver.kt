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

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        Log.d("BootReceiver", "Received action: $action")

        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON" ||
            action == Intent.ACTION_LOCKED_BOOT_COMPLETED) {

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val lectures = db.lectureDao().getAllLectures().firstOrNull() ?: emptyList()
                    val exams = db.examDao().getAllExams().firstOrNull() ?: emptyList()
                    val settings = db.appSettingsDao().getSettings().firstOrNull()

                    ReminderScheduler.reconcileAllReminders(context, lectures, exams, settings)
                    Log.d("BootReceiver", "Successfully reconciled ${lectures.size} lectures and ${exams.size} exams")
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error restoring reminders on boot", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}

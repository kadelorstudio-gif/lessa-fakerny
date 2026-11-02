package app.lessafakerny.android.data

import android.content.Context
import android.util.Log
import app.lessafakerny.android.notifications.ReminderScheduler
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleRepository(
    private val context: Context,
    private val db: AppDatabase
) {
    val lectureDao = db.lectureDao()
    val examDao = db.examDao()
    val settingsDao = db.appSettingsDao()
    val attendanceDao = db.attendanceDao()

    val allLectures: Flow<List<Lecture>> = lectureDao.getAllLectures()
    val allExams: Flow<List<Exam>> = examDao.getAllExams()
    val settings: Flow<AppSettings?> = settingsDao.getSettings()
    val allAttendanceRecords: Flow<List<AttendanceRecord>> = attendanceDao.getAllAttendanceRecords()

    fun getLecturesForDay(dayOfWeek: Int): Flow<List<Lecture>> = lectureDao.getLecturesForDay(dayOfWeek)

    fun getAttendanceForDate(dateString: String): Flow<List<AttendanceRecord>> =
        attendanceDao.getAttendanceForDate(dateString)

    suspend fun initDefaultDataIfEmpty() {
        val currentSettings = settings.firstOrNull()
        if (currentSettings == null) {
            settingsDao.insertOrUpdate(AppSettings())
        }
        // Do not inject fake/demo lectures or exams: fresh installation starts cleanly with empty timetable.
        // Reconcile reminders for whatever real user data exists
        val currentLectures = allLectures.firstOrNull() ?: emptyList()
        val currentExams = allExams.firstOrNull() ?: emptyList()
        ReminderScheduler.reconcileAllReminders(context, currentLectures, currentExams, currentSettings ?: AppSettings())
    }

    suspend fun addLecture(lecture: Lecture): Long {
        val id = lectureDao.insertLecture(lecture)
        val created = lecture.copy(id = id)
        val currentSett = settings.firstOrNull()
        ReminderScheduler.scheduleLectureReminders(context, created, currentSett)
        syncLecturesToCloud()
        return id
    }

    suspend fun updateLecture(lecture: Lecture) {
        lectureDao.updateLecture(lecture)
        val currentSett = settings.firstOrNull()
        ReminderScheduler.cancelLectureReminders(context, lecture.id)
        ReminderScheduler.scheduleLectureReminders(context, lecture, currentSett)
        syncLecturesToCloud()
    }

    suspend fun deleteLecture(lecture: Lecture) {
        ReminderScheduler.cancelLectureReminders(context, lecture.id)
        attendanceDao.deleteForLecture(lecture.id)
        lectureDao.deleteLecture(lecture)
        deleteLectureFromCloud(lecture.id)
    }

    suspend fun recordAttendanceForDate(lectureId: Long, dateString: String, isPresent: Boolean) {
        if (isPresent) {
            attendanceDao.recordAttendance(
                AttendanceRecord(
                    lectureId = lectureId,
                    dateString = dateString,
                    status = "PRESENT",
                    timestamp = System.currentTimeMillis()
                )
            )
        } else {
            attendanceDao.removeAttendance(lectureId, dateString)
        }
    }

    suspend fun addExam(exam: Exam): Long {
        val id = examDao.insertExam(exam)
        val created = exam.copy(id = id)
        val currentSett = settings.firstOrNull()
        ReminderScheduler.scheduleExamReminders(context, created, currentSett)
        syncExamsToCloud()
        return id
    }

    suspend fun updateExam(exam: Exam) {
        examDao.updateExam(exam)
        val currentSett = settings.firstOrNull()
        ReminderScheduler.cancelExamReminders(context, exam.id)
        ReminderScheduler.scheduleExamReminders(context, exam, currentSett)
        syncExamsToCloud()
    }

    suspend fun deleteExam(exam: Exam) {
        ReminderScheduler.cancelExamReminders(context, exam.id)
        examDao.deleteExam(exam)
        deleteExamFromCloud(exam.id)
    }

    suspend fun saveSettings(settings: AppSettings) {
        settingsDao.insertOrUpdate(settings)
        val lectures = allLectures.firstOrNull() ?: emptyList()
        val exams = allExams.firstOrNull() ?: emptyList()
        ReminderScheduler.reconcileAllReminders(context, lectures, exams, settings)
    }

    suspend fun clearAllData() {
        val lectures = allLectures.firstOrNull() ?: emptyList()
        val exams = allExams.firstOrNull() ?: emptyList()
        lectures.forEach { ReminderScheduler.cancelLectureReminders(context, it.id) }
        exams.forEach { ReminderScheduler.cancelExamReminders(context, it.id) }

        lectureDao.getAllLectures().firstOrNull()?.forEach { lectureDao.deleteLecture(it) }
        examDao.getAllExams().firstOrNull()?.forEach { examDao.deleteExam(it) }
        attendanceDao.clearAll()
    }

    private fun deleteLectureFromCloud(lectureId: Long) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users").document(user.uid)
                    .collection("lectures").document(lectureId.toString())
                    .delete().await()
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Failed to delete lecture from cloud", e)
            }
        }
    }

    private fun deleteExamFromCloud(examId: Long) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("users").document(user.uid)
                    .collection("exams").document(examId.toString())
                    .delete().await()
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Failed to delete exam from cloud", e)
            }
        }
    }

    fun syncLecturesToCloud() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val lectures = allLectures.firstOrNull() ?: return@launch
                val firestore = FirebaseFirestore.getInstance()
                val batch = firestore.batch()
                val userRef = firestore.collection("users").document(user.uid).collection("lectures")

                lectures.forEach { lecture ->
                    val docRef = userRef.document(lecture.id.toString())
                    batch.set(docRef, lecture)
                }
                batch.commit().await()
                Log.d("ScheduleRepository", "Synced lectures to Firestore successfully")
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Firestore sync failed", e)
            }
        }
    }

    fun syncExamsToCloud() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val exams = allExams.firstOrNull() ?: return@launch
                val firestore = FirebaseFirestore.getInstance()
                val batch = firestore.batch()
                val userRef = firestore.collection("users").document(user.uid).collection("exams")

                exams.forEach { exam ->
                    val docRef = userRef.document(exam.id.toString())
                    batch.set(docRef, exam)
                }
                batch.commit().await()
                Log.d("ScheduleRepository", "Synced exams to Firestore successfully")
            } catch (e: Exception) {
                Log.e("ScheduleRepository", "Firestore exam sync failed", e)
            }
        }
    }

    suspend fun syncWithCloudOnAuth() {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        try {
            val firestore = FirebaseFirestore.getInstance()
            val cloudLecturesSnap = firestore.collection("users").document(user.uid).collection("lectures").get().await()
            val cloudLectures = cloudLecturesSnap.toObjects(Lecture::class.java)

            val localLectures = allLectures.firstOrNull() ?: emptyList()
            val localIds = localLectures.map { it.id }.toSet()

            // Merge cloud lectures that aren't present locally
            cloudLectures.forEach { cl ->
                if (!localIds.contains(cl.id)) {
                    lectureDao.insertLecture(cl)
                }
            }

            // Sync any local lectures to cloud that aren't in cloud yet
            syncLecturesToCloud()

            val cloudExamsSnap = firestore.collection("users").document(user.uid).collection("exams").get().await()
            val cloudExams = cloudExamsSnap.toObjects(Exam::class.java)

            val localExams = allExams.firstOrNull() ?: emptyList()
            val localExamIds = localExams.map { it.id }.toSet()

            cloudExams.forEach { ce ->
                if (!localExamIds.contains(ce.id)) {
                    examDao.insertExam(ce)
                }
            }

            syncExamsToCloud()

            // Reconcile reminders
            val currentSett = settings.firstOrNull()
            val updatedLectures = allLectures.firstOrNull() ?: emptyList()
            val updatedExams = allExams.firstOrNull() ?: emptyList()
            ReminderScheduler.reconcileAllReminders(context, updatedLectures, updatedExams, currentSett)
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "syncWithCloudOnAuth failed", e)
        }
    }
}

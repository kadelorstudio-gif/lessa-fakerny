package app.lessafakerny.android.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import app.lessafakerny.android.data.AppDatabase
import app.lessafakerny.android.data.AppSettings
import app.lessafakerny.android.data.AttendanceRecord
import app.lessafakerny.android.data.Exam
import app.lessafakerny.android.data.Lecture
import app.lessafakerny.android.data.ScheduleRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ScheduleRepository = ScheduleRepository(
        application.applicationContext,
        AppDatabase.getDatabase(application)
    )

    val currentTodayDayOfWeek: Int
        get() {
            val cal = Calendar.getInstance()
            return cal.get(Calendar.DAY_OF_WEEK)
        }

    val todayDateString: String
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            return sdf.format(Date())
        }

    // Weekly timetable selected day (for Section 2)
    private val _selectedTimetableDay = MutableStateFlow(currentTodayDayOfWeek)
    val selectedTimetableDay: StateFlow<Int> = _selectedTimetableDay.asStateFlow()

    fun selectTimetableDay(day: Int) {
        _selectedTimetableDay.value = day
    }

    val allLectures: StateFlow<List<Lecture>> = repository.allLectures
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allExams: StateFlow<List<Exam>> = repository.allExams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val settings: StateFlow<AppSettings?> = repository.settings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val todayAttendance: StateFlow<List<AttendanceRecord>> = repository.getAttendanceForDate(todayDateString)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttendanceRecords: StateFlow<List<AttendanceRecord>> = repository.allAttendanceRecords
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _nowMillis = MutableStateFlow(System.currentTimeMillis())
    val nowMillis: StateFlow<Long> = _nowMillis.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initDefaultDataIfEmpty()
        }
        startCountdownTicker()
    }

    private fun startCountdownTicker() {
        viewModelScope.launch {
            while (true) {
                _nowMillis.value = System.currentTimeMillis()
                delay(1000)
            }
        }
    }

    fun addOrUpdateLecture(
        id: Long = 0,
        subjectName: String,
        doctorName: String,
        hallNumber: String,
        hallName: String,
        dayOfWeek: Int,
        startTimeMinutes: Int,
        endTimeMinutes: Int,
        cardColorHex: String,
        requiredTools: String
    ) {
        viewModelScope.launch {
            val lecture = Lecture(
                id = id,
                subjectName = subjectName,
                doctorName = doctorName,
                hallNumber = hallNumber,
                hallName = hallName,
                dayOfWeek = dayOfWeek,
                startTimeMinutes = startTimeMinutes,
                endTimeMinutes = endTimeMinutes,
                cardColorHex = cardColorHex,
                requiredTools = requiredTools
            )
            if (id == 0L) {
                repository.addLecture(lecture)
            } else {
                repository.updateLecture(lecture)
            }
        }
    }

    fun deleteLecture(lecture: Lecture) {
        viewModelScope.launch {
            repository.deleteLecture(lecture)
        }
    }

    fun toggleTodayAttendance(lectureId: Long, currentAttended: Boolean) {
        viewModelScope.launch {
            repository.recordAttendanceForDate(lectureId, todayDateString, !currentAttended)
        }
    }

    fun addOrUpdateExam(
        id: Long = 0,
        subjectName: String,
        examDateMillis: Long,
        hallLocation: String,
        allowedTools: String,
        cardColorHex: String
    ) {
        viewModelScope.launch {
            val exam = Exam(
                id = id,
                subjectName = subjectName,
                examDateMillis = examDateMillis,
                hallLocation = hallLocation,
                allowedTools = allowedTools,
                cardColorHex = cardColorHex
            )
            if (id == 0L) {
                repository.addExam(exam)
            } else {
                repository.updateExam(exam)
            }
        }
    }

    fun deleteExam(exam: Exam) {
        viewModelScope.launch {
            repository.deleteExam(exam)
        }
    }

    fun updateSettings(newSettings: AppSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.clearAllData()
        }
    }

    fun unlockThemeWithRewardedAd(themeId: String, onUnlocked: () -> Unit) {
        viewModelScope.launch {
            val current = settings.value ?: AppSettings()
            val existingUnlocked = current.unlockedThemeIds.split(",").map { it.trim() }.toSet()
            val updatedUnlocked = (existingUnlocked + themeId).joinToString(",")

            repository.saveSettings(
                current.copy(
                    activeThemeId = themeId,
                    unlockedThemeIds = updatedUnlocked
                )
            )
            onUnlocked()
        }
    }

    fun syncWithCloud() {
        viewModelScope.launch {
            repository.syncWithCloudOnAuth()
        }
    }
}

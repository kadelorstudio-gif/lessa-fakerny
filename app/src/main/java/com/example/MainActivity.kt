package com.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.content.ContextCompat
import com.example.data.Exam
import com.example.data.Lecture
import com.example.notifications.NotificationHelper
import com.example.ui.components.AppScreen
import com.example.ui.components.GlassBackground
import com.example.ui.components.GlassBottomNavBar
import com.example.ui.screens.AddEditExamDialog
import com.example.ui.screens.AddEditLectureDialog
import com.example.ui.screens.AuthDialog
import com.example.ui.screens.ExamsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MainViewModel
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.TimetableScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.LessafakernyTheme
import com.example.util.LocaleManager
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            FirebaseApp.initializeApp(this)
            NotificationHelper.createNotificationChannels(this)
            Thread {
                try {
                    com.google.android.gms.ads.MobileAds.initialize(this) {}
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val targetScreenExtra = intent?.getStringExtra(NotificationHelper.EXTRA_TARGET_SCREEN)
        val initialScreen = when (targetScreenExtra) {
            "EXAMS" -> AppScreen.EXAMS
            "TIMETABLE" -> AppScreen.TIMETABLE
            "SETTINGS" -> AppScreen.SETTINGS
            else -> AppScreen.HOME
        }

        setContent {
            LessafakernyTheme {
                var isSplashActive by remember { mutableStateOf(targetScreenExtra == null) }

                if (isSplashActive) {
                    SplashScreen(onSplashFinished = { isSplashActive = false })
                } else {
                    MainAppContent(viewModel = viewModel, defaultScreen = initialScreen)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel, defaultScreen: AppScreen = AppScreen.HOME) {
    val context = LocalContext.current
    var currentScreen by remember { mutableStateOf(defaultScreen) }
    val settingsState by viewModel.settings.collectAsState()
    val activeThemeId = settingsState?.activeThemeId ?: "NEON_MIDNIGHT"
    val language = settingsState?.language ?: "ar"

    val layoutDirection = if (language == "ar") LayoutDirection.Rtl else LayoutDirection.Ltr
    val strings = LocaleManager.getStrings(language)

    // Runtime Notification Permission Request for Android 13+
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Dialog States
    var showAddLectureDialog by remember { mutableStateOf(false) }
    var lectureToEdit by remember { mutableStateOf<Lecture?>(null) }
    var initialDayForAddLecture by remember { mutableStateOf(viewModel.currentTodayDayOfWeek) }

    var showAddExamDialog by remember { mutableStateOf(false) }
    var examToEdit by remember { mutableStateOf<Exam?>(null) }

    var showAuthDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = currentScreen != AppScreen.HOME) {
        currentScreen = AppScreen.HOME
    }

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        GlassBackground(themeId = activeThemeId) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                bottomBar = {
                    GlassBottomNavBar(
                        currentScreen = currentScreen,
                        onScreenSelected = { currentScreen = it },
                        strings = strings
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        AppScreen.HOME -> HomeScreen(
                            viewModel = viewModel,
                            onNavigateToTimetable = { currentScreen = AppScreen.TIMETABLE },
                            onAddLectureClick = {
                                lectureToEdit = null
                                initialDayForAddLecture = viewModel.currentTodayDayOfWeek
                                showAddLectureDialog = true
                            },
                            onEditLectureClick = { lecture ->
                                lectureToEdit = lecture
                                initialDayForAddLecture = lecture.dayOfWeek
                                showAddLectureDialog = true
                            },
                            strings = strings,
                            language = language
                        )

                        AppScreen.TIMETABLE -> TimetableScreen(
                            viewModel = viewModel,
                            onAddLectureClick = { day ->
                                lectureToEdit = null
                                initialDayForAddLecture = day
                                showAddLectureDialog = true
                            },
                            onEditLectureClick = { lecture ->
                                lectureToEdit = lecture
                                initialDayForAddLecture = lecture.dayOfWeek
                                showAddLectureDialog = true
                            },
                            strings = strings,
                            language = language
                        )

                        AppScreen.EXAMS -> ExamsScreen(
                            viewModel = viewModel,
                            onAddExamClick = {
                                examToEdit = null
                                showAddExamDialog = true
                            },
                            onEditExamClick = { exam ->
                                examToEdit = exam
                                showAddExamDialog = true
                            },
                            strings = strings,
                            language = language
                        )

                        AppScreen.SETTINGS -> SettingsScreen(
                            viewModel = viewModel,
                            onOpenAuthDialog = { showAuthDialog = true },
                            strings = strings,
                            language = language
                        )

                        AppScreen.PROFILE -> UserProfileScreen(
                            onOpenAuthDialog = { showAuthDialog = true },
                            strings = strings,
                            language = language
                        )
                    }
                }
            }

            // Dialogs
            if (showAddLectureDialog) {
                AddEditLectureDialog(
                    lectureToEdit = lectureToEdit,
                    initialDayOfWeek = initialDayForAddLecture,
                    onDismiss = { showAddLectureDialog = false },
                    onSave = { subjectName, doctorName, hallNumber, hallName, dayOfWeek, startTimeMinutes, endTimeMinutes, cardColorHex, requiredTools ->
                        viewModel.addOrUpdateLecture(
                            id = lectureToEdit?.id ?: 0L,
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
                        showAddLectureDialog = false
                    },
                    strings = strings,
                    language = language
                )
            }

            if (showAddExamDialog) {
                AddEditExamDialog(
                    examToEdit = examToEdit,
                    onDismiss = { showAddExamDialog = false },
                    onSave = { subjectName, examDateMillis, hallLocation, allowedTools, cardColorHex ->
                        viewModel.addOrUpdateExam(
                            id = examToEdit?.id ?: 0L,
                            subjectName = subjectName,
                            examDateMillis = examDateMillis,
                            hallLocation = hallLocation,
                            allowedTools = allowedTools,
                            cardColorHex = cardColorHex
                        )
                        showAddExamDialog = false
                    },
                    strings = strings,
                    language = language
                )
            }

            if (showAuthDialog) {
                AuthDialog(
                    onDismiss = { showAuthDialog = false },
                    onAuthSuccess = {
                        showAuthDialog = false
                        viewModel.syncWithCloud()
                    },
                    strings = strings,
                    language = language
                )
            }
        }
    }
}

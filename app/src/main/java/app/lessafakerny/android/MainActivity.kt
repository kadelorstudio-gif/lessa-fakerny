package app.lessafakerny.android

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import app.lessafakerny.android.data.Exam
import app.lessafakerny.android.data.Lecture
import app.lessafakerny.android.notifications.NotificationHelper
import app.lessafakerny.android.ui.components.AppScreen
import app.lessafakerny.android.ui.components.GlassBackground
import app.lessafakerny.android.ui.components.GlassBottomNavBar
import app.lessafakerny.android.ui.screens.AddEditExamDialog
import app.lessafakerny.android.ui.screens.AddEditLectureDialog
import app.lessafakerny.android.ui.screens.AuthDialog
import app.lessafakerny.android.ui.screens.AttendanceStatsScreen
import app.lessafakerny.android.ui.screens.ExamsScreen
import app.lessafakerny.android.ui.screens.HomeScreen
import app.lessafakerny.android.ui.screens.LearnScreen
import app.lessafakerny.android.ui.screens.MainViewModel
import app.lessafakerny.android.ui.screens.SettingsScreen
import app.lessafakerny.android.ui.screens.SplashScreen
import app.lessafakerny.android.ui.screens.TimetableScreen
import app.lessafakerny.android.ui.theme.LessafakernyTheme
import app.lessafakerny.android.util.LocaleManager
import com.google.firebase.FirebaseApp

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            FirebaseApp.initializeApp(this)
            NotificationHelper.createNotificationChannels(this)
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
            val settingsState by viewModel.settings.collectAsState()
            val activeThemeId = settingsState?.activeThemeId ?: "NEON_MIDNIGHT"

            LessafakernyTheme(activeThemeId = activeThemeId) {
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

    // Robust Back Handling: return to HOME first, and require double press on HOME to exit
    var lastBackPressMillis by remember { mutableStateOf(0L) }
    BackHandler {
        if (currentScreen != AppScreen.HOME) {
            currentScreen = AppScreen.HOME
        } else {
            val now = System.currentTimeMillis()
            if (now - lastBackPressMillis < 2000L) {
                (context as? AppCompatActivity)?.finish()
            } else {
                lastBackPressMillis = now
                val exitMsg = if (language == "en") "Press back again to exit" else "اضغط مرة أخرى للخروج من التطبيق"
                android.widget.Toast.makeText(context, exitMsg, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
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
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            (fadeIn(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                             scaleIn(initialScale = 0.96f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow)))
                                .togetherWith(
                                    fadeOut(animationSpec = spring(stiffness = Spring.StiffnessMediumLow)) +
                                    scaleOut(targetScale = 1.02f, animationSpec = spring(stiffness = Spring.StiffnessMediumLow))
                                )
                        },
                        label = "screen_transition"
                    ) { targetScreen ->
                        when (targetScreen) {
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

                            AppScreen.LEARN -> LearnScreen(
                                viewModel = viewModel
                            )

                            AppScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel,
                                onOpenAuthDialog = { showAuthDialog = true },
                                onNavigateToPrivacyPolicy = { currentScreen = AppScreen.PRIVACY_POLICY },
                                onNavigateToAbout = { currentScreen = AppScreen.ABOUT },
                                strings = strings,
                                language = language
                            )

                            AppScreen.STATS -> AttendanceStatsScreen(
                                viewModel = viewModel,
                                strings = strings,
                                language = language
                            )

                            AppScreen.PRIVACY_POLICY -> app.lessafakerny.android.ui.screens.PrivacyPolicyScreen(
                                onNavigateBack = { currentScreen = AppScreen.SETTINGS },
                                strings = strings,
                                language = language
                            )

                            AppScreen.ABOUT -> app.lessafakerny.android.ui.screens.AboutScreen(
                                onNavigateBack = { currentScreen = AppScreen.SETTINGS },
                                strings = strings,
                                language = language
                            )
                        }
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
                    onNavigateToPrivacyPolicy = {
                        showAuthDialog = false
                        currentScreen = AppScreen.PRIVACY_POLICY
                    },
                    onNavigateToGuide = {
                        showAuthDialog = false
                        currentScreen = AppScreen.ABOUT
                    },
                    strings = strings,
                    language = language
                )
            }
        }
    }
}

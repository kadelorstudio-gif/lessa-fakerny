package app.lessafakerny.android.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import app.lessafakerny.android.notifications.RingtoneCatalog
import app.lessafakerny.android.notifications.RingtonePreviewPlayer
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.lessafakerny.android.data.AppSettings
import app.lessafakerny.android.notifications.NotificationHelper
import app.lessafakerny.android.ui.components.GlassBadge
import app.lessafakerny.android.ui.components.GlassButton
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.components.GlassOutlineButton
import app.lessafakerny.android.ui.theme.GlassBorder
import app.lessafakerny.android.ui.theme.NeonBlue
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.StatusError
import app.lessafakerny.android.ui.theme.StatusSuccess
import app.lessafakerny.android.ui.theme.StatusWarning
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.ui.theme.TextSecondary
import app.lessafakerny.android.util.AppStrings
import app.lessafakerny.android.util.LocaleManager
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    viewModel: MainViewModel,
    onOpenAuthDialog: () -> Unit,
    onNavigateToPrivacyPolicy: () -> Unit = {},
    onNavigateToAbout: () -> Unit = {},
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val context = LocalContext.current
    val settingsState by viewModel.settings.collectAsState()
    val currentSettings = settingsState ?: AppSettings()
    val isEn = language.equals("en", ignoreCase = true)

    val firebaseUser = FirebaseAuth.getInstance().currentUser

    var showRewardedAdDialog by remember { mutableStateOf(false) }
    var selectedThemeToUnlock by remember { mutableStateOf<String?>(null) }
    var showResetConfirmationDialog by remember { mutableStateOf(false) }

    val currentlyPlayingTone by RingtonePreviewPlayer.currentlyPlayingKey.collectAsState()
    var pendingToneKey by remember(currentSettings.soundTone) { mutableStateOf(currentSettings.soundTone) }

    DisposableEffect(Unit) {
        onDispose {
            RingtonePreviewPlayer.stop()
        }
    }

    val unlockedThemesSet = remember(currentSettings.unlockedThemeIds) {
        currentSettings.unlockedThemeIds.split(",").map { it.trim() }.toSet()
    }

    var showProfileDialog by remember { mutableStateOf(false) }

    if (showProfileDialog) {
        UserProfileDialog(
            user = firebaseUser,
            userRole = currentSettings.userRole,
            strings = strings,
            language = language,
            onDismiss = { showProfileDialog = false },
            onOpenAuth = {
                showProfileDialog = false
                onOpenAuthDialog()
            },
            onLogout = { viewModel.syncWithCloud() }
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .testTag("settings_screen")
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = strings.settingsHeaderTitle,
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.settingsHeaderSubtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(BorderStroke(1.2.dp, NeonCyan), CircleShape)
                        .clickable { showProfileDialog = true }
                        .testTag("settings_profile_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = NeonCyan,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Account & Cloud Sync Section
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(22.dp),
                        useDualNeonBorder = firebaseUser != null,
                        testTag = "account_card"
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan.copy(alpha = 0.14f))
                                        .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(28.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (firebaseUser != null) firebaseUser.email ?: (if (isEn) "Connected Account" else "حساب متصل") else strings.offlineModeLabel,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (firebaseUser != null) strings.onlineModeDesc else strings.offlineModeDesc,
                                        color = if (firebaseUser != null) StatusSuccess else SoftLavender,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            if (firebaseUser == null) {
                                GlassButton(
                                    onClick = onOpenAuthDialog,
                                    modifier = Modifier.fillMaxWidth(),
                                    testTag = "login_btn"
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF050816))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(strings.loginButtonLabel, fontWeight = FontWeight.Bold)
                                    }
                                }
                            } else {
                                GlassButton(
                                    onClick = {
                                        FirebaseAuth.getInstance().signOut()
                                        Toast.makeText(context, if (isEn) "Signed out successfully" else "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    accentColor = StatusError,
                                    testTag = "logout_btn"
                                ) {
                                    Text(strings.logoutButtonLabel, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                        }
                    }
                }

                // 1.8 Time Format Toggle Card (12h vs 24h)
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonBlue,
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = strings.timeFormatTitle,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (currentSettings.is24HourFormat) strings.timeFormat24hLabel else strings.timeFormat12hLabel,
                                    color = SoftLavender,
                                    fontSize = 11.sp
                                )
                            }

                            Switch(
                                checked = currentSettings.is24HourFormat,
                                onCheckedChange = { viewModel.updateTimeFormat(it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF050816),
                                    checkedTrackColor = NeonBlue
                                )
                            )
                        }
                    }
                }

                // 2. Granular Lecture Reminders Section
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(22.dp),
                        testTag = "lecture_reminders_card"
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Notifications, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(strings.lectureRemindersTitle, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                                Switch(
                                    checked = currentSettings.lectureRemindersEnabled,
                                    onCheckedChange = {
                                        viewModel.updateSettings(currentSettings.copy(lectureRemindersEnabled = it))
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF050816), checkedTrackColor = StatusSuccess)
                                )
                            }

                            if (currentSettings.lectureRemindersEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))

                                ReminderToggleRow(strings.remind30mLabel, currentSettings.remind30MinBefore) {
                                    viewModel.updateSettings(currentSettings.copy(remind30MinBefore = it))
                                }
                                ReminderToggleRow(strings.remind15mLabel, currentSettings.remind15MinBefore) {
                                    viewModel.updateSettings(currentSettings.copy(remind15MinBefore = it))
                                }
                                ReminderToggleRow(strings.remind5mLabel, currentSettings.remind5MinBefore) {
                                    viewModel.updateSettings(currentSettings.copy(remind5MinBefore = it))
                                }
                                ReminderToggleRow(strings.remindStartLabel, currentSettings.remindAtStart) {
                                    viewModel.updateSettings(currentSettings.copy(remindAtStart = it))
                                }
                            }
                        }
                    }
                }

                // 3. Granular Exam Reminders Section
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonViolet,
                        shape = RoundedCornerShape(22.dp),
                        testTag = "exam_reminders_card"
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Timer, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(strings.examRemindersTitle, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                }
                                Switch(
                                    checked = currentSettings.examRemindersEnabled,
                                    onCheckedChange = {
                                        viewModel.updateSettings(currentSettings.copy(examRemindersEnabled = it))
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF050816), checkedTrackColor = StatusSuccess)
                                )
                            }

                            if (currentSettings.examRemindersEnabled) {
                                Spacer(modifier = Modifier.height(12.dp))

                                ReminderToggleRow(strings.remindExam24hLabel, currentSettings.remindExam24hBefore) {
                                    viewModel.updateSettings(currentSettings.copy(remindExam24hBefore = it))
                                }
                                ReminderToggleRow(strings.remindExam2hLabel, currentSettings.remindExam2hBefore) {
                                    viewModel.updateSettings(currentSettings.copy(remindExam2hBefore = it))
                                }
                                ReminderToggleRow(strings.remindExam30mLabel, currentSettings.remindExam30mBefore) {
                                    viewModel.updateSettings(currentSettings.copy(remindExam30mBefore = it))
                                }
                            }
                        }
                    }
                }

                // 4. Dedicated Notification Ringtones Section («نغمات الإشعارات»)
                item {
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("notification_ringtones_card"),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(24.dp),
                        useDualNeonBorder = true
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            // Section Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(38.dp)
                                            .clip(CircleShape)
                                            .background(NeonCyan.copy(alpha = 0.16f))
                                            .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GraphicEq,
                                            contentDescription = null,
                                            tint = NeonCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = strings.ringtonesSectionTitle,
                                            color = TextPrimary,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = strings.ringtonesSectionSubtitle,
                                            color = SoftLavender,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                // Active tone chip
                                val activeOption = RingtoneCatalog.getOptionByKey(currentSettings.soundTone)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(NeonCyan.copy(alpha = 0.18f))
                                        .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isEn) activeOption.nameEn else activeOption.nameAr,
                                        color = NeonCyan,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Ringtones List
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                RingtoneCatalog.options.forEach { option ->
                                    val isSelected = pendingToneKey.equals(option.key, ignoreCase = true)
                                    val isPlaying = currentlyPlayingTone.equals(option.key, ignoreCase = true)

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(
                                                if (isSelected) NeonCyan.copy(alpha = 0.14f) else SurfaceElevated
                                            )
                                            .border(
                                                BorderStroke(
                                                    if (isSelected) 1.5.dp else 1.dp,
                                                    if (isSelected) NeonCyan else GlassBorder
                                                ),
                                                RoundedCornerShape(16.dp)
                                            )
                                            .clickable {
                                                pendingToneKey = option.key
                                            }
                                            .padding(horizontal = 12.dp, vertical = 10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            // Icon
                                            Box(
                                                modifier = Modifier
                                                    .size(34.dp)
                                                    .clip(CircleShape)
                                                    .background(
                                                        if (isSelected) NeonCyan.copy(alpha = 0.25f) else Color(0xFF101A35)
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = option.icon,
                                                    contentDescription = null,
                                                    tint = if (isSelected) NeonCyan else TextSecondary,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(10.dp))

                                            // Name & description
                                            Column(modifier = Modifier.weight(1f)) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = if (isEn) option.nameEn else option.nameAr,
                                                        color = if (isSelected) NeonCyan else TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                                    )
                                                    if (isSelected) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "✓",
                                                            color = StatusSuccess,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                                Text(
                                                    text = if (isEn) option.descriptionEn else option.descriptionAr,
                                                    color = if (isSelected) SoftLavender else TextMuted,
                                                    fontSize = 10.sp,
                                                    lineHeight = 14.sp
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            // Play / Stop preview button
                                            if (option.key.equals("SILENT", ignoreCase = true)) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFF1E293B))
                                                        .padding(horizontal = 8.dp, vertical = 5.dp)
                                                ) {
                                                    Text(
                                                        text = if (isEn) "Silent" else "صامت",
                                                        color = TextMuted,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(
                                                            if (isPlaying) StatusError.copy(alpha = 0.25f) else NeonCyan.copy(alpha = 0.15f)
                                                        )
                                                        .border(
                                                            BorderStroke(
                                                                1.dp,
                                                                if (isPlaying) StatusError else NeonCyan.copy(alpha = 0.4f)
                                                            ),
                                                            RoundedCornerShape(10.dp)
                                                        )
                                                        .clickable {
                                                            if (isPlaying) {
                                                                RingtonePreviewPlayer.stop()
                                                            } else {
                                                                RingtonePreviewPlayer.play(context, option.key)
                                                            }
                                                        }
                                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                                ) {
                                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                                        Icon(
                                                            imageVector = if (isPlaying) Icons.Default.Stop else Icons.Default.VolumeUp,
                                                            contentDescription = null,
                                                            tint = if (isPlaying) StatusError else NeonCyan,
                                                            modifier = Modifier.size(14.dp)
                                                        )
                                                        Spacer(modifier = Modifier.width(4.dp))
                                                        Text(
                                                            text = if (isPlaying) strings.previewStop else strings.previewPlay,
                                                            color = if (isPlaying) StatusError else NeonCyan,
                                                            fontSize = 11.sp,
                                                            fontWeight = FontWeight.Bold
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action buttons: Save Tone & Reset to Default
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                GlassButton(
                                    onClick = {
                                        RingtonePreviewPlayer.stop()
                                        viewModel.updateSettings(currentSettings.copy(soundTone = pendingToneKey))
                                        NotificationHelper.createNotificationChannels(context, pendingToneKey)
                                        Toast.makeText(context, strings.ringtoneSavedToast, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    accentColor = NeonCyan,
                                    testTag = "save_ringtone_btn"
                                ) {
                                    Text(
                                        text = strings.saveRingtoneBtn,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }

                                GlassOutlineButton(
                                    onClick = {
                                        RingtonePreviewPlayer.stop()
                                        pendingToneKey = "DEFAULT"
                                        viewModel.updateSettings(currentSettings.copy(soundTone = "DEFAULT"))
                                        NotificationHelper.createNotificationChannels(context, "DEFAULT")
                                        Toast.makeText(context, strings.ringtoneResetToast, Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.weight(1f),
                                    borderColor = TextMuted,
                                    testTag = "reset_ringtone_btn"
                                ) {
                                    Text(
                                        text = strings.resetDefaultRingtoneBtn,
                                        color = TextSecondary,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Vibration toggle switch
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(strings.vibrationToggleLabel, color = TextSecondary, fontSize = 13.sp)
                                Switch(
                                    checked = currentSettings.vibrationEnabled,
                                    onCheckedChange = {
                                        viewModel.updateSettings(currentSettings.copy(vibrationEnabled = it))
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color(0xFF050816),
                                        checkedTrackColor = StatusSuccess
                                    )
                                )
                            }
                        }
                    }
                }

                // 5. Language Selection (Arabic / English)
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonBlue,
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.languageSectionTitle, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NeonCyan.copy(alpha = 0.18f))
                                    .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), RoundedCornerShape(14.dp))
                                    .clickable {
                                        val newLang = if (currentSettings.language == "ar") "en" else "ar"
                                        viewModel.updateSettings(currentSettings.copy(language = newLang))
                                        Toast.makeText(context, if (newLang == "ar") "تم تفعيل اللغة العربية" else "Switched to English", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (currentSettings.language == "ar") "العربية 🇪🇬" else "English 🇬🇧",
                                    color = NeonCyan,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 6. Premium Themes Gallery
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonViolet,
                        shape = RoundedCornerShape(22.dp),
                        testTag = "themes_gallery_card"
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Palette, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.themesSectionTitle, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(strings.themesSectionSubtitle, color = SoftLavender, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            val themesList = listOf(
                                Triple("NEON_MIDNIGHT", "Neon Midnight 💜", if (isEn) "Vibrant Violet & Glowing Cyan (Official)" else "البنفسجي والسيان المتوهج (المظهر الرسمي)"),
                                Triple("SAPPHIRE_FROST", "Sapphire Frost 💙", if (isEn) "Royal Blue & Frost Accents" else "الأزرق الملكي والثلجي المشرق"),
                                Triple("EMERALD_GLOW", "Emerald Glow 💚", if (isEn) "Neon Emerald & Deep Forest" else "الزمرد النيوني والغابات الهادئة"),
                                Triple("AMETHYST_SUNSET", "Amethyst Sunset 🩷", if (isEn) "Radiant Ruby & Bright Magenta" else "الياقوتي والوردي المشرق")
                            )

                            themesList.forEach { (id, name, desc) ->
                                val isActive = currentSettings.activeThemeId == id
                                val isUnlocked = unlockedThemesSet.contains(id)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isActive) NeonViolet.copy(alpha = 0.25f) else SurfaceElevated)
                                        .border(BorderStroke(1.dp, if (isActive) NeonViolet else GlassBorder), RoundedCornerShape(16.dp))
                                        .clickable {
                                            if (isUnlocked) {
                                                viewModel.updateSettings(currentSettings.copy(activeThemeId = id))
                                                Toast.makeText(context, if (isEn) "Applied theme: $name" else "تم تطبيق مظهر $name", Toast.LENGTH_SHORT).show()
                                            } else {
                                                selectedThemeToUnlock = id
                                                showRewardedAdDialog = true
                                            }
                                        }
                                        .padding(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text(desc, color = TextMuted, fontSize = 11.sp)
                                        }

                                        if (isActive) {
                                            GlassBadge(text = strings.activeBadge, color = StatusSuccess)
                                        } else if (isUnlocked) {
                                            GlassBadge(text = strings.unlockedBadge, color = NeonCyan)
                                        } else {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.Lock, contentDescription = null, tint = StatusWarning, modifier = Modifier.size(15.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                GlassBadge(text = strings.watchAdBadge, color = StatusWarning)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // 6.5 Quick Access & App Shortcuts
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(22.dp),
                        onClick = {
                            if (androidx.core.content.pm.ShortcutManagerCompat.isRequestPinShortcutSupported(context)) {
                                val pinShortcutInfo = androidx.core.content.pm.ShortcutInfoCompat.Builder(context, "lessafakerny_home_shortcut")
                                    .setShortLabel(if (isEn) "Lessa Fakerny" else "لسه فاكرني")
                                    .setLongLabel(if (isEn) "Open Lessa Fakerny" else "فتح تطبيق لسه فاكرني")
                                    .setIcon(androidx.core.graphics.drawable.IconCompat.createWithResource(context, app.lessafakerny.android.R.mipmap.ic_launcher))
                                    .setIntent(Intent(context, app.lessafakerny.android.MainActivity::class.java).apply {
                                        action = Intent.ACTION_VIEW
                                    })
                                    .build()
                                androidx.core.content.pm.ShortcutManagerCompat.requestPinShortcut(context, pinShortcutInfo, null)
                                Toast.makeText(context, if (isEn) "Shortcut request sent" else "تم إرسال طلب إضافة الاختصار", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, if (isEn) "Pinning shortcuts not supported on this device" else "الجهاز لا يدعم إضافة الاختصارات", Toast.LENGTH_SHORT).show()
                            }
                        },
                        testTag = "add_to_home_shortcut_card"
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(if (isEn) "Add Shortcut to Home Screen" else "إضافة اختصار للشاشة الرئيسية", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(if (isEn) "Pin app shortcut on home screen" else "تثبيت اختصار التطبيق على الشاشة الرئيسية", color = SoftLavender, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 6.6 About & User Guide
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonViolet,
                        shape = RoundedCornerShape(22.dp),
                        onClick = onNavigateToAbout,
                        testTag = "about_app_card"
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.AccountCircle, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(if (isEn) "About & How to Use" else "عن التطبيق ودليل الاستخدام", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(if (isEn) "App features, exam guide & version v1.0" else "إصدار التطبيق v1.0 ودليل الاستخدام", color = SoftLavender, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 6.7 Privacy Policy
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(22.dp),
                        onClick = onNavigateToPrivacyPolicy,
                        testTag = "privacy_policy_card"
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(if (isEn) "Privacy Policy" else "سياسة الخصوصية", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(if (isEn) "Data usage, cloud sync & security" else "استخدام البيانات، التشفير، والمزامنة", color = SoftLavender, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // 7. Developer & Studio Information (Requirement 10.D)
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(22.dp),
                        useDualNeonBorder = true,
                        testTag = "developer_info_card"
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(NeonCyan.copy(alpha = 0.16f))
                                        .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.5f)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Code, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(strings.developerSectionTitle, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(strings.studioNameLabel, color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, tint = SoftLavender, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(strings.studioEmailLabel, color = TextSecondary, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            GlassButton(
                                onClick = {
                                    try {
                                        val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                            data = Uri.parse("mailto:Kadelor-studio@gmail.com")
                                            putExtra(Intent.EXTRA_SUBJECT, strings.contactEmailSubject)
                                        }
                                        context.startActivity(emailIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(
                                            context,
                                            if (isEn) "No email app installed on this device" else "لم يتم العثور على تطبيق بريد إلكتروني",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                accentColor = NeonCyan,
                                testTag = "contact_dev_btn"
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF050816))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(strings.contactDevBtn, fontWeight = FontWeight.Bold, color = Color(0xFF050816))
                                }
                            }
                        }
                    }
                }

                // 8. Data Management & Reset
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = StatusError,
                        shape = RoundedCornerShape(22.dp),
                        onClick = { showResetConfirmationDialog = true }
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.DeleteForever, contentDescription = null, tint = StatusError, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(strings.clearDataTitle, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                    Text(strings.clearDataSubtitle, color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }

        // Reset Confirmation Dialog
        if (showResetConfirmationDialog) {
            AlertDialog(
                onDismissRequest = { showResetConfirmationDialog = false },
                title = { Text(strings.clearDataConfirmTitle, color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text(strings.clearDataConfirmMessage, color = TextSecondary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.clearAllData()
                            showResetConfirmationDialog = false
                            Toast.makeText(context, if (isEn) "All data cleared successfully" else "تم مسح البيانات بنجاح", Toast.LENGTH_SHORT).show()
                        }
                    ) {
                        Text(strings.clearDataConfirmBtn, color = StatusError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showResetConfirmationDialog = false }) {
                        Text(strings.cancelBtn, color = TextMuted)
                    }
                },
                containerColor = SurfaceElevated
            )
        }

        // Rewarded Video Ad Modal Simulator
        if (showRewardedAdDialog && selectedThemeToUnlock != null) {
            RewardedAdSimulatorModal(
                themeId = selectedThemeToUnlock!!,
                onDismiss = { showRewardedAdDialog = false },
                onRewardCompleted = {
                    viewModel.unlockThemeWithRewardedAd(selectedThemeToUnlock!!) {
                        showRewardedAdDialog = false
                        Toast.makeText(context, if (isEn) "Congratulations! Theme unlocked 🎉" else "مبروك! تم فتح المظهر المتميز بنجاح 🎉", Toast.LENGTH_LONG).show()
                    }
                },
                isEn = isEn
            )
        }
    }
}

@Composable
fun ReminderToggleRow(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextSecondary, fontSize = 12.sp)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF050816), checkedTrackColor = StatusSuccess)
        )
    }
}

@Composable
fun RewardedAdSimulatorModal(
    themeId: String,
    onDismiss: () -> Unit,
    onRewardCompleted: () -> Unit,
    isEn: Boolean
) {
    var progress by remember { mutableStateOf(0f) }
    var secondsLeft by remember { mutableStateOf(5) }
    var isFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        for (i in 1..5) {
            delay(1000)
            secondsLeft = 5 - i
            progress = i / 5f
        }
        isFinished = true
    }

    Dialog(onDismissRequest = {}) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            shape = RoundedCornerShape(26.dp),
            tintColor = StatusWarning,
            isGlowing = true,
            useDualNeonBorder = true
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircle,
                    contentDescription = null,
                    tint = StatusWarning,
                    modifier = Modifier.size(52.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (isEn) "Rewarded Ad Video • Test Ad" else "فيديو إعلاني مكافئ • Test Rewarded Ad",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text("ID: ${app.lessafakerny.android.util.AdConstants.REWARDED_AD_UNIT_ID}", color = TextMuted, fontSize = 10.sp)

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isEn) "Watch full ad to unlock premium theme for free" else "شاهد الإعلان بالكامل لفتح المظهر المتميز مجاناً",
                    color = SoftLavender,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(18.dp))

                if (!isFinished) {
                    Text(
                        text = if (isEn) "Reward ready in: $secondsLeft seconds" else "المكافأة جاهزة خلال: $secondsLeft ثواني",
                        color = StatusWarning,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(CircleShape),
                        color = StatusWarning,
                        trackColor = StatusWarning.copy(alpha = 0.2f),
                        strokeCap = StrokeCap.Round
                    )
                } else {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(38.dp))
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isEn) "Ad completed! 🎉" else "تمت مشاهدة الإعلان بنجاح! 🎉",
                        color = StatusSuccess,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GlassButton(
                        onClick = onRewardCompleted,
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = StatusSuccess
                    ) {
                        Text(if (isEn) "Activate Theme Now" else "تفعيل المظهر المتميز الآن", color = Color(0xFF050816), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

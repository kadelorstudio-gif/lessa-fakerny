package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AttendanceRecord
import com.example.data.Lecture
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassNativeAdCard
import com.example.ui.components.GlassOutlineButton
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusError
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppStrings
import com.example.util.LocaleManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MainViewModel,
    onNavigateToTimetable: () -> Unit,
    onAddLectureClick: () -> Unit,
    onEditLectureClick: (Lecture) -> Unit,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val allLectures by viewModel.allLectures.collectAsState()
    val todayAttendance by viewModel.todayAttendance.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()

    val currentTodayDayOfWeek = viewModel.currentTodayDayOfWeek
    val isEn = language.equals("en", ignoreCase = true)

    // Strictly today's lectures sorted chronologically by start time
    val todayLectures = remember(allLectures, currentTodayDayOfWeek) {
        allLectures.filter { it.dayOfWeek == currentTodayDayOfWeek }
            .sortedBy { it.startTimeMinutes }
    }

    val calendar = Calendar.getInstance().apply { timeInMillis = nowMillis }
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMinutesOfDay = currentHour * 60 + calendar.get(Calendar.MINUTE)
    val currentSecondsOfMinute = calendar.get(Calendar.SECOND)

    val attendedMap = remember(todayAttendance) {
        todayAttendance.associate { it.lectureId to (it.status == "PRESENT") }
    }

    val ongoingLecture = remember(todayLectures, currentMinutesOfDay) {
        todayLectures.find { currentMinutesOfDay in it.startTimeMinutes until it.endTimeMinutes }
    }

    val nextUpcomingLecture = remember(todayLectures, currentMinutesOfDay) {
        todayLectures.find { it.startTimeMinutes > currentMinutesOfDay }
    }

    val greeting = if (isEn) {
        when {
            currentHour in 4..11 -> "Good Morning 🎓"
            currentHour in 12..16 -> "Good Afternoon 📚"
            else -> "Good Evening 🌙"
        }
    } else {
        when {
            currentHour in 4..11 -> "صباح الهمّة والنشاط 🎓"
            currentHour in 12..16 -> "مساء التركيز والإنجاز 📚"
            else -> "مساء التفوق والهدوء 🌙"
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .testTag("home_screen")
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // 1. Header with greeting and date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = greeting,
                        color = NeonCyan,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val dateFormat = if (isEn) {
                        SimpleDateFormat("EEEE, d MMMM yyyy", Locale.ENGLISH)
                    } else {
                        SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))
                    }
                    Text(
                        text = dateFormat.format(calendar.time),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Attendance ratio badge for today
                val attendedCount = todayLectures.count { attendedMap[it.id] == true }
                val totalCount = todayLectures.size
                val badgeText = if (isEn) {
                    "$attendedCount/$totalCount attended"
                } else {
                    "$attendedCount/$totalCount مكتمل"
                }
                GlassBadge(
                    text = badgeText,
                    color = if (attendedCount == totalCount && totalCount > 0) StatusSuccess else SoftLavender
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Signature Hero Countdown Clock Card
            LectureClockHeroCard(
                ongoingLecture = ongoingLecture,
                nextLecture = nextUpcomingLecture,
                currentMinutesOfDay = currentMinutesOfDay,
                currentSecondsOfMinute = currentSecondsOfMinute,
                isOngoingAttended = ongoingLecture?.let { attendedMap[it.id] == true } ?: false,
                onToggleAttendance = { lecture ->
                    val isAttended = attendedMap[lecture.id] == true
                    viewModel.toggleTodayAttendance(lecture.id, isAttended)
                },
                onNavigateToTimetable = onNavigateToTimetable,
                strings = strings,
                isEn = isEn
            )

            Spacer(modifier = Modifier.height(18.dp))

            // 3. Section Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEn) "Today's Lectures (${todayLectures.size})" else "محاضرات اليوم (${todayLectures.size})",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (isEn) "View Weekly Timetable →" else "عرض الجدول الأسبوعي ←",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateToTimetable)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Lectures List / Empty State
            if (todayLectures.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.12f))
                                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🎉", fontSize = 34.sp)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = strings.emptyTodayTitle,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = strings.emptyTodaySubtitle,
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        GlassOutlineButton(
                            onClick = onNavigateToTimetable,
                            borderColor = NeonCyan
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.openTimetableAction, color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(todayLectures) { index, lecture ->
                        val isOngoing = currentMinutesOfDay in lecture.startTimeMinutes until lecture.endTimeMinutes
                        val isCompleted = currentMinutesOfDay >= lecture.endTimeMinutes
                        val isUpcoming = currentMinutesOfDay < lecture.startTimeMinutes
                        val isAttended = attendedMap[lecture.id] == true
                        val isMissed = isCompleted && !isAttended

                        val cardColor = try {
                            Color(android.graphics.Color.parseColor(lecture.cardColorHex))
                        } catch (e: Exception) {
                            NeonCyan
                        }

                        TodayLectureItemCard(
                            lecture = lecture,
                            isOngoing = isOngoing,
                            isCompleted = isCompleted,
                            isUpcoming = isUpcoming,
                            isAttended = isAttended,
                            isMissed = isMissed,
                            currentMinutesOfDay = currentMinutesOfDay,
                            cardColor = cardColor,
                            onAttendanceToggle = {
                                viewModel.toggleTodayAttendance(lecture.id, isAttended)
                            },
                            onEdit = { onEditLectureClick(lecture) },
                            onDelete = { viewModel.deleteLecture(lecture) },
                            strings = strings,
                            isEn = isEn
                        )

                        if (index == 1 && todayLectures.size > 2) {
                            GlassNativeAdCard()
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(88.dp))
                    }
                }
            }
        }

        // Luminous Glass Glowing FAB
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 84.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = NeonCyan.copy(alpha = 0.75f),
                    ambientColor = NeonBlue.copy(alpha = 0.45f)
                )
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        listOf(
                            NeonCyan,
                            NeonBlue,
                            NeonCyan
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.85f), NeonCyan, Color.White.copy(alpha = 0.4f))
                        )
                    ),
                    CircleShape
                )
                .clickable(onClick = onAddLectureClick)
                .size(58.dp)
                .testTag("add_lecture_fab"),
            contentAlignment = Alignment.Center
        ) {
            // Specular top shine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Lecture",
                tint = Color(0xFF050816),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun LectureClockHeroCard(
    ongoingLecture: Lecture?,
    nextLecture: Lecture?,
    currentMinutesOfDay: Int,
    currentSecondsOfMinute: Int,
    isOngoingAttended: Boolean,
    onToggleAttendance: (Lecture) -> Unit,
    onNavigateToTimetable: () -> Unit,
    strings: AppStrings,
    isEn: Boolean
) {
    if (ongoingLecture == null && nextLecture == null) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            tintColor = StatusSuccess,
            shape = RoundedCornerShape(24.dp)
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(StatusSuccess.copy(alpha = 0.16f))
                        .border(BorderStroke(1.dp, StatusSuccess.copy(alpha = 0.4f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEn) "All today's lectures completed! 🎓" else "أكملت جدول اليوم بنجاح! 🎓",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isEn) "No more lectures for today. Relax and recharge." else "لا توجد محاضرات قادمة اليوم. استرح وجدد طاقتك.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
        return
    }

    val activeLecture = ongoingLecture ?: nextLecture!!
    val isOngoing = ongoingLecture != null

    val totalStartSecs = activeLecture.startTimeMinutes * 60
    val totalEndSecs = activeLecture.endTimeMinutes * 60
    val currentTotalSecs = currentMinutesOfDay * 60 + currentSecondsOfMinute

    val remainingSecs = if (isOngoing) {
        (totalEndSecs - currentTotalSecs).coerceAtLeast(0)
    } else {
        (totalStartSecs - currentTotalSecs).coerceAtLeast(0)
    }

    val hours = remainingSecs / 3600
    val mins = (remainingSecs % 3600) / 60
    val secs = remainingSecs % 60

    val timeFormatted = String.format("%02d:%02d:%02d", hours, mins, secs)

    val progress = if (isOngoing) {
        val totalDuration = (totalEndSecs - totalStartSecs).coerceAtLeast(1)
        val elapsed = currentTotalSecs - totalStartSecs
        (elapsed.toFloat() / totalDuration.toFloat()).coerceIn(0f, 1f)
    } else {
        0.1f
    }

    val themeColor = if (isOngoing) StatusSuccess else NeonCyan

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        tintColor = themeColor,
        isGlowing = true,
        useDualNeonBorder = true,
        shape = RoundedCornerShape(26.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                GlassBadge(
                    text = if (isOngoing) strings.activeLectureNow else (if (isEn) "Upcoming Lecture ⏳" else "المحاضرة القادمة ⏳"),
                    color = themeColor
                )

                Text(
                    text = "${activeLecture.formatStartTime()} - ${activeLecture.formatEndTime()}",
                    color = SoftLavender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Clock Dial & Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Clock progress dial
                Box(
                    modifier = Modifier.size(92.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeWidth = 5.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        val radius = diameter / 2f
                        val center = Offset(size.width / 2f, size.height / 2f)

                        drawCircle(
                            color = themeColor.copy(alpha = 0.15f),
                            radius = radius,
                            center = center,
                            style = Stroke(width = strokeWidth)
                        )

                        drawArc(
                            brush = Brush.sweepGradient(
                                listOf(themeColor, NeonBlue, NeonMagenta, themeColor)
                            ),
                            startAngle = -90f,
                            sweepAngle = progress * 360f,
                            useCenter = false,
                            topLeft = Offset(strokeWidth / 2f, strokeWidth / 2f),
                            size = Size(diameter, diameter),
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = if (isOngoing) (if (isEn) "Remaining" else "متبقي") else (if (isEn) "Starts in" else "يبدأ في"),
                            color = TextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = if (hours > 0) "${hours}h ${mins}m" else "${mins}m ${secs}s",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activeLecture.subjectName,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "${activeLecture.doctorName} • ${activeLecture.hallName} (${activeLecture.hallNumber})",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isOngoing) strings.timeLeftToEnd else strings.startsIn,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                    Text(
                        text = timeFormatted,
                        color = themeColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // In-progress quick attendance prompt
            if (isOngoing) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEn) "Are you in the lecture hall?" else "هل أنت متواجد في القاعة؟",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isOngoingAttended) StatusSuccess.copy(alpha = 0.2f) else NeonCyan.copy(alpha = 0.2f))
                            .border(BorderStroke(1.dp, if (isOngoingAttended) StatusSuccess else NeonCyan), RoundedCornerShape(12.dp))
                            .clickable { onToggleAttendance(activeLecture) }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isOngoingAttended) strings.attendedStatus else strings.iAttendedBtn,
                            color = if (isOngoingAttended) StatusSuccess else NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TodayLectureItemCard(
    lecture: Lecture,
    isOngoing: Boolean,
    isCompleted: Boolean,
    isUpcoming: Boolean,
    isAttended: Boolean,
    isMissed: Boolean,
    currentMinutesOfDay: Int,
    cardColor: Color,
    onAttendanceToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    strings: AppStrings,
    isEn: Boolean
) {
    val statusLabel = when {
        isOngoing -> if (isEn) "Active Now ⚡" else "جارية الآن ⚡"
        isAttended -> if (isEn) "Attended ✓" else "تم الحضور ✓"
        isMissed -> if (isEn) "Missed ✕" else "فائتة ✕"
        else -> if (isEn) "Upcoming ⏳" else "قادمة ⏳"
    }

    val statusColor = when {
        isAttended -> StatusSuccess
        isOngoing -> StatusSuccess
        isMissed -> StatusError
        else -> cardColor
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        tintColor = statusColor,
        isGlowing = isOngoing,
        shape = RoundedCornerShape(20.dp),
        testTag = "today_lecture_${lecture.id}"
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title & Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                            .shadow(6.dp, CircleShape, spotColor = statusColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = lecture.subjectName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                GlassBadge(text = statusLabel, color = statusColor)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Details: Doctor & Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = lecture.doctorName, color = TextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${lecture.hallName} (${lecture.hallNumber})", color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Schedule
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = cardColor, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${lecture.formatStartTime()} - ${lecture.formatEndTime()}",
                    color = cardColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (lecture.requiredTools.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${strings.toolsLabel} ${lecture.requiredTools}", color = TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Attendance Button ("أنا حضرت" / "حاضر ✓")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Attendance Action Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isAttended) StatusSuccess.copy(alpha = 0.22f) else SurfaceElevated)
                        .border(
                            BorderStroke(1.dp, if (isAttended) StatusSuccess else GlassBorder),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(onClick = onAttendanceToggle)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isAttended) Icons.Default.CheckCircle else Icons.Default.Check,
                            contentDescription = null,
                            tint = if (isAttended) StatusSuccess else TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isAttended) strings.attendedStatus else strings.iAttendedBtn,
                            color = if (isAttended) StatusSuccess else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Edit/Delete
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(17.dp))
                    }
                }
            }
        }
    }
}

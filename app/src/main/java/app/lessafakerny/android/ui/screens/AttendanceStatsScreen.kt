package app.lessafakerny.android.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lessafakerny.android.ui.components.GlassBadge
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.theme.GlassBorder
import app.lessafakerny.android.ui.theme.NeonBlue
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.StatusError
import app.lessafakerny.android.ui.theme.StatusSuccess
import app.lessafakerny.android.ui.theme.StatusWarning
import app.lessafakerny.android.ui.theme.SurfaceCard
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.ui.theme.TextSecondary
import app.lessafakerny.android.util.AppStrings
import app.lessafakerny.android.util.LocaleManager
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

@Composable
fun AttendanceStatsScreen(
    viewModel: MainViewModel,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val isEn = language.equals("en", ignoreCase = true)
    val allLectures by viewModel.allLectures.collectAsState()
    val allExams by viewModel.allExams.collectAsState()
    val allAttendance by viewModel.allAttendanceRecords.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()

    // Screen-level entrance trigger
    var isScreenLoaded by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(80)
        isScreenLoaded = true
    }

    // 1. Core Analytics Calculations
    val totalRecords = allAttendance.size
    val attendedCount = allAttendance.count { it.status.equals("PRESENT", ignoreCase = true) }
    val absentCount = (totalRecords - attendedCount).coerceAtLeast(0)

    val rawOverallRate = if (totalRecords > 0) {
        (attendedCount.toFloat() / totalRecords.toFloat()) * 100f
    } else {
        100f
    }
    val overallAttendanceRate = if (rawOverallRate.isNaN() || rawOverallRate.isInfinite()) 100f else rawOverallRate.coerceIn(0f, 100f)

    // Distinct registered academic subjects
    val distinctSubjectsCount = allLectures.map { it.subjectName.trim().lowercase() }.distinct().size

    // Exams metrics
    val totalExamsCount = allExams.size
    val upcomingExamsCount = allExams.count { it.examDateMillis > nowMillis }

    // Lectures remaining this week calculation
    val currentDay = viewModel.currentTodayDayOfWeek
    val cal = Calendar.getInstance()
    val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    val remainingLecturesThisWeek = allLectures.count { lecture ->
        when {
            lecture.dayOfWeek > currentDay -> true
            lecture.dayOfWeek == currentDay -> lecture.endTimeMinutes > currentMinutes
            else -> false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("attendance_stats_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Screen Header with animated badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = if (isEn) "Academic Analytics 📊" else "إحصائيات الأداء الأكاديمي 📊",
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isEn) "Real-time attendance rates & smart study insights" else "تحليلات الحضور ومعدلات الالتزام والامتحانات الحية",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                GlassBadge(
                    text = if (isEn) "$distinctSubjectsCount Subjects" else "$distinctSubjectsCount مواد",
                    color = NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(bottom = 88.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Executive Floating Hero Gauge Card
                item {
                    FloatingExecutiveHeroCard(
                        attendanceRate = overallAttendanceRate,
                        attendedCount = attendedCount,
                        absentCount = absentCount,
                        totalRecords = totalRecords,
                        isScreenLoaded = isScreenLoaded,
                        isEn = isEn
                    )
                }

                // 2. Section Header: Key Floating Metrics
                item {
                    Text(
                        text = if (isEn) "Core Metrics & Highlights ⚡" else "المؤشرات الدراسية الرئيسية ⚡",
                        color = NeonViolet,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                // 3. Floating Glass Cards Grid (Row 1: Total Lectures & Remaining This Week)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingStatGlassCard(
                            modifier = Modifier.weight(1f),
                            title = if (isEn) "Total Lectures" else "إجمالي المحاضرات",
                            targetNumber = allLectures.size,
                            subtitle = if (isEn) "Weekly timetable" else "محاضرة مسجلة أسبوعياً",
                            icon = Icons.Default.School,
                            accentColor = NeonCyan,
                            badgeText = if (allLectures.isNotEmpty()) "${allLectures.size}" else null,
                            isScreenLoaded = isScreenLoaded,
                            staggerIndex = 1,
                            testTag = "stat_card_total_lectures"
                        )

                        FloatingStatGlassCard(
                            modifier = Modifier.weight(1f),
                            title = if (isEn) "Left This Week" else "المتبقي هذا الأسبوع",
                            targetNumber = remainingLecturesThisWeek,
                            subtitle = if (isEn) "Pending sessions" else "محاضرة قادمة بالأسبوع",
                            icon = Icons.Default.Today,
                            accentColor = NeonBlue,
                            badgeText = if (remainingLecturesThisWeek > 0) "⏳" else null,
                            isScreenLoaded = isScreenLoaded,
                            staggerIndex = 2,
                            testTag = "stat_card_remaining_week"
                        )
                    }
                }

                // 4. Floating Glass Cards Grid (Row 2: Attended vs Missed)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingStatGlassCard(
                            modifier = Modifier.weight(1f),
                            title = if (isEn) "Attended Sessions" else "المحاضرات المحضورة",
                            targetNumber = attendedCount,
                            subtitle = if (isEn) "Logged present" else "جلسة تم حضورها بنجاح",
                            icon = Icons.Default.CheckCircle,
                            accentColor = StatusSuccess,
                            badgeText = if (attendedCount > 0) "✓" else null,
                            isScreenLoaded = isScreenLoaded,
                            staggerIndex = 3,
                            testTag = "stat_card_attended"
                        )

                        FloatingStatGlassCard(
                            modifier = Modifier.weight(1f),
                            title = if (isEn) "Missed Sessions" else "المحاضرات الفائتة",
                            targetNumber = absentCount,
                            subtitle = if (isEn) "Recorded absences" else "مرات الغياب المسجلة",
                            icon = Icons.Default.Warning,
                            accentColor = if (absentCount > 0) StatusError else SoftLavender,
                            badgeText = if (absentCount > 0) "! غياب" else null,
                            isScreenLoaded = isScreenLoaded,
                            staggerIndex = 4,
                            testTag = "stat_card_absent"
                        )
                    }
                }

                // 5. Floating Glass Cards Grid (Row 3: Total Exams & Upcoming Exams)
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        FloatingStatGlassCard(
                            modifier = Modifier.weight(1f),
                            title = if (isEn) "Total Exams" else "إجمالي الامتحانات",
                            targetNumber = totalExamsCount,
                            subtitle = if (isEn) "Scheduled exams" else "امتحان مسجل بالخطة",
                            icon = Icons.Default.DateRange,
                            accentColor = NeonViolet,
                            badgeText = if (totalExamsCount > 0) "$totalExamsCount" else null,
                            isScreenLoaded = isScreenLoaded,
                            staggerIndex = 5,
                            testTag = "stat_card_total_exams"
                        )

                        FloatingStatGlassCard(
                            modifier = Modifier.weight(1f),
                            title = if (isEn) "Upcoming Exams" else "الامتحانات القادمة",
                            targetNumber = upcomingExamsCount,
                            subtitle = if (isEn) "Pending countdowns" else "امتحان قادم في انتظارك",
                            icon = Icons.Default.Timer,
                            accentColor = if (upcomingExamsCount > 0) StatusWarning else StatusSuccess,
                            badgeText = if (upcomingExamsCount > 0) "قريب" else "منتهية",
                            isScreenLoaded = isScreenLoaded,
                            staggerIndex = 6,
                            testTag = "stat_card_upcoming_exams"
                        )
                    }
                }

                // 6. Section Header: Subject-wise Detailed Breakdown
                item {
                    Text(
                        text = if (isEn) "Subject-wise Attendance Breakdown 📚" else "تفاصيل الحضور حسب كل مادة دراسية 📚",
                        color = NeonCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 10.dp)
                    )
                }

                // 7. Subject-wise Floating Cards
                if (allLectures.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            tintColor = NeonViolet,
                            shape = RoundedCornerShape(22.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(26.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(NeonViolet.copy(alpha = 0.2f))
                                        .border(BorderStroke(1.2.dp, NeonViolet.copy(alpha = 0.5f)), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = NeonViolet,
                                        modifier = Modifier.size(30.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = if (isEn) "No subjects registered in timetable yet" else "لم تقم بإضافة مواد إلى جدولك الدراسي بعد",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isEn) "Add lectures in the Timetable tab to track automated attendance and detect schedule overlaps."
                                    else "أضف محاضراتك في شاشة الجدول ليتم احتساب نسب الحضور وتنبيهات الغياب تلقائياً.",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = allLectures,
                        key = { index, lecture -> "stat_lec_${lecture.id}_$index" }
                    ) { index, lecture ->
                        val lectureRecords = allAttendance.filter { it.lectureId == lecture.id }
                        val subjectTotal = lectureRecords.size
                        val subjectAttended = lectureRecords.count { it.status.equals("PRESENT", ignoreCase = true) }
                        val subjectAbsent = (subjectTotal - subjectAttended).coerceAtLeast(0)

                        val rawRate = if (subjectTotal > 0) {
                            (subjectAttended.toFloat() / subjectTotal.toFloat()) * 100f
                        } else {
                            100f
                        }
                        val subjectRate = if (rawRate.isNaN() || rawRate.isInfinite()) 100f else rawRate.coerceIn(0f, 100f)

                        val subjectCardColor = try {
                            if (!lecture.cardColorHex.isNullOrBlank() && lecture.cardColorHex.startsWith("#")) {
                                Color(android.graphics.Color.parseColor(lecture.cardColorHex))
                            } else {
                                NeonCyan
                            }
                        } catch (e: Exception) {
                            NeonCyan
                        }

                        FloatingSubjectStatCard(
                            lecture = lecture,
                            subjectRate = subjectRate,
                            subjectAttended = subjectAttended,
                            subjectAbsent = subjectAbsent,
                            subjectTotal = subjectTotal,
                            accentColor = subjectCardColor,
                            staggerIndex = index,
                            isScreenLoaded = isScreenLoaded,
                            isEn = isEn
                        )
                    }
                }
            }
        }
    }
}

/**
 * 🌟 Executive Floating Hero Card featuring an animated circular progress gauge,
 * count-up rate, quick metrics, and dynamic attendance status badge.
 */
@Composable
private fun FloatingExecutiveHeroCard(
    attendanceRate: Float,
    attendedCount: Int,
    absentCount: Int,
    totalRecords: Int,
    isScreenLoaded: Boolean,
    isEn: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Smooth Interactive Float & Elevation
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 16.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero_elevation"
    )
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.985f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero_scale"
    )
    val animatedOffsetY by animateDpAsState(
        targetValue = if (isPressed) (-4).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "hero_offset"
    )

    // Staggered Entrance Animation
    val entranceAlpha by animateFloatAsState(
        targetValue = if (isScreenLoaded) 1f else 0f,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "hero_alpha"
    )
    val entranceTranslationY by animateDpAsState(
        targetValue = if (isScreenLoaded) 0.dp else 24.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "hero_slide"
    )

    // Animated Count-up for Attendance Percentage
    val animatedRate by animateFloatAsState(
        targetValue = if (isScreenLoaded) attendanceRate else 0f,
        animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
        label = "hero_rate_countup"
    )

    // Breathing glow animation around the hero card
    val infiniteTransition = rememberInfiniteTransition(label = "hero_glow_infinite")
    val ambientGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.65f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambient_glow_alpha"
    )

    val gaugeColor = when {
        attendanceRate >= 85f -> StatusSuccess
        attendanceRate >= 70f -> StatusWarning
        else -> StatusError
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedOffsetY + entranceTranslationY)
            .scale(animatedScale)
            .alpha(entranceAlpha)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(26.dp),
                spotColor = gaugeColor.copy(alpha = ambientGlowAlpha * 0.7f),
                ambientColor = NeonBlue.copy(alpha = 0.2f)
            )
            .clip(RoundedCornerShape(26.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A).copy(alpha = 0.85f),
                        Color(0xFF1E293B).copy(alpha = 0.65f),
                        Color(0xFF0A0F24).copy(alpha = 0.9f)
                    )
                )
            )
            .border(
                BorderStroke(
                    width = 1.4.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            NeonCyan.copy(alpha = if (isPressed) 0.9f else 0.55f),
                            NeonBlue.copy(alpha = 0.35f),
                            NeonViolet.copy(alpha = if (isPressed) 0.85f else 0.45f)
                        )
                    )
                ),
                RoundedCornerShape(26.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) {}
            .testTag("executive_hero_attendance_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEn) "Overall Commitment Rate" else "معدل الالتزام والحضور العام",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                GlassBadge(
                    text = if (attendanceRate >= 85f) (if (isEn) "Outstanding 🌟" else "التزام ممتاز 🌟")
                           else if (attendanceRate >= 70f) (if (isEn) "Moderate ⚠️" else "حضور متوسط ⚠️")
                           else (if (isEn) "Critical 🚨" else "غياب حرج 🚨"),
                    color = gaugeColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Glowing Percentage Gauge
                Box(
                    modifier = Modifier.size(108.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        if (size.minDimension <= 0f) return@Canvas
                        val strokeWidth = 11.dp.toPx()
                        val radius = ((size.minDimension - strokeWidth) / 2f).coerceAtLeast(1f)

                        // Ambient Track Circle
                        drawCircle(
                            color = SurfaceElevated.copy(alpha = 0.9f),
                            radius = radius,
                            style = Stroke(width = strokeWidth)
                        )

                        // Progress Arc with glowing gradient
                        val sweepAngle = ((animatedRate / 100f) * 360f).coerceIn(0f, 360f)
                        if (sweepAngle > 0f) {
                            drawArc(
                                brush = Brush.sweepGradient(
                                    listOf(
                                        gaugeColor,
                                        NeonCyan,
                                        NeonBlue,
                                        gaugeColor
                                    )
                                ),
                                startAngle = -90f,
                                sweepAngle = sweepAngle,
                                useCenter = false,
                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format(Locale.US, "%.0f%%", animatedRate),
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isEn) "Attended" else "حضور",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // Breakdown summary pills
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HeroMetricRow(
                        label = if (isEn) "Attended Sessions:" else "مرات الحضور:",
                        value = "$attendedCount",
                        color = StatusSuccess,
                        icon = Icons.Default.CheckCircle
                    )

                    HeroMetricRow(
                        label = if (isEn) "Absences:" else "مرات الغياب:",
                        value = "$absentCount",
                        color = if (absentCount > 0) StatusError else TextMuted,
                        icon = Icons.Default.Warning
                    )

                    HeroMetricRow(
                        label = if (isEn) "Total Tracked:" else "إجمالي الجلسات:",
                        value = "$totalRecords",
                        color = SoftLavender,
                        icon = Icons.Default.DateRange
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Motivation / Warning Banner
            val (statusText, statusColor) = when {
                totalRecords == 0 -> Pair(
                    if (isEn) "💡 Start logging your daily lecture attendance to track accurate stats!"
                    else "💡 ابدأ بتأكيد حضور محاضراتك اليومية لترى تحليلاتك الدراسية الدقيقة!",
                    NeonCyan
                )
                attendanceRate >= 85f -> Pair(
                    if (isEn) "🌟 Outstanding attendance! Keep up this high commitment to secure top grades."
                    else "🌟 التزام مثالي وممتاز! استمر في هذا الأداء لضمان أفضل التقديرات الجامعية.",
                    StatusSuccess
                )
                attendanceRate >= 70f -> Pair(
                    if (isEn) "⚠️ Good overall attendance, but beware of crossing the absence limit threshold."
                    else "⚠️ نسبة حضور جيدة، ولكن يُرجى الانتباه لتفادي الاقتراب من نسبة الغياب الحرجة.",
                    StatusWarning
                )
                else -> Pair(
                    if (isEn) "🚨 Critical absence level! Urgent risk of course exam disqualification."
                    else "🚨 نسبة غياب حرجة جداً! أنت معرض لخطر الحرمان من دخول الامتحانات.",
                    StatusError
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(statusColor.copy(alpha = 0.12f))
                    .border(BorderStroke(1.dp, statusColor.copy(alpha = 0.45f)), RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 9.dp)
            ) {
                Text(
                    text = statusText,
                    color = statusColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

/**
 * 💎 Floating Glass Stat Card Component with:
 * - Interactive Press (lifts up by -4.dp & scales smoothly)
 * - Staggered entrance animation
 * - Count-up numeric animation
 * - Frosted glass translucent background & glowing dual border
 */
@Composable
fun FloatingStatGlassCard(
    modifier: Modifier = Modifier,
    title: String,
    targetNumber: Int,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    badgeText: String? = null,
    isScreenLoaded: Boolean = true,
    staggerIndex: Int = 0,
    testTag: String = "floating_stat_card"
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Interactive Lift & Scale
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.965f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "stat_scale"
    )
    val animatedOffsetY by animateDpAsState(
        targetValue = if (isPressed) (-5).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "stat_offset"
    )
    val animatedElevation by animateDpAsState(
        targetValue = if (isPressed) 14.dp else 4.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "stat_elevation"
    )

    // Staggered Entrance
    var isCardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isScreenLoaded) {
        if (isScreenLoaded) {
            delay((staggerIndex * 50L).coerceAtLeast(0L))
            isCardVisible = true
        }
    }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (isCardVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        label = "stat_alpha"
    )
    val entranceTranslationY by animateDpAsState(
        targetValue = if (isCardVisible) 0.dp else 20.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "stat_slide"
    )

    // Count-Up Animation
    val animatedCount by animateIntAsState(
        targetValue = if (isCardVisible) targetNumber else 0,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "stat_countup"
    )

    Box(
        modifier = modifier
            .offset(y = animatedOffsetY + entranceTranslationY)
            .scale(animatedScale)
            .alpha(entranceAlpha)
            .shadow(
                elevation = animatedElevation,
                shape = RoundedCornerShape(22.dp),
                spotColor = accentColor.copy(alpha = if (isPressed) 0.45f else 0.22f),
                ambientColor = Color(0xFF000000)
            )
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A).copy(alpha = 0.82f),
                        Color(0xFF1E293B).copy(alpha = 0.65f)
                    )
                )
            )
            .border(
                BorderStroke(
                    width = 1.3.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            accentColor.copy(alpha = if (isPressed) 0.85f else 0.5f),
                            NeonBlue.copy(alpha = 0.25f),
                            accentColor.copy(alpha = 0.15f)
                        )
                    )
                ),
                RoundedCornerShape(22.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) {}
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Floating Glowing Icon Badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.18f))
                        .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.6f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                if (!badgeText.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.16f))
                            .border(BorderStroke(0.8.dp, accentColor.copy(alpha = 0.4f)), RoundedCornerShape(8.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = accentColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Count-Up Number
            Text(
                text = "$animatedCount",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Stat Title
            Text(
                text = title,
                color = accentColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            // Subtitle Description
            Text(
                text = subtitle,
                color = TextMuted,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )
        }
    }
}

/**
 * 📚 Floating Subject-wise Card with interactive lift, glowing progress bar,
 * and attendance vs absence indicators.
 */
@Composable
private fun FloatingSubjectStatCard(
    lecture: app.lessafakerny.android.data.Lecture,
    subjectRate: Float,
    subjectAttended: Int,
    subjectAbsent: Int,
    subjectTotal: Int,
    accentColor: Color,
    staggerIndex: Int,
    isScreenLoaded: Boolean,
    isEn: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.98f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "subj_scale"
    )
    val animatedOffsetY by animateDpAsState(
        targetValue = if (isPressed) (-4).dp else 0.dp,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "subj_offset"
    )

    // Staggered Entrance
    var isCardVisible by remember { mutableStateOf(false) }
    LaunchedEffect(isScreenLoaded) {
        if (isScreenLoaded) {
            delay((staggerIndex * 40L).coerceAtLeast(0L))
            isCardVisible = true
        }
    }

    val entranceAlpha by animateFloatAsState(
        targetValue = if (isCardVisible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "subj_alpha"
    )

    // Animated Count-up for Subject Rate
    val animatedSubjectRate by animateFloatAsState(
        targetValue = if (isCardVisible) subjectRate else 0f,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "subj_rate_anim"
    )

    val safeProgress = ((animatedSubjectRate / 100f)).coerceIn(0f, 1f)

    val rateStatusColor = when {
        subjectRate >= 85f -> StatusSuccess
        subjectRate >= 70f -> StatusWarning
        else -> StatusError
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedOffsetY)
            .scale(animatedScale)
            .alpha(entranceAlpha)
            .shadow(
                elevation = if (isPressed) 12.dp else 4.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = accentColor.copy(alpha = if (isPressed) 0.4f else 0.18f),
                ambientColor = Color.Black
            )
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0F172A).copy(alpha = 0.8f),
                        Color(0xFF1E293B).copy(alpha = 0.6f)
                    )
                )
            )
            .border(
                BorderStroke(
                    width = 1.2.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            accentColor.copy(alpha = if (isPressed) 0.85f else 0.45f),
                            NeonBlue.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                ),
                RoundedCornerShape(20.dp)
            )
            .clickable(interactionSource = interactionSource, indication = null) {}
            .testTag("subject_stat_card_${lecture.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                            .shadow(6.dp, CircleShape, spotColor = accentColor)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = lecture.subjectName,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (lecture.doctorName.isNotBlank()) {
                            Text(
                                text = lecture.doctorName,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                GlassBadge(
                    text = String.format(Locale.US, "%.0f%%", animatedSubjectRate),
                    color = rateStatusColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { safeProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(CircleShape),
                color = rateStatusColor,
                trackColor = SurfaceElevated.copy(alpha = 0.85f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isEn) "Attended: $subjectAttended" else "حضور: $subjectAttended",
                    color = StatusSuccess,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isEn) "Absences: $subjectAbsent" else "غياب: $subjectAbsent",
                    color = if (subjectAbsent > 0) StatusError else TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isEn) "Total: $subjectTotal" else "الإجمالي: $subjectTotal",
                    color = SoftLavender,
                    fontSize = 12.sp
                )
            }

            // Warning pill if absence is high (> 25% absence threshold in universities)
            if (subjectTotal >= 4 && (subjectAbsent.toFloat() / subjectTotal.toFloat()) >= 0.25f) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(StatusError.copy(alpha = 0.12f))
                        .border(BorderStroke(1.dp, StatusError.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEn) "⚠️ Warning: Exceeded 25% course absence threshold" else "⚠️ تنبيه: تجاوزت نسبة الغياب المسموحة (25%) لهذه المادة",
                        color = StatusError,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HeroMetricRow(
    label: String,
    value: String,
    color: Color,
    icon: ImageVector
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            color = color,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

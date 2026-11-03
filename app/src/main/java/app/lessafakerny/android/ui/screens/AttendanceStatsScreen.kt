package app.lessafakerny.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lessafakerny.android.ui.components.GlassBadge
import app.lessafakerny.android.ui.components.GlassCard
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
import java.util.Locale

@Composable
fun AttendanceStatsScreen(
    viewModel: MainViewModel,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val isEn = language.equals("en", ignoreCase = true)
    val allLectures by viewModel.allLectures.collectAsState()
    val allAttendance by viewModel.allAttendanceRecords.collectAsState()

    // Aggregate statistics calculation
    val totalRecords = allAttendance.size
    val attendedCount = allAttendance.count { it.status.equals("PRESENT", ignoreCase = true) }
    val absentCount = (totalRecords - attendedCount).coerceAtLeast(0)

    val rawOverallRate = if (totalRecords > 0) {
        (attendedCount.toFloat() / totalRecords.toFloat()) * 100f
    } else {
        100f
    }
    val overallAttendanceRate = if (rawOverallRate.isNaN() || rawOverallRate.isInfinite()) 100f else rawOverallRate.coerceIn(0f, 100f)

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

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = if (isEn) "Attendance Analytics 📊" else "إحصائيات الحضور والغياب 📊",
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isEn) "Track your overall & subject attendance rates" else "متابعة نسب الحضور والغياب الدقيقة لكل مادة دراسية",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                GlassBadge(
                    text = if (isEn) "${allLectures.size} Subjects" else "${allLectures.size} مواد",
                    color = NeonCyan
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Overall Summary Gauge Card
                item {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        tintColor = NeonCyan,
                        shape = RoundedCornerShape(24.dp),
                        useDualNeonBorder = true,
                        testTag = "overall_attendance_gauge_card"
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = if (isEn) "Overall Attendance Rate" else "معدل الحضور العام",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Circular Percentage Gauge
                                Box(
                                    modifier = Modifier.size(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        if (size.minDimension <= 0f) return@Canvas
                                        val strokeWidth = 10.dp.toPx()
                                        val radius = ((size.minDimension - strokeWidth) / 2f).coerceAtLeast(1f)

                                        // Background Track Circle
                                        drawCircle(
                                            color = SurfaceElevated.copy(alpha = 0.8f),
                                            radius = radius,
                                            style = Stroke(width = strokeWidth)
                                        )

                                        // Progress Arc
                                        val sweepAngle = ((overallAttendanceRate / 100f) * 360f).coerceIn(0f, 360f)
                                        val gaugeColor = when {
                                            overallAttendanceRate >= 85f -> StatusSuccess
                                            overallAttendanceRate >= 70f -> StatusWarning
                                            else -> StatusError
                                        }

                                        if (sweepAngle > 0f) {
                                            drawArc(
                                                color = gaugeColor,
                                                startAngle = -90f,
                                                sweepAngle = sweepAngle,
                                                useCenter = false,
                                                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                                            )
                                        }
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = String.format(Locale.US, "%.0f%%", overallAttendanceRate),
                                            color = TextPrimary,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = if (isEn) "Attendance" else "نسبة الحضور",
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(20.dp))

                                // Quick Metrics Breakdown
                                Column(modifier = Modifier.weight(1f)) {
                                    MetricRow(
                                        label = if (isEn) "Sessions Attended:" else "عدد مرات الحضور:",
                                        value = "$attendedCount",
                                        color = StatusSuccess,
                                        icon = Icons.Default.CheckCircle
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    MetricRow(
                                        label = if (isEn) "Sessions Missed:" else "عدد مرات الغياب:",
                                        value = "$absentCount",
                                        color = StatusError,
                                        icon = Icons.Default.Warning
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    MetricRow(
                                        label = if (isEn) "Total Logged Sessions:" else "إجمالي المحاضرات المسجلة:",
                                        value = "$totalRecords",
                                        color = SoftLavender,
                                        icon = Icons.Default.DateRange
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Overall Status Banner
                            val (statusText, statusColor) = when {
                                totalRecords == 0 -> Pair(
                                    if (isEn) "No attendance records saved yet" else "لم يتم تسجيل أي حضور حتى الآن",
                                    NeonCyan
                                )
                                overallAttendanceRate >= 85f -> Pair(
                                    if (isEn) "Excellent commitment! Keep it up 🌟" else "التزام ممتاز جداً! واصل تفوقك 🌟",
                                    StatusSuccess
                                )
                                overallAttendanceRate >= 70f -> Pair(
                                    if (isEn) "Good attendance, beware of absence limits ⚠️" else "حضور جيد، انتبه لنسبة الغياب المتاحة ⚠️",
                                    StatusWarning
                                )
                                else -> Pair(
                                    if (isEn) "Critical absence level! High risk of prohibition 🚨" else "نسبة غياب حرجة! خطر الحرمان من الاختبارات 🚨",
                                    StatusError
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(statusColor.copy(alpha = 0.15f))
                                    .border(BorderStroke(1.dp, statusColor.copy(alpha = 0.4f)), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = statusText,
                                    color = statusColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // 2. Section Title
                item {
                    Text(
                        text = if (isEn) "Subject-wise Attendance Breakdown 📚" else "تفاصيل الحضور حسب المادة الدراسية 📚",
                        color = NeonViolet,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // 3. Subject-wise Stats Cards
                if (allLectures.isEmpty()) {
                    item {
                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            tintColor = NeonViolet,
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = SoftLavender,
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = if (isEn) "No subjects added to your timetable yet" else "لم تقم بإضافة مواد إلى جدولك الأسبوعي بعد",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isEn) "Add lectures from the Timetable screen to view detailed stats." else "قم بإضافة المحاضرات من شاشة الجدول ليتم حساب الحضور والغياب تلقائياً.",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(
                        items = allLectures,
                        key = { index, lecture -> "stat_lec_${lecture.id}_$index" }
                    ) { _, lecture ->
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

                        val safeProgress = ((subjectRate / 100f)).coerceIn(0f, 1f)

                        GlassCard(
                            modifier = Modifier.fillMaxWidth(),
                            tintColor = subjectCardColor,
                            shape = RoundedCornerShape(20.dp),
                            testTag = "subject_stat_card_${lecture.id}"
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
                                                .size(12.dp)
                                                .clip(CircleShape)
                                                .background(subjectCardColor)
                                                .shadow(6.dp, CircleShape, spotColor = subjectCardColor)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
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
                                        text = String.format(Locale.US, "%.0f%%", subjectRate),
                                        color = when {
                                            subjectRate >= 85f -> StatusSuccess
                                            subjectRate >= 70f -> StatusWarning
                                            else -> StatusError
                                        }
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                LinearProgressIndicator(
                                    progress = { safeProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(CircleShape),
                                    color = when {
                                        subjectRate >= 85f -> StatusSuccess
                                        subjectRate >= 70f -> StatusWarning
                                        else -> StatusError
                                    },
                                    trackColor = SurfaceElevated.copy(alpha = 0.8f)
                                )

                                Spacer(modifier = Modifier.height(10.dp))

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
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(
    label: String,
    value: String,
    color: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
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

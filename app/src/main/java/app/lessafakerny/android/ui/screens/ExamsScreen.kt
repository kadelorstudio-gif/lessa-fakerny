package app.lessafakerny.android.ui.screens

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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lessafakerny.android.data.Exam
import app.lessafakerny.android.ui.components.GlassBadge
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.theme.NeonMagenta
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.StatusError
import app.lessafakerny.android.ui.theme.StatusSuccess
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.ui.theme.TextSecondary
import app.lessafakerny.android.util.AppStrings
import app.lessafakerny.android.util.LocaleManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExamsScreen(
    viewModel: MainViewModel,
    onAddExamClick: () -> Unit,
    onEditExamClick: (Exam) -> Unit,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val allExams by viewModel.allExams.collectAsState()
    val nowMillis by viewModel.nowMillis.collectAsState()
    val isEn = language.equals("en", ignoreCase = true)

    var examToDelete by remember { mutableStateOf<Exam?>(null) }

    val sortedExams = remember(allExams) {
        allExams.sortedBy { it.examDateMillis }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .testTag("exams_screen")
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.examsHeaderTitle,
                        color = NeonViolet,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.examsHeaderSubtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                val examsCountText = if (isEn) "${sortedExams.size} exams" else "${sortedExams.size} اختبارات"
                GlassBadge(
                    text = examsCountText,
                    color = NeonViolet
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (sortedExams.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(74.dp)
                                .clip(CircleShape)
                                .background(NeonViolet.copy(alpha = 0.15f))
                                .border(BorderStroke(1.2.dp, NeonViolet.copy(alpha = 0.45f)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = NeonViolet,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = strings.emptyExamsTitle,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = strings.emptyExamsSubtitle,
                            color = TextMuted,
                            fontSize = 13.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 4.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    itemsIndexed(
                        items = sortedExams,
                        key = { index, exam -> "exam_${exam.id}_$index" }
                    ) { _, exam ->
                        val cardColor = try {
                            if (!exam.cardColorHex.isNullOrBlank() && exam.cardColorHex.startsWith("#")) {
                                Color(android.graphics.Color.parseColor(exam.cardColorHex))
                            } else {
                                NeonViolet
                            }
                        } catch (e: Exception) {
                            NeonViolet
                        }

                        ExamCountdownCardItem(
                            exam = exam,
                            nowMillis = nowMillis,
                            cardColor = cardColor,
                            onEdit = { onEditExamClick(exam) },
                            onDelete = { examToDelete = exam },
                            strings = strings,
                            isEn = isEn
                        )
                    }
                }
            }
        }

        // Luminous Glass Glowing FAB with correct Scaffold inner padding
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 16.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = NeonViolet.copy(alpha = 0.75f),
                    ambientColor = NeonMagenta.copy(alpha = 0.45f)
                )
                .clip(CircleShape)
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(
                            NeonViolet,
                            app.lessafakerny.android.ui.theme.NeonMagenta,
                            NeonViolet
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.85f), NeonViolet, Color.White.copy(alpha = 0.4f))
                        )
                    ),
                    CircleShape
                )
                .clickable(onClick = onAddExamClick)
                .size(58.dp)
                .testTag("add_exam_fab"),
            contentAlignment = Alignment.Center
        ) {
            // Specular top shine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(28.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        androidx.compose.ui.graphics.Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.35f), Color.Transparent)
                        )
                    )
            )
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Exam", tint = Color.White, modifier = Modifier.size(28.dp))
        }

        // Delete Confirmation Dialog
        if (examToDelete != null) {
            AlertDialog(
                onDismissRequest = { examToDelete = null },
                title = { Text(strings.deleteExamDialogTitle, color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text(strings.deleteExamDialogMessage, color = TextSecondary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            examToDelete?.let { viewModel.deleteExam(it) }
                            examToDelete = null
                        }
                    ) {
                        Text(if (isEn) "Delete" else "حذف", color = StatusError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { examToDelete = null }) {
                        Text(strings.cancelBtn, color = TextMuted)
                    }
                },
                containerColor = SurfaceElevated
            )
        }
    }
}

@Composable
fun ExamCountdownCardItem(
    exam: Exam,
    nowMillis: Long,
    cardColor: Color,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    strings: AppStrings,
    isEn: Boolean
) {
    val examDate = exam.examDateMillis
    val diffMillis = (examDate - nowMillis).coerceAtLeast(0L)
    val isPast = examDate in 1L..nowMillis

    val totalSecs = if (isPast || examDate <= 0L) 0L else diffMillis / 1000L
    val days = totalSecs / (24L * 3600L)
    val hours = (totalSecs % (24L * 3600L)) / 3600L
    val mins = (totalSecs % 3600L) / 60L
    val secs = totalSecs % 60L

    val formattedDate = try {
        if (examDate > 0L) {
            val pattern = "EEEE, d MMMM yyyy • hh:mm a"
            val locale = if (isEn) Locale.ENGLISH else Locale("ar")
            SimpleDateFormat(pattern, locale).format(Date(examDate))
        } else {
            ""
        }
    } catch (e: Exception) {
        ""
    }

    val isUrgent = !isPast && examDate > 0L && days <= 3

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        tintColor = if (isPast) TextMuted else cardColor,
        isGlowing = isUrgent,
        useDualNeonBorder = isUrgent,
        shape = RoundedCornerShape(22.dp),
        testTag = "exam_card_${exam.id}"
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Title & Actions
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
                            .background(cardColor)
                            .shadow(6.dp, CircleShape, spotColor = cardColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = exam.subjectName,
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = StatusError,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = SoftLavender,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formattedDate,
                    color = SoftLavender,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = exam.hallLocation,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (exam.allowedTools.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = StatusSuccess,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${strings.allowedToolsLabel} ${exam.allowedTools}",
                        color = StatusSuccess,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Countdown Timer Display
            if (isPast) {
                GlassBadge(
                    text = if (isEn) "Exam finished" else "انتهى موعد الامتحان",
                    color = TextMuted
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceElevated.copy(alpha = 0.6f))
                        .padding(vertical = 10.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FuturisticTimeUnit(value = days.toString(), label = if (isEn) "Days" else "أيام", color = cardColor)
                    Text(":", color = SoftLavender.copy(alpha = 0.4f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    FuturisticTimeUnit(value = String.format(Locale.US, "%02d", hours), label = if (isEn) "Hours" else "ساعات", color = cardColor)
                    Text(":", color = SoftLavender.copy(alpha = 0.4f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    FuturisticTimeUnit(value = String.format(Locale.US, "%02d", mins), label = if (isEn) "Mins" else "دقائق", color = cardColor)
                    Text(":", color = SoftLavender.copy(alpha = 0.4f), fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    FuturisticTimeUnit(value = String.format(Locale.US, "%02d", secs), label = if (isEn) "Secs" else "ثواني", color = cardColor)
                }
            }
        }
    }
}

@Composable
fun FuturisticTimeUnit(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            text = label,
            color = SoftLavender,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

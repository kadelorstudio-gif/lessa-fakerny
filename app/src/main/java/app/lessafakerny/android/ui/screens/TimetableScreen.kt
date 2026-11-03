package app.lessafakerny.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
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
import app.lessafakerny.android.data.Lecture
import app.lessafakerny.android.ui.components.GlassBadge
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.theme.GlassBorder
import app.lessafakerny.android.ui.theme.NeonBlue
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonMagenta
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.StatusError
import app.lessafakerny.android.ui.theme.StatusWarning
import app.lessafakerny.android.ui.theme.SurfaceCard
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.ui.theme.TextSecondary
import app.lessafakerny.android.util.AppStrings
import app.lessafakerny.android.util.LocaleManager
import app.lessafakerny.android.util.getLocalizedDayName
import java.util.Calendar

@Composable
fun TimetableScreen(
    viewModel: MainViewModel,
    onAddLectureClick: (Int) -> Unit,
    onEditLectureClick: (Lecture) -> Unit,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val selectedDay by viewModel.selectedTimetableDay.collectAsState()
    val allLectures by viewModel.allLectures.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val is24Hour = settings?.is24HourFormat ?: false
    val todayDay = viewModel.currentTodayDayOfWeek
    val isEn = language.equals("en", ignoreCase = true)

    var lectureToDelete by remember { mutableStateOf<Lecture?>(null) }

    val dayLectures = remember(allLectures, selectedDay) {
        allLectures.filter { it.dayOfWeek == selectedDay }
            .sortedBy { it.startTimeMinutes }
    }

    // Detect overlapping lectures
    val overlappingLectureIds = remember(dayLectures) {
        val overlaps = mutableSetOf<Long>()
        for (i in dayLectures.indices) {
            for (j in i + 1 until dayLectures.size) {
                val l1 = dayLectures[i]
                val l2 = dayLectures[j]
                if (maxOf(l1.startTimeMinutes, l2.startTimeMinutes) < minOf(l1.endTimeMinutes, l2.endTimeMinutes)) {
                    overlaps.add(l1.id)
                    overlaps.add(l2.id)
                }
            }
        }
        overlaps
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp)
                .testTag("timetable_screen")
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text(
                        text = strings.timetableHeaderTitle,
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.timetableHeaderSubtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                val totalLabel = if (isEn) "${allLectures.size} lectures/wk" else "${allLectures.size} محاضرة أسبوعياً"
                GlassBadge(
                    text = totalLabel,
                    color = NeonBlue
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Day Selector Tabs
            TimetableDaySelector(
                selectedDay = selectedDay,
                todayDay = todayDay,
                allLectures = allLectures,
                onDaySelected = { viewModel.selectTimetableDay(it) },
                language = language
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Overlap Warning Banner if detected
            if (overlappingLectureIds.isNotEmpty()) {
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    tintColor = StatusWarning,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = StatusWarning,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strings.conflictWarning,
                            color = StatusWarning,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Day Header & Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val dayNameStr = getLocalizedDayName(selectedDay, language)
                val dayCountText = if (isEn) "$dayNameStr Lectures (${dayLectures.size})" else "محاضرات $dayNameStr (${dayLectures.size})"
                Text(
                    text = dayCountText,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = if (selectedDay == todayDay) (if (isEn) "Today" else "اليوم الحالي") else "",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (dayLectures.isEmpty()) {
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
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(NeonBlue.copy(alpha = 0.12f))
                                .border(BorderStroke(1.dp, NeonBlue.copy(alpha = 0.35f)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = NeonBlue,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        val noLecText = if (isEn) {
                            "No lectures on ${getLocalizedDayName(selectedDay, language)}"
                        } else {
                            "لا توجد محاضرات في ${getLocalizedDayName(selectedDay, language)}"
                        }
                        Text(
                            text = noLecText,
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (isEn) "Tap (+) to add courses for this day" else "أضف موادك ومواعيدك لهذا اليوم بالضغط على زر (+)",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(
                        items = dayLectures,
                        key = { index, lecture -> "tt_lec_${lecture.id}_$index" }
                    ) { index, lecture ->
                        val cardColor = try {
                            Color(android.graphics.Color.parseColor(lecture.cardColorHex))
                        } catch (e: Exception) {
                            NeonCyan
                        }
                        val isOverlapping = overlappingLectureIds.contains(lecture.id)

                        TimetableLectureItem(
                            lecture = lecture,
                            cardColor = cardColor,
                            isOverlapping = isOverlapping,
                            onEdit = { onEditLectureClick(lecture) },
                            onDelete = { lectureToDelete = lecture },
                            onEndEarly = { viewModel.endLectureEarly(lecture) },
                            strings = strings,
                            is24Hour = is24Hour,
                            language = language
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }

        // Luminous Glass Glowing FAB to add lecture on currently viewed day
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 20.dp, bottom = 16.dp)
                .shadow(
                    elevation = 20.dp,
                    shape = CircleShape,
                    spotColor = NeonBlue.copy(alpha = 0.75f),
                    ambientColor = NeonCyan.copy(alpha = 0.45f)
                )
                .clip(CircleShape)
                .background(
                    androidx.compose.ui.graphics.Brush.linearGradient(
                        listOf(
                            NeonBlue,
                            NeonCyan,
                            NeonBlue
                        )
                    )
                )
                .border(
                    BorderStroke(
                        1.5.dp,
                        androidx.compose.ui.graphics.Brush.linearGradient(
                            listOf(Color.White.copy(alpha = 0.85f), NeonBlue, Color.White.copy(alpha = 0.4f))
                        )
                    ),
                    CircleShape
                )
                .clickable(onClick = { onAddLectureClick(selectedDay) })
                .size(58.dp)
                .testTag("timetable_add_fab"),
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
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Lecture", tint = Color.White, modifier = Modifier.size(28.dp))
        }

        // Delete Confirmation Dialog
        if (lectureToDelete != null) {
            AlertDialog(
                onDismissRequest = { lectureToDelete = null },
                title = { Text(strings.deleteLectureDialogTitle, color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = { Text(strings.deleteLectureDialogMessage, color = TextSecondary) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            lectureToDelete?.let { viewModel.deleteLecture(it) }
                            lectureToDelete = null
                        }
                    ) {
                        Text(strings.clearDataConfirmBtn.substringBefore("،"), color = StatusError, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { lectureToDelete = null }) {
                        Text(strings.cancelBtn, color = TextMuted)
                    }
                },
                containerColor = SurfaceElevated
            )
        }
    }
}

@Composable
fun TimetableDaySelector(
    selectedDay: Int,
    todayDay: Int,
    allLectures: List<Lecture>,
    onDaySelected: (Int) -> Unit,
    language: String
) {
    val scrollState = rememberScrollState()
    val days = listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        days.forEach { dayInt ->
            val dayName = getLocalizedDayName(dayInt, language)
            val isSelected = dayInt == selectedDay
            val isToday = dayInt == todayDay
            val count = allLectures.count { it.dayOfWeek == dayInt }

            val chipBg = when {
                isSelected -> NeonBlue.copy(alpha = 0.25f)
                isToday -> SurfaceElevated
                else -> SurfaceCard.copy(alpha = 0.8f)
            }

            val borderBrush = when {
                isSelected -> NeonBlue
                isToday -> NeonCyan.copy(alpha = 0.6f)
                else -> GlassBorder
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(chipBg)
                    .border(BorderStroke(1.dp, borderBrush), RoundedCornerShape(16.dp))
                    .clickable { onDaySelected(dayInt) }
                    .padding(horizontal = 14.dp, vertical = 7.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isToday) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(NeonCyan)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = dayName,
                        color = if (isSelected) NeonBlue else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                    if (count > 0) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(if (isSelected) NeonBlue else SoftLavender.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = count.toString(),
                                color = if (isSelected) Color.White else SoftLavender,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimetableLectureItem(
    lecture: Lecture,
    cardColor: Color,
    isOverlapping: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onEndEarly: (() -> Unit)? = null,
    strings: AppStrings,
    is24Hour: Boolean = false,
    language: String = "ar"
) {
    val nowCal = java.util.Calendar.getInstance()
    val currentMins = nowCal.get(java.util.Calendar.HOUR_OF_DAY) * 60 + nowCal.get(java.util.Calendar.MINUTE)
    val isOngoing = currentMins in lecture.startTimeMinutes until lecture.endTimeMinutes

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        tintColor = if (isOverlapping) StatusWarning else cardColor,
        shape = RoundedCornerShape(20.dp),
        testTag = "timetable_item_${lecture.id}"
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                        text = lecture.subjectName,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = TextSecondary, modifier = Modifier.size(17.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = StatusError, modifier = Modifier.size(17.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = lecture.doctorName, color = TextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.width(16.dp))

                Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(15.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "${lecture.hallName} (${lecture.hallNumber})", color = TextSecondary, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = cardColor, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${lecture.formatStartTime(is24Hour, language)} - ${lecture.formatEndTime(is24Hour, language)}",
                        color = cardColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isOngoing && onEndEarly != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(StatusWarning.copy(alpha = 0.18f))
                            .border(BorderStroke(1.dp, StatusWarning.copy(alpha = 0.5f)), RoundedCornerShape(12.dp))
                            .clickable(onClick = onEndEarly)
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = if (language == "en") "End Early ⏹️" else "انتهت مبكراً ⏹️",
                            color = StatusWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            if (lecture.requiredTools.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null, tint = TextMuted, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "${strings.toolsLabel} ${lecture.requiredTools}", color = TextMuted, fontSize = 11.sp)
                }
            }
        }
    }
}

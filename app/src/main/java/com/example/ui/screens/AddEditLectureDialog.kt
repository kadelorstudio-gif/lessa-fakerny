package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.Lecture
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusError
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppStrings
import com.example.util.LocaleManager
import com.example.util.getLocalizedDayName
import java.util.Calendar

@Composable
fun AddEditLectureDialog(
    lectureToEdit: Lecture? = null,
    initialDayOfWeek: Int = Calendar.SUNDAY,
    onDismiss: () -> Unit,
    onSave: (
        subjectName: String,
        doctorName: String,
        hallNumber: String,
        hallName: String,
        dayOfWeek: Int,
        startTimeMinutes: Int,
        endTimeMinutes: Int,
        cardColorHex: String,
        requiredTools: String
    ) -> Unit,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    var subjectName by remember { mutableStateOf(lectureToEdit?.subjectName ?: "") }
    var doctorName by remember { mutableStateOf(lectureToEdit?.doctorName ?: "") }
    var hallNumber by remember { mutableStateOf(lectureToEdit?.hallNumber ?: "") }
    var hallName by remember { mutableStateOf(lectureToEdit?.hallName ?: "") }
    var selectedDay by remember { mutableStateOf(lectureToEdit?.dayOfWeek ?: initialDayOfWeek) }

    var startHour by remember { mutableStateOf((lectureToEdit?.startTimeMinutes ?: 540) / 60) }
    var startMinute by remember { mutableStateOf((lectureToEdit?.startTimeMinutes ?: 540) % 60) }
    var endHour by remember { mutableStateOf((lectureToEdit?.endTimeMinutes ?: 660) / 60) }
    var endMinute by remember { mutableStateOf((lectureToEdit?.endTimeMinutes ?: 660) % 60) }

    var cardColorHex by remember { mutableStateOf(lectureToEdit?.cardColorHex ?: "#00E5FF") }
    var requiredTools by remember { mutableStateOf(lectureToEdit?.requiredTools ?: "") }

    var errorMessage by remember { mutableStateOf("") }

    val presetColors = listOf(
        "#00E5FF", "#8B5CF6", "#287BFF", "#FF45D4", "#35D6A0", "#FFCA70"
    )

    val daysList = listOf(
        Calendar.SUNDAY,
        Calendar.MONDAY,
        Calendar.TUESDAY,
        Calendar.WEDNESDAY,
        Calendar.THURSDAY,
        Calendar.FRIDAY,
        Calendar.SATURDAY
    )

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("add_lecture_dialog"),
            shape = RoundedCornerShape(26.dp),
            tintColor = try { Color(android.graphics.Color.parseColor(cardColorHex)) } catch (e: Exception) { NeonCyan },
            useDualNeonBorder = true
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (lectureToEdit == null) strings.addLectureTitle else strings.editLectureTitle,
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlassTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = strings.subjectNameLabel,
                    placeholder = strings.subjectNamePlaceholder
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it },
                    label = strings.doctorNameLabel,
                    placeholder = strings.doctorNamePlaceholder
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassTextField(
                        value = hallName,
                        onValueChange = { hallName = it },
                        label = strings.buildingLabel,
                        placeholder = strings.buildingPlaceholder,
                        modifier = Modifier.weight(1.2f)
                    )

                    GlassTextField(
                        value = hallNumber,
                        onValueChange = { hallNumber = it },
                        label = strings.hallNumberLabel,
                        placeholder = strings.hallNumberPlaceholder,
                        modifier = Modifier.weight(0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(strings.dayOfWeekLabel, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    daysList.forEach { dInt ->
                        val dName = getLocalizedDayName(dInt, language)
                        val isSelected = dInt == selectedDay
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) NeonCyan.copy(alpha = 0.22f) else SurfaceElevated)
                                .border(BorderStroke(1.dp, if (isSelected) NeonCyan else GlassBorder), RoundedCornerShape(12.dp))
                                .clickable { selectedDay = dInt }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(dName, color = if (isSelected) NeonCyan else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time Pickers Inputs (Start & End)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.startTimeLabel, color = TextSecondary, fontSize = 11.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassTextField(
                                value = String.format("%02d", startHour),
                                onValueChange = { startHour = it.toIntOrNull()?.coerceIn(0, 23) ?: startHour },
                                label = strings.hoursUnit,
                                modifier = Modifier.weight(1f)
                            )
                            Text(":", color = SoftLavender, modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold)
                            GlassTextField(
                                value = String.format("%02d", startMinute),
                                onValueChange = { startMinute = it.toIntOrNull()?.coerceIn(0, 59) ?: startMinute },
                                label = strings.minutesUnit,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(strings.endTimeLabel, color = TextSecondary, fontSize = 11.sp)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GlassTextField(
                                value = String.format("%02d", endHour),
                                onValueChange = { endHour = it.toIntOrNull()?.coerceIn(0, 23) ?: endHour },
                                label = strings.hoursUnit,
                                modifier = Modifier.weight(1f)
                            )
                            Text(":", color = SoftLavender, modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold)
                            GlassTextField(
                                value = String.format("%02d", endMinute),
                                onValueChange = { endMinute = it.toIntOrNull()?.coerceIn(0, 59) ?: endMinute },
                                label = strings.minutesUnit,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(strings.cardColorLabel, color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    presetColors.forEach { hex ->
                        val colorVal = Color(android.graphics.Color.parseColor(hex))
                        val isSelected = hex.equals(cardColorHex, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(colorVal)
                                .border(
                                    BorderStroke(if (isSelected) 3.dp else 1.dp, if (isSelected) Color.White else Color.Transparent),
                                    CircleShape
                                )
                                .clickable { cardColorHex = hex }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = requiredTools,
                    onValueChange = { requiredTools = it },
                    label = strings.requiredToolsLabel,
                    placeholder = strings.requiredToolsPlaceholder
                )

                if (errorMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(errorMessage, color = StatusError, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(strings.cancelBtn, color = TextMuted)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    GlassButton(
                        onClick = {
                            if (subjectName.isBlank()) {
                                errorMessage = strings.errorSubjectEmpty
                                return@GlassButton
                            }
                            val startMins = startHour * 60 + startMinute
                            val endMins = endHour * 60 + endMinute
                            if (endMins <= startMins) {
                                errorMessage = strings.errorEndTimeInvalid
                                return@GlassButton
                            }

                            onSave(
                                subjectName,
                                doctorName.ifBlank { if (language == "en") "Unassigned" else "غير محدد" },
                                hallNumber.ifBlank { "-" },
                                hallName.ifBlank { if (language == "en") "Main Building" else "المبنى الرئيسي" },
                                selectedDay,
                                startMins,
                                endMins,
                                cardColorHex,
                                requiredTools
                            )
                        },
                        testTag = "save_lecture_btn"
                    ) {
                        Text(strings.saveLectureBtn, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

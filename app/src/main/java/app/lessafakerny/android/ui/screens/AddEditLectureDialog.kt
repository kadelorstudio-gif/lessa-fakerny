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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import app.lessafakerny.android.data.Lecture
import app.lessafakerny.android.ui.components.GlassButton
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.components.GlassTextField
import app.lessafakerny.android.ui.components.MobileTimePicker
import app.lessafakerny.android.ui.theme.GlassBorder
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.StatusError
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.ui.theme.TextSecondary
import app.lessafakerny.android.util.AppStrings
import app.lessafakerny.android.util.LocaleManager
import app.lessafakerny.android.util.getLocalizedDayName
import java.util.Calendar

@Composable
fun AddEditLectureDialog(
    lectureToEdit: Lecture? = null,
    initialDayOfWeek: Int = Calendar.SUNDAY,
    is24Hour: Boolean = false,
    userRole: String = "STUDENT",
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

    var startTimeMinutes by remember { mutableIntStateOf(lectureToEdit?.startTimeMinutes ?: 540) } // 09:00 AM
    var endTimeMinutes by remember { mutableIntStateOf(lectureToEdit?.endTimeMinutes ?: 660) }   // 11:00 AM

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
                .padding(vertical = 12.dp)
                .testTag("add_lecture_dialog"),
            shape = RoundedCornerShape(26.dp),
            tintColor = try { Color(android.graphics.Color.parseColor(cardColorHex)) } catch (e: Exception) { NeonCyan },
            useDualNeonBorder = true
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .navigationBarsPadding()
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
                    label = if (userRole == "DOCTOR") (if (language == "en") "Instructor Name / Title" else "اسم المحاضر / الدكتور") else strings.doctorNameLabel,
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

                Spacer(modifier = Modifier.height(14.dp))

                // Mobile Wheel Time Picker: Start Time
                Text(strings.startTimeLabel, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                MobileTimePicker(
                    initialMinutesOfDay = startTimeMinutes,
                    is24Hour = is24Hour,
                    strings = strings,
                    onTimeSelected = { newMins ->
                        startTimeMinutes = newMins
                        errorMessage = ""
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Mobile Wheel Time Picker: End Time
                Text(strings.endTimeLabel, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                MobileTimePicker(
                    initialMinutesOfDay = endTimeMinutes,
                    is24Hour = is24Hour,
                    strings = strings,
                    onTimeSelected = { newMins ->
                        endTimeMinutes = newMins
                        errorMessage = ""
                    }
                )

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
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusError.copy(alpha = 0.15f))
                            .border(BorderStroke(1.dp, StatusError.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Text(errorMessage, color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
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
                            if (endTimeMinutes <= startTimeMinutes) {
                                errorMessage = strings.errorEndTimeInvalid
                                return@GlassButton
                            }

                            onSave(
                                subjectName,
                                doctorName.ifBlank { if (language == "en") "Unassigned" else "غير محدد" },
                                hallNumber.ifBlank { "-" },
                                hallName.ifBlank { if (language == "en") "Main Building" else "المبنى الرئيسي" },
                                selectedDay,
                                startTimeMinutes,
                                endTimeMinutes,
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

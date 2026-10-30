package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.example.data.Exam
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassTextField
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.StatusError
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppStrings
import com.example.util.LocaleManager
import java.util.Calendar

@Composable
fun AddEditExamDialog(
    examToEdit: Exam? = null,
    onDismiss: () -> Unit,
    onSave: (
        subjectName: String,
        examDateMillis: Long,
        hallLocation: String,
        allowedTools: String,
        cardColorHex: String
    ) -> Unit,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    var subjectName by remember { mutableStateOf(examToEdit?.subjectName ?: "") }
    var hallLocation by remember { mutableStateOf(examToEdit?.hallLocation ?: "") }
    var allowedTools by remember { mutableStateOf(examToEdit?.allowedTools ?: "") }
    var cardColorHex by remember { mutableStateOf(examToEdit?.cardColorHex ?: "#8B5CF6") }

    val initialCalendar = Calendar.getInstance().apply {
        if (examToEdit != null) {
            timeInMillis = examToEdit.examDateMillis
        } else {
            add(Calendar.DAY_OF_YEAR, 7)
        }
    }

    var daysFromNow by remember { mutableStateOf("7") }
    var examHour by remember { mutableStateOf(initialCalendar.get(Calendar.HOUR_OF_DAY).toString()) }

    var errorMessage by remember { mutableStateOf("") }

    val presetColors = listOf(
        "#8B5CF6", "#00E5FF", "#FF45D4", "#287BFF", "#35D6A0", "#FFCA70"
    )

    Dialog(onDismissRequest = onDismiss) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("add_exam_dialog"),
            shape = RoundedCornerShape(26.dp),
            tintColor = try { Color(android.graphics.Color.parseColor(cardColorHex)) } catch (e: Exception) { NeonViolet },
            useDualNeonBorder = true
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = if (examToEdit == null) strings.addExamTitle else strings.editExamTitle,
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                GlassTextField(
                    value = subjectName,
                    onValueChange = { subjectName = it },
                    label = strings.examNameLabel,
                    placeholder = strings.examNamePlaceholder
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(strings.examDateLabel, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    GlassTextField(
                        value = daysFromNow,
                        onValueChange = { daysFromNow = it.filter { ch -> ch.isDigit() } },
                        label = strings.examDaysFromNowLabel,
                        placeholder = "7",
                        modifier = Modifier.weight(1f)
                    )

                    GlassTextField(
                        value = examHour,
                        onValueChange = { examHour = it.filter { ch -> ch.isDigit() }.take(2) },
                        label = strings.examHourLabel,
                        placeholder = "10",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = hallLocation,
                    onValueChange = { hallLocation = it },
                    label = strings.examHallLabel,
                    placeholder = strings.examHallPlaceholder
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = allowedTools,
                    onValueChange = { allowedTools = it },
                    label = strings.allowedToolsLabel,
                    placeholder = strings.allowedToolsPlaceholder
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

                            val daysInt = daysFromNow.toIntOrNull() ?: 1
                            val hourInt = examHour.toIntOrNull()?.coerceIn(0, 23) ?: 10

                            val targetCal = Calendar.getInstance().apply {
                                add(Calendar.DAY_OF_YEAR, daysInt)
                                set(Calendar.HOUR_OF_DAY, hourInt)
                                set(Calendar.MINUTE, 0)
                                set(Calendar.SECOND, 0)
                            }

                            onSave(
                                subjectName,
                                targetCal.timeInMillis,
                                hallLocation.ifBlank { if (language == "en") "Main Examination Hall" else "صالة الامتحانات الرئيسية" },
                                allowedTools.ifBlank { if (language == "en") "Pen, Student ID Card" else "قلم جاف، بطاقة الكارنية" },
                                cardColorHex
                            )
                        },
                        accentColor = NeonViolet,
                        testTag = "save_exam_btn"
                    ) {
                        Text(strings.saveExamBtn, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }
    }
}

package app.lessafakerny.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import app.lessafakerny.android.ui.theme.GlassBorder
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.SurfaceCard
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.util.AppStrings
import java.util.Locale

@Composable
fun MobileTimePicker(
    initialMinutesOfDay: Int,
    is24Hour: Boolean,
    strings: AppStrings,
    onTimeSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedHour24 by remember(initialMinutesOfDay) { mutableIntStateOf(initialMinutesOfDay / 60) }
    var selectedMinute by remember(initialMinutesOfDay) { mutableIntStateOf(initialMinutesOfDay % 60) }

    var isAm by remember(selectedHour24) { mutableStateOf(selectedHour24 < 12) }

    val displayHour = if (is24Hour) {
        selectedHour24
    } else {
        val h12 = selectedHour24 % 12
        if (h12 == 0) 12 else h12
    }

    fun updateMinutes() {
        val final24Hour = if (is24Hour) {
            selectedHour24
        } else {
            val h12 = displayHour % 12
            if (isAm) h12 else h12 + 12
        }
        val totalMins = (final24Hour * 60 + selectedMinute).coerceIn(0, 1439)
        onTimeSelected(totalMins)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(SurfaceCard)
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(18.dp))
            .padding(12.dp)
            .testTag("mobile_time_picker")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Hour Wheel/Stepper Column
                TimeStepColumn(
                    value = displayHour,
                    label = strings.hourLabel,
                    range = if (is24Hour) 0..23 else 1..12,
                    formatPattern = "%02d",
                    onValueChange = { newHour ->
                        if (is24Hour) {
                            selectedHour24 = newHour
                        } else {
                            val h12 = newHour % 12
                            selectedHour24 = if (isAm) h12 else h12 + 12
                        }
                        updateMinutes()
                    }
                )

                Text(
                    text = ":",
                    color = NeonCyan,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Minute Wheel/Stepper Column
                TimeStepColumn(
                    value = selectedMinute,
                    label = strings.minuteLabel,
                    range = 0..59,
                    step = 1,
                    formatPattern = "%02d",
                    onValueChange = { newMinute ->
                        selectedMinute = newMinute
                        updateMinutes()
                    }
                )

                // AM / PM Segment Toggle (only visible in 12-hour mode)
                if (!is24Hour) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = " ", fontSize = 11.sp) // aligner

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isAm) NeonCyan.copy(alpha = 0.25f) else SurfaceElevated)
                                .border(BorderStroke(1.dp, if (isAm) NeonCyan else GlassBorder), RoundedCornerShape(12.dp))
                                .clickable {
                                    isAm = true
                                    val h12 = displayHour % 12
                                    selectedHour24 = h12
                                    updateMinutes()
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = strings.amLabel,
                                color = if (isAm) NeonCyan else TextMuted,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (!isAm) NeonViolet.copy(alpha = 0.25f) else SurfaceElevated)
                                .border(BorderStroke(1.dp, if (!isAm) NeonViolet else GlassBorder), RoundedCornerShape(12.dp))
                                .clickable {
                                    isAm = false
                                    val h12 = displayHour % 12
                                    selectedHour24 = h12 + 12
                                    updateMinutes()
                                }
                                .padding(horizontal = 14.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = strings.pmLabel,
                                color = if (!isAm) NeonViolet else TextMuted,
                                fontSize = 12.sp,
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
private fun TimeStepColumn(
    value: Int,
    label: String,
    range: IntRange,
    step: Int = 1,
    formatPattern: String = "%02d",
    onValueChange: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, color = SoftLavender, fontSize = 11.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(4.dp))

        IconButton(
            onClick = {
                var next = value + step
                if (next > range.last) next = range.first
                onValueChange(next)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Increase",
                tint = NeonCyan,
                modifier = Modifier.size(22.dp)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceElevated)
                .border(BorderStroke(1.2.dp, NeonCyan.copy(alpha = 0.6f)), RoundedCornerShape(10.dp))
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = String.format(Locale.US, formatPattern, value),
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        IconButton(
            onClick = {
                var prev = value - step
                if (prev < range.first) prev = range.last
                onValueChange(prev)
            },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Decrease",
                tint = NeonCyan,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

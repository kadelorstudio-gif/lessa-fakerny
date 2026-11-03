package app.lessafakerny.android.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lessafakerny.android.ui.theme.NeonBlue
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonMagenta
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SurfaceCard
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary

import androidx.compose.material.icons.filled.MenuBook

enum class AppScreen {
    HOME, TIMETABLE, EXAMS, LEARN, STATS, SETTINGS, PRIVACY_POLICY, ABOUT
}

@Composable
fun GlassBottomNavBar(
    currentScreen: AppScreen,
    onScreenSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier,
    strings: app.lessafakerny.android.util.AppStrings = app.lessafakerny.android.util.LocaleManager.ARABIC
) {
    val dualBorderBrush = Brush.horizontalGradient(
        colors = listOf(
            NeonCyan.copy(alpha = 0.65f),
            Color.White.copy(alpha = 0.40f),
            NeonMagenta.copy(alpha = 0.50f),
            NeonViolet.copy(alpha = 0.60f)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .height(66.dp)
            .shadow(
                elevation = 20.dp,
                shape = RoundedCornerShape(34.dp),
                spotColor = NeonCyan.copy(alpha = 0.35f),
                ambientColor = NeonViolet.copy(alpha = 0.20f)
            )
            .clip(RoundedCornerShape(34.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        SurfaceCard.copy(alpha = 0.92f),
                        SurfaceElevated.copy(alpha = 0.82f),
                        SurfaceCard.copy(alpha = 0.95f)
                    )
                )
            )
            .border(BorderStroke(1.2.dp, dualBorderBrush), RoundedCornerShape(34.dp))
            .testTag("bottom_nav_bar")
    ) {
        // Specular Top Shine on Navigation Glass Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                    )
                )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 2.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(
                screen = AppScreen.HOME,
                title = strings.navHome,
                icon = Icons.Default.Today,
                isSelected = currentScreen == AppScreen.HOME,
                accentColor = NeonCyan,
                onClick = { onScreenSelected(AppScreen.HOME) }
            )

            NavBarItem(
                screen = AppScreen.TIMETABLE,
                title = strings.navTimetable,
                icon = Icons.Default.CalendarMonth,
                isSelected = currentScreen == AppScreen.TIMETABLE,
                accentColor = NeonBlue,
                onClick = { onScreenSelected(AppScreen.TIMETABLE) }
            )

            NavBarItem(
                screen = AppScreen.EXAMS,
                title = strings.navExams,
                icon = Icons.Default.Timer,
                isSelected = currentScreen == AppScreen.EXAMS,
                accentColor = NeonViolet,
                onClick = { onScreenSelected(AppScreen.EXAMS) }
            )

            NavBarItem(
                screen = AppScreen.LEARN,
                title = strings.navLearn,
                icon = Icons.AutoMirrored.Filled.MenuBook,
                isSelected = currentScreen == AppScreen.LEARN,
                accentColor = NeonCyan,
                onClick = { onScreenSelected(AppScreen.LEARN) }
            )

            NavBarItem(
                screen = AppScreen.STATS,
                title = strings.navStats,
                icon = Icons.Default.CheckCircle,
                isSelected = currentScreen == AppScreen.STATS,
                accentColor = NeonCyan,
                onClick = { onScreenSelected(AppScreen.STATS) }
            )

            NavBarItem(
                screen = AppScreen.SETTINGS,
                title = strings.navSettings,
                icon = Icons.Default.Settings,
                isSelected = currentScreen == AppScreen.SETTINGS,
                accentColor = NeonMagenta,
                onClick = { onScreenSelected(AppScreen.SETTINGS) }
            )
        }
    }
}

@Composable
fun NavBarItem(
    screen: AppScreen,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    val bgModifier = if (isSelected) {
        Modifier
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = accentColor.copy(alpha = 0.5f)
            )
            .clip(CircleShape)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.30f),
                        SurfaceCard.copy(alpha = 0.90f),
                        accentColor.copy(alpha = 0.15f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            accentColor.copy(alpha = 0.85f),
                            Color.White.copy(alpha = 0.5f),
                            accentColor.copy(alpha = 0.4f)
                        )
                    )
                ),
                CircleShape
            )
    } else {
        Modifier
            .clip(CircleShape)
            .background(Color.Transparent)
    }

    Box(
        modifier = Modifier
            .then(bgModifier)
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true, color = accentColor),
                onClick = onClick
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) accentColor else TextMuted,
                modifier = Modifier
                    .size(20.dp)
                    .then(
                        if (isSelected) Modifier.shadow(6.dp, spotColor = accentColor) else Modifier
                    )
            )

            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

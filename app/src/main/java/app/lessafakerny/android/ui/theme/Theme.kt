package app.lessafakerny.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val FuturisticDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = TextOnNeon,
    primaryContainer = SurfaceElevated,
    onPrimaryContainer = TextPrimary,
    secondary = NeonBlue,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceCard,
    onSecondaryContainer = SoftLavender,
    tertiary = NeonViolet,
    onTertiary = TextPrimary,
    background = BgMain,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDeepGlass,
    onSurfaceVariant = TextSecondary,
    outline = GlassBorder,
    error = StatusError,
    onError = Color.White
)

@Composable
fun LessafakernyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = FuturisticDarkColorScheme,
        typography = AppTypography,
        content = content
    )
}

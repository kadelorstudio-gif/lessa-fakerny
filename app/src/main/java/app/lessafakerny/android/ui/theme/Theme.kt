package app.lessafakerny.android.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColorsPalette(
    val id: String,
    val name: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val tertiaryAccent: Color,
    val accentBlue: Color,
    val bgMain: Color,
    val bgSecondary: Color,
    val surfaceElevated: Color,
    val surfaceCard: Color,
    val glassBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val glowTop: Color,
    val glowBottom: Color,
    val glowMiddle: Color
)

val NeonMidnightPalette = AppColorsPalette(
    id = "NEON_MIDNIGHT",
    name = "Neon Midnight",
    primaryAccent = NeonCyan,
    secondaryAccent = NeonViolet,
    tertiaryAccent = NeonMagenta,
    accentBlue = NeonBlue,
    bgMain = BgMain,
    bgSecondary = BgSecondary,
    surfaceElevated = SurfaceElevated,
    surfaceCard = SurfaceCard,
    glassBorder = GlassBorder,
    textPrimary = TextPrimary,
    textSecondary = TextSecondary,
    textMuted = TextMuted,
    glowTop = NeonViolet.copy(alpha = 0.28f),
    glowBottom = NeonCyan.copy(alpha = 0.22f),
    glowMiddle = NeonMagenta.copy(alpha = 0.14f)
)

val SapphireFrostPalette = AppColorsPalette(
    id = "SAPPHIRE_FROST",
    name = "Sapphire Frost",
    primaryAccent = Color(0xFF38BDF8),
    secondaryAccent = Color(0xFF2563EB),
    tertiaryAccent = Color(0xFF818CF8),
    accentBlue = Color(0xFF60A5FA),
    bgMain = Color(0xFF030A1C),
    bgSecondary = Color(0xFF081432),
    surfaceElevated = Color(0xFF0E2352),
    surfaceCard = Color(0xFF0C1D42),
    glassBorder = Color(0x4038BDF8),
    textPrimary = Color(0xFFF0F6FF),
    textSecondary = Color(0xFF93C5FD),
    textMuted = Color(0xFF64748B),
    glowTop = Color(0xFF2563EB).copy(alpha = 0.32f),
    glowBottom = Color(0xFF38BDF8).copy(alpha = 0.24f),
    glowMiddle = Color(0xFF818CF8).copy(alpha = 0.15f)
)

val EmeraldGlowPalette = AppColorsPalette(
    id = "EMERALD_GLOW",
    name = "Emerald Glow",
    primaryAccent = Color(0xFF35D6A0),
    secondaryAccent = Color(0xFF059669),
    tertiaryAccent = Color(0xFF34D399),
    accentBlue = Color(0xFF10B981),
    bgMain = Color(0xFF02120B),
    bgSecondary = Color(0xFF042013),
    surfaceElevated = Color(0xFF0C3825),
    surfaceCard = Color(0xFF082B1C),
    glassBorder = Color(0x4035D6A0),
    textPrimary = Color(0xFFF0FDF4),
    textSecondary = Color(0xFFA7F3D0),
    textMuted = Color(0xFF6EE7B7).copy(alpha = 0.6f),
    glowTop = Color(0xFF35D6A0).copy(alpha = 0.30f),
    glowBottom = Color(0xFF10B981).copy(alpha = 0.22f),
    glowMiddle = Color(0xFF059669).copy(alpha = 0.16f)
)

val AmethystSunsetPalette = AppColorsPalette(
    id = "AMETHYST_SUNSET",
    name = "Amethyst Sunset",
    primaryAccent = Color(0xFFFF45D4),
    secondaryAccent = Color(0xFFF59E0B),
    tertiaryAccent = Color(0xFFEC4899),
    accentBlue = Color(0xFFFB7185),
    bgMain = Color(0xFF160414),
    bgSecondary = Color(0xFF23081E),
    surfaceElevated = Color(0xFF3D1335),
    surfaceCard = Color(0xFF2D0E27),
    glassBorder = Color(0x40FF45D4),
    textPrimary = Color(0xFFFFF1F2),
    textSecondary = Color(0xFFFECDD3),
    textMuted = Color(0xFFFDA4AF).copy(alpha = 0.6f),
    glowTop = Color(0xFFFF45D4).copy(alpha = 0.30f),
    glowBottom = Color(0xFFF59E0B).copy(alpha = 0.22f),
    glowMiddle = Color(0xFFEC4899).copy(alpha = 0.16f)
)

fun getThemePalette(themeId: String): AppColorsPalette {
    return when (themeId) {
        "SAPPHIRE_FROST" -> SapphireFrostPalette
        "EMERALD_GLOW" -> EmeraldGlowPalette
        "AMETHYST_SUNSET" -> AmethystSunsetPalette
        else -> NeonMidnightPalette
    }
}

val LocalThemePalette = staticCompositionLocalOf { NeonMidnightPalette }

object AppTheme {
    val colors: AppColorsPalette
        @Composable
        get() = LocalThemePalette.current
}

fun createColorSchemeForPalette(palette: AppColorsPalette): ColorScheme {
    return darkColorScheme(
        primary = palette.primaryAccent,
        onPrimary = palette.bgMain,
        primaryContainer = palette.surfaceElevated,
        onPrimaryContainer = palette.textPrimary,
        secondary = palette.secondaryAccent,
        onSecondary = palette.textPrimary,
        secondaryContainer = palette.surfaceCard,
        onSecondaryContainer = palette.textSecondary,
        tertiary = palette.tertiaryAccent,
        onTertiary = palette.textPrimary,
        background = palette.bgMain,
        onBackground = palette.textPrimary,
        surface = palette.surfaceCard,
        onSurface = palette.textPrimary,
        surfaceVariant = palette.surfaceElevated,
        onSurfaceVariant = palette.textSecondary,
        outline = palette.glassBorder,
        error = StatusError,
        onError = Color.White
    )
}

@Composable
fun LessafakernyTheme(
    activeThemeId: String = "NEON_MIDNIGHT",
    content: @Composable () -> Unit
) {
    val palette = getThemePalette(activeThemeId)
    val colorScheme = createColorSchemeForPalette(palette)

    CompositionLocalProvider(LocalThemePalette provides palette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

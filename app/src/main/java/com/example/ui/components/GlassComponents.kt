package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BgMain
import com.example.ui.theme.BgSecondary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassBorderSoft
import com.example.ui.theme.GlassFill
import com.example.ui.theme.GlassFillSubtle
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextOnNeon
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Atmospheric background recreating the iconic neon cosmos environment:
 * - Deep navy gradient canvas (#050816 to #080D20)
 * - Shimmering ambient radial glow spheres (cyan, magenta, violet)
 * - Subtle cosmic particles and celestial orbital rings for depth
 */
@Composable
fun GlassBackground(
    themeId: String = "NEON_MIDNIGHT",
    content: @Composable BoxScope.() -> Unit
) {
    val (glowTop, glowBottom, glowMiddle) = when (themeId) {
        "SAPPHIRE_FROST" -> Triple(
            NeonBlue.copy(alpha = 0.28f),
            NeonCyan.copy(alpha = 0.22f),
            SoftLavender.copy(alpha = 0.12f)
        )
        "EMERALD_GLOW" -> Triple(
            Color(0xFF35D6A0).copy(alpha = 0.26f),
            NeonCyan.copy(alpha = 0.20f),
            NeonBlue.copy(alpha = 0.12f)
        )
        "AMETHYST_SUNSET" -> Triple(
            NeonMagenta.copy(alpha = 0.28f),
            NeonViolet.copy(alpha = 0.22f),
            NeonCyan.copy(alpha = 0.12f)
        )
        else -> Triple(
            NeonViolet.copy(alpha = 0.25f),
            NeonCyan.copy(alpha = 0.22f),
            NeonMagenta.copy(alpha = 0.14f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val pulseOffset by infiniteTransition.animateFloat(
        initialValue = -35f,
        targetValue = 35f,
        animationSpec = infiniteRepeatable(
            animation = tween(4500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.75f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha_pulse"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BgMain, BgSecondary, BgMain)
                )
            )
    ) {
        // Celestial Orbital Motif & Ambient Glowing Nebulae
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Top-right prominent cyan/blue luminous orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowTop.copy(alpha = glowTop.alpha * pulseAlpha), Color.Transparent),
                    center = Offset(canvasWidth * 0.85f, canvasHeight * 0.10f),
                    radius = 450f + pulseOffset
                )
            )

            // Bottom-left prominent violet/magenta luminous orb
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowBottom.copy(alpha = glowBottom.alpha * pulseAlpha), Color.Transparent),
                    center = Offset(canvasWidth * 0.12f, canvasHeight * 0.78f),
                    radius = 480f - pulseOffset
                )
            )

            // Center subtle ambient highlight
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowMiddle.copy(alpha = glowMiddle.alpha * 0.8f), Color.Transparent),
                    center = Offset(canvasWidth * 0.65f, canvasHeight * 0.45f),
                    radius = 360f + (pulseOffset * 0.5f)
                )
            )

            // Cosmic orbital rings for futuristic depth
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        NeonCyan.copy(alpha = 0.08f),
                        NeonMagenta.copy(alpha = 0.05f),
                        NeonViolet.copy(alpha = 0.08f),
                        NeonCyan.copy(alpha = 0.08f)
                    ),
                    center = Offset(canvasWidth * 0.5f, canvasHeight * 0.18f)
                ),
                radius = canvasWidth * 0.72f,
                center = Offset(canvasWidth * 0.5f, canvasHeight * 0.18f),
                style = Stroke(width = 1.2.dp.toPx())
            )

            drawCircle(
                color = SoftLavender.copy(alpha = 0.035f),
                radius = canvasWidth * 1.1f,
                center = Offset(canvasWidth * 0.5f, canvasHeight * 0.65f),
                style = Stroke(width = 1.dp.toPx())
            )

            // Glowing distant stardust points
            val starPositions = listOf(
                Offset(canvasWidth * 0.15f, canvasHeight * 0.08f),
                Offset(canvasWidth * 0.82f, canvasHeight * 0.25f),
                Offset(canvasWidth * 0.35f, canvasHeight * 0.35f),
                Offset(canvasWidth * 0.90f, canvasHeight * 0.62f),
                Offset(canvasWidth * 0.08f, canvasHeight * 0.50f),
                Offset(canvasWidth * 0.75f, canvasHeight * 0.88f)
            )
            for (idx in starPositions.indices) {
                val pos = starPositions[idx]
                val starAlpha = (0.25f + 0.25f * kotlin.math.sin((pulseOffset + idx * 30) * 0.05f)).coerceIn(0.1f, 0.6f)
                drawCircle(
                    color = Color.White.copy(alpha = starAlpha),
                    radius = if (idx % 2 == 0) 1.8f else 1.2f,
                    center = pos
                )
            }
        }

        content()
    }
}

/**
 * Signature Ultra-Modern Glassmorphism Card:
 * Frosted translucent glass surface with multi-layer specular highlights,
 * beveled top shine reflection, vibrant neon edge glowing borders, and luminous spot shadows.
 */
@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(22.dp),
    tintColor: Color = NeonCyan,
    isGlowing: Boolean = false,
    useDualNeonBorder: Boolean = false,
    onClick: (() -> Unit)? = null,
    testTag: String = "glass_card",
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "card_glow")
    val pulseAlphaState = infiniteTransition.animateFloat(
        initialValue = 0.55f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val pulseAlpha = if (isGlowing) pulseAlphaState.value else 0.40f

    val borderBrush = when {
        useDualNeonBorder -> Brush.linearGradient(
            colors = listOf(
                NeonCyan.copy(alpha = pulseAlpha),
                Color.White.copy(alpha = 0.6f),
                NeonMagenta.copy(alpha = pulseAlpha * 0.9f),
                NeonViolet.copy(alpha = pulseAlpha * 0.6f)
            )
        )
        isGlowing -> Brush.linearGradient(
            colors = listOf(
                tintColor.copy(alpha = pulseAlpha),
                Color.White.copy(alpha = 0.45f),
                tintColor.copy(alpha = pulseAlpha * 0.5f),
                tintColor.copy(alpha = 0.15f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                tintColor.copy(alpha = 0.35f),
                Color.White.copy(alpha = 0.12f),
                GlassBorderSoft.copy(alpha = 0.25f),
                Color.White.copy(alpha = 0.05f)
            )
        )
    }

    Surface(
        modifier = modifier
            .testTag(testTag)
            .shadow(
                elevation = if (isGlowing) 16.dp else 6.dp,
                shape = shape,
                spotColor = tintColor.copy(alpha = if (isGlowing) 0.45f else 0.14f),
                ambientColor = tintColor.copy(alpha = if (isGlowing) 0.30f else 0.08f)
            )
            .clip(shape)
            .border(BorderStroke(1.2.dp, borderBrush), shape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            SurfaceCard.copy(alpha = 0.88f),
                            SurfaceElevated.copy(alpha = 0.78f),
                            SurfaceCard.copy(alpha = 0.92f)
                        )
                    )
                )
        ) {
            // Authentic Frosted Glass Specular Top Highlight (Glassmorphic shine)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.09f),
                                Color.White.copy(alpha = 0.02f),
                                Color.Transparent
                            )
                        )
                    )
            )

            content()
        }
    }
}

/**
 * Signature Neon Primary Action Button:
 * Electric Cyan-to-Blue luminous pill with high-contrast text and tactile ripple.
 */
@Composable
fun GlassButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    accentColor: Color = NeonCyan,
    testTag: String = "glass_button",
    content: @Composable () -> Unit
) {
    val buttonGradient = if (enabled) {
        Brush.horizontalGradient(
            colors = listOf(
                accentColor,
                NeonBlue,
                accentColor
            )
        )
    } else {
        Brush.horizontalGradient(
            colors = listOf(
                SurfaceElevated.copy(alpha = 0.6f),
                SurfaceElevated.copy(alpha = 0.6f)
            )
        )
    }

    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .testTag(testTag)
            .height(54.dp)
            .shadow(
                elevation = if (enabled) 14.dp else 0.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = accentColor.copy(alpha = 0.6f),
                ambientColor = NeonViolet.copy(alpha = 0.35f)
            )
            .clip(RoundedCornerShape(20.dp))
            .border(
                BorderStroke(
                    1.2.dp,
                    if (enabled) {
                        Brush.horizontalGradient(
                            listOf(Color.White.copy(alpha = 0.7f), accentColor, Color.White.copy(alpha = 0.3f))
                        )
                    } else {
                        BorderStroke(1.dp, GlassBorder).brush
                    }
                ),
                RoundedCornerShape(20.dp)
            ),
        color = Color.Transparent,
        contentColor = TextOnNeon
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(buttonGradient),
            contentAlignment = Alignment.Center
        ) {
            // Top specular shine
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.White.copy(alpha = 0.30f), Color.Transparent)
                        )
                    )
            )

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                content()
            }
        }
    }
}

/**
 * Secondary Outline Glass Button with fine luminous border and specular inner glow.
 */
@Composable
fun GlassOutlineButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    borderColor: Color = NeonCyan,
    testTag: String = "glass_outline_button",
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .height(50.dp)
            .shadow(
                elevation = if (enabled) 6.dp else 0.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = borderColor.copy(alpha = 0.3f)
            )
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        SurfaceCard.copy(alpha = 0.85f),
                        SurfaceElevated.copy(alpha = 0.70f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            borderColor.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.3f),
                            borderColor.copy(alpha = 0.4f)
                        )
                    )
                ),
                RoundedCornerShape(18.dp)
            )
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Luminous Glass Icon Button with tactile glowing feedback.
 */
@Composable
fun GlassIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accentColor: Color = NeonCyan,
    enabled: Boolean = true,
    size: Dp = 44.dp,
    testTag: String = "glass_icon_button",
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .size(size)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                spotColor = accentColor.copy(alpha = 0.4f),
                ambientColor = accentColor.copy(alpha = 0.2f)
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        accentColor.copy(alpha = 0.22f),
                        SurfaceCard.copy(alpha = 0.85f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.2.dp,
                    Brush.sweepGradient(
                        listOf(
                            accentColor.copy(alpha = 0.7f),
                            Color.White.copy(alpha = 0.4f),
                            accentColor.copy(alpha = 0.3f),
                            accentColor.copy(alpha = 0.7f)
                        )
                    )
                ),
                CircleShape
            )
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * Clean, deep navy text field with glowing electric cyan border when focused.
 */
@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    singleLine: Boolean = true,
    testTag: String = "glass_text_field"
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary, fontSize = 13.sp) },
        placeholder = { Text(placeholder, color = TextMuted, fontSize = 13.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag)
            .background(SurfaceElevated.copy(alpha = 0.65f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = GlassBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            cursorColor = NeonCyan
        )
    )
}

/**
 * Luminous micro-pill badge for categories, days, and attendance indicators with glowing glass effect.
 */
@Composable
fun GlassBadge(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = NeonCyan,
    icon: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .shadow(
                elevation = 6.dp,
                shape = CircleShape,
                spotColor = color.copy(alpha = 0.35f),
                ambientColor = color.copy(alpha = 0.15f)
            )
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        color.copy(alpha = 0.20f),
                        SurfaceCard.copy(alpha = 0.85f),
                        color.copy(alpha = 0.12f)
                    )
                )
            )
            .border(
                BorderStroke(
                    1.2.dp,
                    Brush.horizontalGradient(
                        listOf(
                            color.copy(alpha = 0.75f),
                            Color.White.copy(alpha = 0.4f),
                            color.copy(alpha = 0.35f)
                        )
                    )
                ),
                CircleShape
            )
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                icon()
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                color = color,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Sleek native test ad card integrated naturally as an illuminated glass card.
 */
@Composable
fun GlassNativeAdCard(
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        tintColor = StatusWarning,
        shape = RoundedCornerShape(18.dp),
        testTag = "native_ad_card"
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(StatusWarning.copy(alpha = 0.16f))
                    .border(BorderStroke(1.dp, StatusWarning.copy(alpha = 0.35f)), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Sponsored",
                    tint = StatusWarning,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "إعلان مدعوم • Test Native Ad",
                        color = StatusWarning,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(StatusWarning.copy(alpha = 0.25f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text("Ad", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "نصيحة دراسية: نظّم وقتك واستخدم الخرائط الذهنية لتفوق دراسي مضمون!",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * Bottom sticky banner ad container with fine glass border.
 */
@Composable
fun GlassBannerAd(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            .background(SurfaceCard.copy(alpha = 0.95f))
            .border(BorderStroke(1.dp, GlassBorder), RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.ui.viewinterop.AndroidView(
            factory = { context ->
                com.google.android.gms.ads.AdView(context).apply {
                    setAdSize(com.google.android.gms.ads.AdSize.BANNER)
                    adUnitId = com.example.util.AdConstants.BANNER_AD_UNIT_ID
                    loadAd(com.google.android.gms.ads.AdRequest.Builder().build())
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

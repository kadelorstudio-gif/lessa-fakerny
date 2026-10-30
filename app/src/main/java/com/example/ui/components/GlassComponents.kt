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
 * - Subtle ambient radial glows (cyan top-left & violet bottom-right)
 * - Minimal celestial orbital rings for futuristic depth
 */
@Composable
fun GlassBackground(
    themeId: String = "NEON_MIDNIGHT",
    content: @Composable BoxScope.() -> Unit
) {
    val (glowTop, glowBottom) = when (themeId) {
        "SAPPHIRE_FROST" -> Pair(
            NeonBlue.copy(alpha = 0.22f),
            NeonCyan.copy(alpha = 0.16f)
        )
        "EMERALD_GLOW" -> Pair(
            Color(0xFF35D6A0).copy(alpha = 0.20f),
            NeonCyan.copy(alpha = 0.14f)
        )
        "AMETHYST_SUNSET" -> Pair(
            NeonMagenta.copy(alpha = 0.20f),
            NeonViolet.copy(alpha = 0.16f)
        )
        else -> Pair(
            NeonViolet.copy(alpha = 0.18f),
            NeonCyan.copy(alpha = 0.15f)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val pulseOffset by infiniteTransition.animateFloat(
        initialValue = -20f,
        targetValue = 20f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
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
        // Celestial Orbital Motif Canvas in background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            // Top-end subtle radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowTop, Color.Transparent),
                    center = Offset(canvasWidth * 0.85f, canvasHeight * 0.12f),
                    radius = 380f + pulseOffset
                )
            )

            // Bottom-start subtle radial glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(glowBottom, Color.Transparent),
                    center = Offset(canvasWidth * 0.15f, canvasHeight * 0.75f),
                    radius = 420f - pulseOffset
                )
            )

            // Very subtle cosmic orbital arc (inspired by the icon's clock orbital ring)
            drawCircle(
                color = SoftLavender.copy(alpha = 0.035f),
                radius = canvasWidth * 0.75f,
                center = Offset(canvasWidth * 0.5f, canvasHeight * 0.2f),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        content()
    }
}

/**
 * Signature Glassmorphism Card:
 * Deep translucent navy surface with fine low-opacity neon borders,
 * soft ambient glow, and optional illuminated dual-gradient highlight.
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
        initialValue = 0.45f,
        targetValue = 0.90f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )
    val pulseAlpha = if (isGlowing) pulseAlphaState.value else 0.35f

    val borderBrush = when {
        useDualNeonBorder -> Brush.linearGradient(
            colors = listOf(
                NeonCyan.copy(alpha = pulseAlpha),
                NeonMagenta.copy(alpha = pulseAlpha * 0.85f),
                NeonViolet.copy(alpha = pulseAlpha * 0.5f)
            )
        )
        isGlowing -> Brush.linearGradient(
            colors = listOf(
                tintColor.copy(alpha = pulseAlpha),
                Color.White.copy(alpha = 0.25f),
                tintColor.copy(alpha = pulseAlpha * 0.4f)
            )
        )
        else -> Brush.linearGradient(
            colors = listOf(
                tintColor.copy(alpha = 0.22f),
                GlassBorderSoft,
                Color.White.copy(alpha = 0.04f)
            )
        )
    }

    Surface(
        modifier = modifier
            .testTag(testTag)
            .shadow(
                elevation = if (isGlowing) 12.dp else 4.dp,
                shape = shape,
                spotColor = tintColor.copy(alpha = if (isGlowing) 0.30f else 0.08f),
                ambientColor = tintColor.copy(alpha = if (isGlowing) 0.20f else 0.05f)
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
                            SurfaceCard.copy(alpha = 0.92f),
                            SurfaceElevated.copy(alpha = 0.85f),
                            SurfaceCard.copy(alpha = 0.95f)
                        )
                    )
                )
        ) {
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
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .shadow(
                elevation = if (enabled) 12.dp else 0.dp,
                shape = RoundedCornerShape(18.dp),
                spotColor = accentColor.copy(alpha = 0.5f)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = accentColor,
            contentColor = TextOnNeon,
            disabledContainerColor = SurfaceElevated.copy(alpha = 0.6f),
            disabledContentColor = TextMuted
        )
    ) {
        content()
    }
}

/**
 * Secondary Outline Glass Button with fine luminous border.
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
            .height(48.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(GlassFillSubtle)
            .border(BorderStroke(1.dp, borderColor.copy(alpha = 0.5f)), RoundedCornerShape(16.dp))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 16.dp),
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
 * Luminous micro-pill badge for categories, days, and attendance indicators.
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
            .clip(CircleShape)
            .background(color.copy(alpha = 0.14f))
            .border(BorderStroke(1.dp, color.copy(alpha = 0.45f)), CircleShape)
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

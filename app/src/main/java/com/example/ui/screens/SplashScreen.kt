package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.GlassBackground
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.80f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_spin")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "halo_rotation"
    )

    val haloPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "halo_pulse"
    )

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 500)
        )
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 1100, easing = FastOutSlowInEasing)
        )
        delay(350)
        onSplashFinished()
    }

    GlassBackground {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
                .testTag("splash_screen"),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))

            // Central Luminous Glass Capsule with Rotating Neon Orbit
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .scale(scale.value)
                    .alpha(alpha.value),
                contentAlignment = Alignment.Center
            ) {
                // Rotating Cosmic Neon Halo Ring
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(rotation)
                ) {
                    val strokeWidth = 3.dp.toPx()
                    val radius = (size.minDimension / 2f) - (strokeWidth / 2f) - 6.dp.toPx()
                    drawCircle(
                        brush = Brush.sweepGradient(
                            listOf(
                                NeonCyan,
                                NeonBlue,
                                NeonViolet,
                                NeonMagenta,
                                NeonCyan
                            )
                        ),
                        radius = radius * haloPulse,
                        center = Offset(size.width / 2f, size.height / 2f),
                        style = Stroke(width = strokeWidth)
                    )
                }

                // Ambient Radial Flare Glow
                Canvas(modifier = Modifier.size(190.dp)) {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                NeonCyan.copy(alpha = 0.35f * haloPulse),
                                NeonViolet.copy(alpha = 0.20f),
                                Color.Transparent
                            )
                        ),
                        radius = size.width / 2f
                    )
                }

                // Frosted Glass Beveled Tile encasing the Logo
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .shadow(
                            elevation = 32.dp,
                            shape = RoundedCornerShape(38.dp),
                            spotColor = NeonCyan.copy(alpha = 0.65f),
                            ambientColor = NeonViolet.copy(alpha = 0.45f)
                        )
                        .clip(RoundedCornerShape(38.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    SurfaceCard.copy(alpha = 0.90f),
                                    SurfaceElevated.copy(alpha = 0.75f),
                                    SurfaceCard.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                1.5.dp,
                                Brush.linearGradient(
                                    listOf(
                                        Color.White.copy(alpha = 0.8f),
                                        NeonCyan,
                                        NeonMagenta,
                                        Color.White.copy(alpha = 0.3f)
                                    )
                                )
                            ),
                            RoundedCornerShape(38.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // Specular Top Shine on Glass Tile
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                            .align(Alignment.TopCenter)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.White.copy(alpha = 0.22f), Color.Transparent)
                                )
                            )
                    )

                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(125.dp)
                            .clip(RoundedCornerShape(30.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Glowing App Title with Multi-Color Gradient
            Text(
                text = "لسه فاكرني؟",
                style = TextStyle(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            NeonCyan,
                            Color.White,
                            SoftLavender,
                            NeonMagenta
                        )
                    ),
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier
                    .alpha(alpha.value)
                    .shadow(
                        elevation = 16.dp,
                        spotColor = NeonCyan.copy(alpha = 0.5f)
                    )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Ultra-modern glowing glass badge
            Box(
                modifier = Modifier
                    .alpha(alpha.value)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        spotColor = NeonViolet.copy(alpha = 0.35f)
                    )
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                NeonViolet.copy(alpha = 0.25f),
                                SurfaceCard.copy(alpha = 0.85f),
                                NeonCyan.copy(alpha = 0.20f)
                            )
                        )
                    )
                    .border(
                        BorderStroke(
                            1.dp,
                            Brush.horizontalGradient(
                                listOf(NeonCyan.copy(alpha = 0.6f), Color.White.copy(alpha = 0.3f), NeonViolet.copy(alpha = 0.6f))
                            )
                        ),
                        CircleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 7.dp)
            ) {
                Text(
                    text = "منظّم المحاضرات والامتحانات الذكي • إصدار زجاجي متوهج 🎓",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Futuristic Glowing Glass Progress Track
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp)
                    .alpha(alpha.value),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(SurfaceElevated.copy(alpha = 0.8f))
                        .border(BorderStroke(0.8.dp, Color.White.copy(alpha = 0.15f)), CircleShape)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .height(6.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        NeonCyan,
                                        NeonBlue,
                                        NeonViolet,
                                        NeonMagenta
                                    )
                                )
                            )
                            .shadow(
                                elevation = 6.dp,
                                spotColor = NeonCyan
                            )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(NeonCyan)
                            .shadow(4.dp, spotColor = NeonCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "V1.0 • NEON GLASS",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

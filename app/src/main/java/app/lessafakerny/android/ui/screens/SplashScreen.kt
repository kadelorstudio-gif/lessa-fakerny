package app.lessafakerny.android.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lessafakerny.android.R
import app.lessafakerny.android.ui.components.GlassBackground
import app.lessafakerny.android.ui.theme.NeonBlue
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonMagenta
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.SurfaceCard
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.85f) }
    val alpha = remember { Animatable(0f) }
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
        )
        progress.animateTo(
            targetValue = 1.0f,
            animationSpec = tween(durationMillis = 850, easing = FastOutSlowInEasing)
        )
        delay(250)
        onSplashFinished()
    }

    GlassBackground {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .testTag("splash_screen"),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                // Central Hero: Sleek, Floating Minimalist Glass Card with App Logo
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .scale(scale.value)
                        .alpha(alpha.value)
                ) {
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .shadow(
                                elevation = 30.dp,
                                shape = RoundedCornerShape(36.dp),
                                spotColor = NeonCyan.copy(alpha = 0.55f),
                                ambientColor = NeonViolet.copy(alpha = 0.35f)
                            )
                            .clip(RoundedCornerShape(36.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        SurfaceCard.copy(alpha = 0.92f),
                                        SurfaceElevated.copy(alpha = 0.78f),
                                        SurfaceCard.copy(alpha = 0.96f)
                                    )
                                )
                            )
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.linearGradient(
                                        listOf(
                                            Color.White.copy(alpha = 0.85f),
                                            NeonCyan.copy(alpha = 0.8f),
                                            NeonViolet.copy(alpha = 0.6f),
                                            Color.White.copy(alpha = 0.25f)
                                        )
                                    )
                                ),
                                RoundedCornerShape(36.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        // Glossy specular highlight
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(42.dp)
                                .align(Alignment.TopCenter)
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color.White.copy(alpha = 0.25f), Color.Transparent)
                                    )
                                )
                        )

                        Image(
                            painter = painterResource(id = R.drawable.app_logo),
                            contentDescription = "Logo",
                            modifier = Modifier
                                .size(112.dp)
                                .clip(RoundedCornerShape(26.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Clean, Bold Luminous Title
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
                            fontSize = 34.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.shadow(
                            elevation = 16.dp,
                            spotColor = NeonCyan.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Minimalist Glass Subtitle Badge
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(SurfaceElevated.copy(alpha = 0.75f))
                            .border(
                                BorderStroke(
                                    1.dp,
                                    Brush.horizontalGradient(
                                        listOf(NeonCyan.copy(alpha = 0.5f), NeonViolet.copy(alpha = 0.5f))
                                    )
                                ),
                                CircleShape
                            )
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "منظّمك الجامعي الذكي 🎓",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Bottom Minimal Progress Track
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .alpha(alpha.value)
                        .padding(bottom = 20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .width(130.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(SurfaceElevated.copy(alpha = 0.8f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(progress.value)
                                .height(4.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(NeonCyan, NeonBlue, NeonMagenta)
                                    )
                                )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "NEON EDITION",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }
            }
        }
    }
}

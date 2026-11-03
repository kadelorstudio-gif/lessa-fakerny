package app.lessafakerny.android.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.lessafakerny.android.ui.components.GlassButton
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.util.AppStrings

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    strings: AppStrings,
    language: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isEn = language.equals("en", ignoreCase = true)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 18.dp)
            .testTag("privacy_policy_screen")
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top Navigation Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.4f)), CircleShape)
                    .size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = NeonCyan
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = strings.privacyPolicyTitle,
                    color = NeonCyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.privacyPolicySubtitle,
                    color = SoftLavender,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Hero Shield Card
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                tintColor = NeonCyan,
                useDualNeonBorder = true,
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(BorderStroke(1.2.dp, NeonCyan), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isEn) "Your Privacy is Secured" else "خصوصيتك محمية بأمان",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isEn) "Offline-First architecture with encrypted Firebase sync" else "معمارية حفظ محلي أوفلاين ومزامنة تشفير مع سحابة Firebase",
                            color = SoftLavender,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Section 1
            PrivacySectionCard(
                title = strings.privacyPolicySection1Title,
                body = strings.privacyPolicySection1Body
            )

            // Section 2
            PrivacySectionCard(
                title = strings.privacyPolicySection2Title,
                body = strings.privacyPolicySection2Body
            )

            // Section 3
            PrivacySectionCard(
                title = strings.privacyPolicySection3Title,
                body = strings.privacyPolicySection3Body
            )

            // Section 4
            PrivacySectionCard(
                title = strings.privacyPolicySection4Title,
                body = strings.privacyPolicySection4Body
            )

            // Section 5: Contact
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                tintColor = NeonViolet,
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = strings.privacyPolicySection5Title,
                        color = NeonViolet,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = strings.privacyPolicySection5Body,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    GlassButton(
                        onClick = {
                            try {
                                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:Kadelor-studio@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Privacy Inquiry - Lessa Fakerny?")
                                }
                                context.startActivity(emailIntent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Kadelor-studio@gmail.com", Toast.LENGTH_LONG).show()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        accentColor = NeonViolet
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Kadelor-studio@gmail.com", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun PrivacySectionCard(
    title: String,
    body: String
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        tintColor = NeonCyan,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = NeonCyan,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = body,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    }
}

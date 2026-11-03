package app.lessafakerny.android.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import app.lessafakerny.android.ui.components.GlassButton
import app.lessafakerny.android.ui.components.GlassCard
import app.lessafakerny.android.ui.theme.NeonCyan
import app.lessafakerny.android.ui.theme.NeonMagenta
import app.lessafakerny.android.ui.theme.NeonViolet
import app.lessafakerny.android.ui.theme.SoftLavender
import app.lessafakerny.android.ui.theme.StatusError
import app.lessafakerny.android.ui.theme.SurfaceCard
import app.lessafakerny.android.ui.theme.SurfaceElevated
import app.lessafakerny.android.ui.theme.TextMuted
import app.lessafakerny.android.ui.theme.TextPrimary
import app.lessafakerny.android.ui.theme.TextSecondary
import app.lessafakerny.android.util.AppStrings
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Composable
fun UserProfileDialog(
    user: FirebaseUser?,
    userRole: String,
    strings: AppStrings,
    language: String,
    onDismiss: () -> Unit,
    onOpenAuth: () -> Unit,
    onLogout: () -> Unit
) {
    var showAuth by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var isSendingEmail by remember { mutableStateOf(false) }
    var isReloadingUser by remember { mutableStateOf(false) }
    var cooldownTimer by remember { mutableStateOf(0) }
    var statusMessage by remember { mutableStateOf("") }
    var isVerifiedState by remember(user) { mutableStateOf(user?.isEmailVerified == true) }

    val isEn = language.equals("en", ignoreCase = true)

    androidx.compose.runtime.LaunchedEffect(cooldownTimer) {
        if (cooldownTimer > 0) {
            kotlinx.coroutines.delay(1000L)
            cooldownTimer -= 1
        }
    }

    if (showAuth) {
        AuthDialog(
            onDismiss = { showAuth = false },
            onAuthSuccess = {
                showAuth = false
                onDismiss()
            },
            strings = strings,
            language = language
        )
    } else {
        Dialog(onDismissRequest = onDismiss) {
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("user_profile_dialog"),
                shape = RoundedCornerShape(26.dp),
                tintColor = NeonCyan,
                useDualNeonBorder = true
            ) {
                Column(
                    modifier = Modifier
                        .padding(22.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(NeonCyan.copy(alpha = 0.25f), NeonViolet.copy(alpha = 0.3f))
                                )
                            )
                            .border(BorderStroke(2.dp, NeonCyan), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = strings.profileHeaderTitle,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Student Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(BorderStroke(1.dp, NeonCyan), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = strings.roleStudentLabel,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (user != null) {
                        // User info details card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceCard)
                                .border(BorderStroke(1.dp, NeonCyan.copy(alpha = 0.3f)), RoundedCornerShape(14.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = NeonCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = user.email ?: "Google Account",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Badge,
                                        contentDescription = null,
                                        tint = SoftLavender,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${strings.userIdLabel} ${user.uid.take(12)}...",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = if (isVerifiedState) NeonCyan else StatusError,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (isVerifiedState) strings.verifiedBadge else strings.notVerifiedBadge,
                                        color = if (isVerifiedState) NeonCyan else StatusError,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Real Email Verification Controls (if unverified)
                        if (!isVerifiedState) {
                            Spacer(modifier = Modifier.height(12.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(NeonViolet.copy(alpha = 0.12f))
                                    .border(BorderStroke(1.dp, NeonViolet.copy(alpha = 0.4f)), RoundedCornerShape(14.dp))
                                    .padding(12.dp)
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = if (isEn) "Verify your email address to secure your account." else "يرجى تفعيل البريد الإلكتروني لتأمين الحساب واستعادة البيانات.",
                                        color = SoftLavender,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )

                                    if (statusMessage.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = statusMessage,
                                            color = NeonCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        // Resend verification button
                                        GlassButton(
                                            onClick = {
                                                if (cooldownTimer == 0 && !isSendingEmail) {
                                                    isSendingEmail = true
                                                    statusMessage = ""
                                                    user.sendEmailVerification()
                                                        .addOnCompleteListener { task ->
                                                            isSendingEmail = false
                                                            if (task.isSuccessful) {
                                                                cooldownTimer = 30
                                                                statusMessage = if (isEn) "Verification email sent!" else "تم إرسال رابط التفعيل بنجاح!"
                                                            } else {
                                                                statusMessage = task.exception?.localizedMessage ?: (if (isEn) "Failed to send email" else "فشل إرسال البريد")
                                                            }
                                                        }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            enabled = cooldownTimer == 0 && !isSendingEmail,
                                            accentColor = NeonViolet,
                                            testTag = "resend_verification_btn"
                                        ) {
                                            Text(
                                                text = if (cooldownTimer > 0) {
                                                    if (isEn) "Wait ${cooldownTimer}s" else "انتظر ${cooldownTimer} ثانية"
                                                } else if (isSendingEmail) {
                                                    if (isEn) "Sending..." else "جاري الإرسال..."
                                                } else {
                                                    if (isEn) "Resend Email" else "إعادة إرسال البريد"
                                                },
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        // Check verification status button
                                        app.lessafakerny.android.ui.components.GlassOutlineButton(
                                            onClick = {
                                                if (!isReloadingUser) {
                                                    isReloadingUser = true
                                                    user.reload().addOnCompleteListener { task ->
                                                        isReloadingUser = false
                                                        val refreshedUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
                                                        isVerifiedState = refreshedUser?.isEmailVerified == true
                                                        statusMessage = if (refreshedUser?.isEmailVerified == true) {
                                                            if (isEn) "Account verified successfully! 🎉" else "تم تفعيل الحساب بنجاح! 🎉"
                                                        } else {
                                                            if (isEn) "Email not verified yet. Check inbox." else "لم يتم التفعيل بعد. افتح صندوق البريد."
                                                        }
                                                    }
                                                }
                                            },
                                            modifier = Modifier.weight(1f),
                                            borderColor = NeonCyan,
                                            testTag = "check_verification_status_btn"
                                        ) {
                                            Text(
                                                text = if (isReloadingUser) {
                                                    if (isEn) "Checking..." else "جاري التحقق..."
                                                } else {
                                                    if (isEn) "Check Status" else "التحقق من الحالة"
                                                },
                                                color = NeonCyan,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        GlassButton(
                            onClick = { showLogoutConfirmDialog = true },
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = StatusError,
                            testTag = "logout_btn"
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Logout,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.logoutButtonLabel,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // Not logged in message
                        Text(
                            text = strings.notLoggedInTitle,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = strings.notLoggedInSubtitle,
                            color = TextMuted,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        GlassButton(
                            onClick = { showAuth = true },
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = NeonCyan,
                            testTag = "login_modal_btn"
                        ) {
                            Text(
                                text = strings.loginButtonLabel,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(onClick = onDismiss) {
                        Text(text = strings.closeBtn, color = TextMuted, fontSize = 13.sp)
                    }
                }
            }
        }
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text(if (isEn) "Confirm Sign Out" else "تأكيد تسجيل الخروج", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text(if (isEn) "Are you sure you want to sign out of your account?" else "هل أنت تأكد من تسجيل الخروج من حسابك؟", color = TextSecondary) },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutConfirmDialog = false
                        FirebaseAuth.getInstance().signOut()
                        onLogout()
                        onDismiss()
                    }
                ) {
                    Text(if (isEn) "Sign Out" else "تسجيل الخروج", color = StatusError, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text(strings.cancelBtn, color = TextMuted)
                }
            },
            containerColor = SurfaceElevated
        )
    }
}

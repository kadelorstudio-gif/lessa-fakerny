package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.*
import com.example.util.AppStrings
import com.google.firebase.auth.FirebaseAuth

@Composable
fun UserProfileScreen(
    onOpenAuthDialog: () -> Unit,
    strings: AppStrings,
    language: String
) {
    val context = LocalContext.current
    val isEn = language.equals("en", ignoreCase = true)
    
    // Track auth user state
    var currentUser by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    // Listen to auth state changes or refresh
    DisposableEffect(Unit) {
        val authListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            currentUser = firebaseAuth.currentUser
        }
        FirebaseAuth.getInstance().addAuthStateListener(authListener)
        onDispose {
            FirebaseAuth.getInstance().removeAuthStateListener(authListener)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("user_profile_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Screen Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = strings.profileHeaderTitle,
                        color = NeonCyan,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = strings.profileHeaderSubtitle,
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (currentUser == null) {
                // Not Logged In State
                Spacer(modifier = Modifier.height(40.dp))
                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    tintColor = NeonViolet,
                    shape = RoundedCornerShape(24.dp),
                    useDualNeonBorder = true,
                    testTag = "not_logged_in_card"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)
                                .background(NeonViolet.copy(alpha = 0.2f))
                                .border(BorderStroke(1.5.dp, NeonViolet.copy(alpha = 0.5f)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = NeonViolet,
                                modifier = Modifier.size(42.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = strings.notLoggedInTitle,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = strings.notLoggedInSubtitle,
                            color = SoftLavender,
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        GlassButton(
                            onClick = onOpenAuthDialog,
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = NeonCyan,
                            testTag = "profile_login_btn"
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CloudDone, contentDescription = null, tint = Color(0xFF050816))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(strings.loginButtonLabel, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            } else {
                // Logged In User Profile Card
                val user = currentUser!!
                val userEmail = user.email ?: (if (isEn) "No Email" else "بدون بريد")
                val displayName = user.displayName ?: userEmail.substringBefore("@")
                val userId = user.uid
                val isVerified = user.isEmailVerified

                GlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    tintColor = NeonCyan,
                    shape = RoundedCornerShape(24.dp),
                    useDualNeonBorder = true,
                    testTag = "profile_info_card"
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Avatar
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(NeonCyan.copy(alpha = 0.22f))
                                .border(BorderStroke(2.dp, NeonCyan), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = displayName.take(2).uppercase(),
                                color = NeonCyan,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = displayName,
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = strings.onlineModeDesc,
                                color = StatusSuccess,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        HorizontalDivider(color = GlassBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Details Rows
                        ProfileDetailItem(
                            icon = Icons.Default.Email,
                            label = strings.emailAddressLabel,
                            value = userEmail,
                            accentColor = NeonCyan
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileDetailItem(
                            icon = Icons.Default.Fingerprint,
                            label = strings.userIdLabel,
                            value = userId,
                            accentColor = NeonViolet
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        ProfileDetailItem(
                            icon = if (isVerified) Icons.Default.CheckCircle else Icons.Default.ErrorOutline,
                            label = strings.emailVerifiedLabel,
                            value = if (isVerified) strings.verifiedBadge else strings.notVerifiedBadge,
                            valueColor = if (isVerified) StatusSuccess else StatusWarning,
                            accentColor = if (isVerified) StatusSuccess else StatusWarning
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Logout Button
                        GlassButton(
                            onClick = {
                                FirebaseAuth.getInstance().signOut()
                                currentUser = null
                                Toast.makeText(
                                    context,
                                    if (isEn) "Signed out successfully" else "تم تسجيل الخروج بنجاح",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            accentColor = StatusError,
                            testTag = "profile_logout_btn"
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = strings.logoutButtonLabel,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    accentColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceElevated)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(accentColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = valueColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }
    }
}

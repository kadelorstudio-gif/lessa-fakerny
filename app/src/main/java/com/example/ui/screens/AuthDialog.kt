package com.example.ui.screens

import android.app.Activity
import android.util.Log
import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import com.example.R
import com.example.ui.components.GlassButton
import com.example.ui.components.GlassCard
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonViolet
import com.example.ui.theme.SoftLavender
import com.example.ui.theme.StatusError
import com.example.ui.theme.SurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.AppStrings
import com.example.util.LocaleManager
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun AuthDialog(
    onDismiss: () -> Unit,
    onAuthSuccess: () -> Unit,
    strings: AppStrings = LocaleManager.ARABIC,
    language: String = "ar"
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val isEn = language.equals("en", ignoreCase = true)

    var isRegistering by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }
    var isGoogleLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val credentialManager = remember { CredentialManager.create(context) }

    fun translateError(e: Throwable?): String {
        return when (e) {
            is FirebaseAuthInvalidCredentialsException -> {
                if (isEn) "Invalid credentials or password format." else "بيانات الدخول غير صحيحة، تأكد من البريد وكلمة المرور."
            }
            is FirebaseAuthInvalidUserException -> {
                if (isEn) "No user found with this email." else "لا يوجد حساب مسجل بهذا البريد الإلكتروني."
            }
            is FirebaseAuthUserCollisionException -> {
                if (isEn) "This email is already in use by another account." else "هذا البريد الإلكتروني مستخدم بالفعل في حساب آخر."
            }
            is FirebaseAuthWeakPasswordException -> {
                if (isEn) "Password is too weak. Must be at least 6 characters." else "كلمة المرور ضعيفة جداً، يجب أن تتكون من 6 خانات على الأقل."
            }
            is FirebaseNetworkException -> {
                if (isEn) "Network error. Please check your internet connection." else "تعذر الاتصال بالسحابة، يرجى التحقق من اتصال الإنترنت."
            }
            else -> {
                val msg = e?.localizedMessage
                if (msg != null && msg.isNotBlank()) {
                    msg
                } else {
                    if (isEn) "Authentication failed. Please try again." else "فشل تسجيل الدخول، يرجى المحاولة مرة أخرى."
                }
            }
        }
    }

    Dialog(onDismissRequest = {
        if (!isLoading && !isGoogleLoading) onDismiss()
    }) {
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("auth_dialog"),
            shape = RoundedCornerShape(26.dp),
            tintColor = NeonCyan,
            useDualNeonBorder = true
        ) {
            Column(
                modifier = Modifier
                    .padding(22.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonCyan.copy(alpha = 0.2f), NeonViolet.copy(alpha = 0.25f))
                            )
                        )
                        .border(BorderStroke(1.5.dp, NeonCyan.copy(alpha = 0.7f)), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = if (isRegistering) strings.authDialogRegisterTitle else strings.authDialogLoginTitle,
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = strings.authDialogSubtitle,
                    color = SoftLavender,
                    fontSize = 12.sp,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // 1. Google Sign-In Button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    Color(0xFF1E293B),
                                    Color(0xFF0F172A)
                                )
                            )
                        )
                        .border(
                            BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(NeonCyan, NeonBlue))),
                            RoundedCornerShape(14.dp)
                        )
                        .clickable(enabled = !isLoading && !isGoogleLoading) {
                            coroutineScope.launch {
                                isGoogleLoading = true
                                errorMessage = ""
                                try {
                                    val webClientId = run {
                                        val resId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                                            .takeIf { it != 0 }
                                            ?: context.resources.getIdentifier("default_web_client_id", "string", "app.lessafakerny.android")
                                        if (resId != 0) {
                                            context.getString(resId)
                                        } else {
                                            "247548670509-7flpquvd9l60pc1jvrcjpmgemadbba1h.apps.googleusercontent.com"
                                        }
                                    }

                                    val googleIdOption = GetSignInWithGoogleOption.Builder(webClientId)
                                        .build()

                                    val request = GetCredentialRequest.Builder()
                                        .addCredentialOption(googleIdOption)
                                        .build()

                                    val result = credentialManager.getCredential(context, request)
                                    val credential = result.credential

                                    if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                        val firebaseCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)
                                        auth.signInWithCredential(firebaseCredential).await()
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = if (isEn) "Unknown credential received" else "نوع تسجيل الدخول غير مدعوم"
                                    }
                                } catch (e: GetCredentialCancellationException) {
                                    Log.d("AuthDialog", "Google Sign-In dismissed by user")
                                } catch (e: Exception) {
                                    Log.e("AuthDialog", "Google Sign-In failed", e)
                                    errorMessage = translateError(e)
                                } finally {
                                    isGoogleLoading = false
                                }
                            }
                        }
                        .testTag("google_signin_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    if (isGoogleLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = NeonCyan,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isEn) "Connecting with Google..." else "جاري الاتصال بـ Google...",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            // Vibrant "G" badge
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "G",
                                    color = Color(0xFF4285F4),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = strings.googleSignInButtonLabel,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Divider with text
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = GlassBorder)
                    Text(
                        text = strings.orDividerLabel,
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    HorizontalDivider(modifier = Modifier.weight(1f), color = GlassBorder)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mode Tabs (Login vs Register)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceElevated)
                        .padding(3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (!isRegistering) NeonCyan.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable {
                                isRegistering = false
                                errorMessage = ""
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.loginActionLabel,
                            color = if (!isRegistering) NeonCyan else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isRegistering) NeonViolet.copy(alpha = 0.25f) else Color.Transparent)
                            .clickable {
                                isRegistering = true
                                errorMessage = ""
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = strings.registerActionLabel,
                            color = if (isRegistering) NeonViolet else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Email Field
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        errorMessage = ""
                    },
                    label = { Text(strings.emailLabel) },
                    placeholder = { Text("student@university.edu", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Email, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonCyan,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = NeonCyan,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Password Field
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        errorMessage = ""
                    },
                    label = { Text(strings.passwordLabel) },
                    placeholder = { Text("••••••••", color = TextMuted) },
                    leadingIcon = {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = NeonViolet, modifier = Modifier.size(18.dp))
                    },
                    trailingIcon = {
                        IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    },
                    visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = NeonViolet,
                        unfocusedBorderColor = GlassBorder,
                        focusedLabelColor = NeonViolet,
                        unfocusedLabelColor = TextSecondary,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard
                    )
                )

                // Error message banner
                AnimatedVisibility(
                    visible = errorMessage.isNotBlank(),
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StatusError.copy(alpha = 0.12f))
                            .border(BorderStroke(1.dp, StatusError.copy(alpha = 0.4f)), RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = StatusError,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit Button
                GlassButton(
                    onClick = {
                        val trimmedEmail = email.trim()
                        if (!Patterns.EMAIL_ADDRESS.matcher(trimmedEmail).matches()) {
                            errorMessage = if (isEn) "Please enter a valid email address" else "الرجاء إدخال بريد إلكتروني صحيح"
                            return@GlassButton
                        }
                        if (password.length < 6) {
                            errorMessage = if (isEn) "Password must be at least 6 characters" else "كلمة المرور يجب أن لا تقل عن 6 خانات"
                            return@GlassButton
                        }

                        isLoading = true
                        errorMessage = ""

                        if (isRegistering) {
                            auth.createUserWithEmailAndPassword(trimmedEmail, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = translateError(task.exception)
                                    }
                                }
                        } else {
                            auth.signInWithEmailAndPassword(trimmedEmail, password)
                                .addOnCompleteListener { task ->
                                    isLoading = false
                                    if (task.isSuccessful) {
                                        onAuthSuccess()
                                    } else {
                                        errorMessage = translateError(task.exception)
                                    }
                                }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading && !isGoogleLoading,
                    testTag = "auth_submit_btn"
                ) {
                    if (isLoading) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color(0xFF050816),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Processing..." else "جاري المتابعة...",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Text(
                            text = if (isRegistering) strings.registerActionLabel else strings.loginActionLabel,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Dismiss / Close
                TextButton(
                    onClick = onDismiss,
                    enabled = !isLoading && !isGoogleLoading
                ) {
                    Text(strings.cancelBtn, color = TextMuted, fontSize = 13.sp)
                }
            }
        }
    }
}

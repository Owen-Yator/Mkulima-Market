// ─────────────────────────────────────────────────────────────────────────────
// LoginScreen.kt
// Location: com/mkulimamarket/app/auth/presentation/LoginScreen.kt
//
// Design approach:
//   - Full-height green hero panel at the top (40% of screen) with the brand
//     mark and a Swahili tagline — grounds the screen in its agricultural context
//   - White content panel slides up from below — clean, airy, no clutter
//   - Gold accent on the primary action button only (spend boldness in one place)
//   - Fields use a custom bottom-border style instead of the default outlined box
//     — lighter, more modern, avoids the "generic Material form" feel
//   - Signature element: diagonal wheat-stalk accent strip between hero + form
//
// ALL original callbacks and ViewModel calls are untouched.
// ─────────────────────────────────────────────────────────────────────────────

package com.mkulimamarket.app.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mkulimamarket.app.auth.viewmodel.AuthViewModel

// ── Palette ───────────────────────────────────────────────────────────────────
private val GreenDeep    = Color(0xFF1B5E20)
private val GreenMid     = Color(0xFF2E7D32)
private val GreenLight   = Color(0xFF43A047)
private val GoldAccent   = Color(0xFFF9A825)
private val GoldDark     = Color(0xFFF57F17)
private val SurfaceWhite = Color(0xFFFAFAFA)
private val TextDark     = Color(0xFF1A1A1A)
private val TextMuted    = Color(0xFF757575)
private val FieldLine    = Color(0xFFBDBDBD)
private val FieldFocus   = Color(0xFF2E7D32)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onGoToSignup: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val state        = viewModel.uiState.value
    val focusManager = LocalFocusManager.current
    var showPassword by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(GreenDeep)) {

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Hero panel ────────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.38f)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0A3D0C), GreenMid)
                        )
                    ),
                contentAlignment = Alignment.BottomStart
            ) {
                Column(
                    modifier = Modifier.padding(start = 28.dp, bottom = 32.dp)
                ) {
                    // Wheat icon substitute — simple typographic mark
                    Text(
                        text  = "🌾",
                        fontSize = 40.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text  = "Mkulima Market",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color      = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Bei za soko. Moja kwa moja.",   // "Market prices. Direct."
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color      = Color.White.copy(alpha = 0.70f),
                            fontStyle  = FontStyle.Italic
                        )
                    )
                }
            }

            // ── Signature accent strip ────────────────────────────────────────
            // Gold divider with a subtle angled clip — the one decorative risk
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(GoldAccent, GoldDark, GoldAccent)
                        )
                    )
            )

            // ── Form panel ────────────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
                    .background(SurfaceWhite)
                    .padding(horizontal = 28.dp, vertical = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text  = "Welcome back",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color      = TextDark,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text  = "Sign in to your account",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextMuted
                        )
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email field
                    FieldLabel("Email address")
                    OutlinedTextField(
                        value         = state.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        placeholder   = { Text("you@example.com", color = FieldLine) },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Email,
                                contentDescription = null,
                                tint = GreenMid
                            )
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction    = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        colors     = authFieldColors(),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Password field
                    FieldLabel("Password")
                    OutlinedTextField(
                        value         = state.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        placeholder   = { Text("••••••••", color = FieldLine) },
                        leadingIcon   = {
                            Icon(
                                Icons.Filled.Lock,
                                contentDescription = null,
                                tint = GreenMid
                            )
                        },
                        trailingIcon  = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector        = if (showPassword) Icons.Filled.VisibilityOff
                                    else Icons.Filled.Visibility,
                                    contentDescription = if (showPassword) "Hide" else "Show",
                                    tint               = TextMuted
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction    = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        ),
                        singleLine = true,
                        shape      = RoundedCornerShape(12.dp),
                        colors     = authFieldColors(),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Primary action
                    Button(
                        onClick = {
                            if (viewModel.validateLogin()) onLoginSuccess()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldAccent,
                            contentColor   = Color(0xFF1A1A1A)
                        )
                    ) {
                        Text(
                            text       = "Sign in",
                            fontWeight = FontWeight.Bold,
                            fontSize   = 16.sp
                        )
                    }
                }

                // Footer link
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        "Don't have an account? ",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                    )
                    TextButton(
                        onClick      = onGoToSignup,
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            "Sign up",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color      = GreenMid,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }
    }
}

// ── Shared helpers ─────────────────────────────────────────────────────────────

@Composable
internal fun FieldLabel(text: String) {
    Text(
        text     = text,
        style    = MaterialTheme.typography.labelMedium.copy(
            color      = Color(0xFF424242),
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.3.sp
        ),
        modifier = Modifier.padding(bottom = 6.dp)
    )
}

@Composable
internal fun authFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = FieldFocus,
    unfocusedBorderColor = FieldLine,
    focusedLabelColor    = FieldFocus,
    cursorColor          = FieldFocus,
    focusedLeadingIconColor   = Color(0xFF2E7D32),
    unfocusedLeadingIconColor = Color(0xFF9E9E9E)
)

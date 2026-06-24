package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryBlue

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onLoginClick: (String, () -> Unit, (String) -> Unit) -> Unit,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateFlowOf("") }
    var password by remember { mutableStateFlowOf("") }
    var errorMessage by remember { mutableStateFlowOf<String?>(null) }
    var isSigningUp by remember { mutableStateFlowOf(false) }
    var loading by remember { mutableStateFlowOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(PrimaryBlue.copy(alpha = 0.05f), Color.White)
                )
            )
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Icon Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "EduSync Logo",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(44.dp)
                )
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "EduSync",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PrimaryBlue,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Offline Education for Rural India",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main input card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (isSigningUp) "Create Account" else "Welcome Back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    errorMessage?.let {
                        Text(
                            text = it,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            errorMessage = null
                        },
                        label = { Text("Email Address") },
                        placeholder = { Text("student@edusync.in") },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = "Email") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("email_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            errorMessage = null
                        },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = "Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Button(
                        onClick = {
                            loading = true
                            onLoginClick(
                                email,
                                {
                                    loading = false
                                    onLoginSuccess(email)
                                },
                                { err ->
                                    loading = false
                                    errorMessage = err
                                }
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("login_button")
                    ) {
                        if (loading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = if (isSigningUp) "Sign Up" else "Login",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isSigningUp) "Already have an account?" else "New student?",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                        TextButton(
                            onClick = {
                                isSigningUp = !isSigningUp
                                errorMessage = null
                            },
                            modifier = Modifier.testTag("toggle_signup_login")
                        ) {
                            Text(
                                text = if (isSigningUp) "Login" else "Sign Up",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentOrange
                            )
                        }
                    }
                }
            }

            // Quick bypass demo button
            Button(
                onClick = {
                    loading = true
                    onLoginClick(
                        "demo.student@edusync.org",
                        {
                            loading = false
                            onLoginSuccess("demo.student@edusync.org")
                        },
                        { loading = false }
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentOrange.copy(alpha = 0.15f),
                    contentColor = AccentOrange
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("demo_bypass_button")
            ) {
                Icon(Icons.Default.Star, contentDescription = "Bypass Demo")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Launch Demo Mode (Instant Login)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "✓ Offline-First • Saved Local to Room • Auto-Syncing",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }
    }
}

// Helper to bridge state in compose easily
private fun <T> mutableStateFlowOf(initialValue: T): MutableState<T> = mutableStateOf(initialValue)

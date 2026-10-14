package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun AuthScreen(
    authError: String?,
    isLoading: Boolean,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String) -> Unit,
    onQuickDemo: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Student Login, 1: Register, 2: Admin Login

    // Fields
    var loginEmail by remember { mutableStateOf("student@mpsi.com") }
    var loginPassword by remember { mutableStateOf("student123") }

    var regName by remember { mutableStateOf("") }
    var regMobile by remember { mutableStateOf("") }
    var regEmail by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }

    var adminEmail by remember { mutableStateOf("admin@mpsi.com") }
    var adminPassword by remember { mutableStateOf("admin123") }

    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // Badge Emblem Header
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(NavyPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = GoldBright,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "MPSI Portal Access",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = NavyPrimary
        )
        Text(
            text = "Madhya Pradesh Police Sub-Inspector Test System",
            fontSize = 12.sp,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Tab Selector
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFFF1F5F9),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = if (selectedTab == 2) Color(0xFFDC2626) else NavyPrimary
                )
            },
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxWidth()
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Login", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = NavyPrimary,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Register", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = NavyPrimary,
                unselectedContentColor = TextMuted
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Admin", fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                selectedContentColor = Color(0xFFDC2626),
                unselectedContentColor = TextMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Error message card
        if (authError != null) {
            Surface(
                color = Color(0xFFFEE2E2),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = authError,
                    color = RedError,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (selectedTab) {
                    0 -> {
                        // Student Login
                        Text(
                            text = "Student Sign In",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = loginEmail,
                            onValueChange = { loginEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onLogin(loginEmail, loginPassword) },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Sign In as Student", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    1 -> {
                        // Student Register
                        Text(
                            text = "Create Student Account",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = regName,
                            onValueChange = { regName = it },
                            label = { Text("Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_name_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regMobile,
                            onValueChange = { regMobile = it },
                            label = { Text("Mobile Number (10 digits)") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_mobile_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("reg_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NavyPrimary)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onRegister(regName, regMobile, regEmail, regPassword) },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("reg_submit_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Register & Start MPSI Prep", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }

                    2 -> {
                        // Admin Login
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color(0xFFDC2626))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Admin Portal Login",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFDC2626)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = adminEmail,
                            onValueChange = { adminEmail = it },
                            label = { Text("Admin Email") },
                            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_email_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFDC2626))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = adminPassword,
                            onValueChange = { adminPassword = it },
                            label = { Text("Admin Secret Password") },
                            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("admin_password_input"),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFDC2626))
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { onLogin(adminEmail, adminPassword) },
                            enabled = !isLoading,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("admin_submit_btn")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                            } else {
                                Text("Access Admin Console", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo Quick Credentials Helper
        Surface(
            color = Color(0xFFF8FAFC),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "One-Tap Instant Test Credentials",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onQuickDemo("STUDENT") },
                        colors = ButtonDefaults.buttonColors(containerColor = NavyLight),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fill Demo Student", fontSize = 11.sp)
                    }
                    Button(
                        onClick = { onQuickDemo("ADMIN") },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF991B1B)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Fill Demo Admin", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntity
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary

@Composable
fun TopNavBar(
    title: String = "MPSI TEST SERIES",
    subtitle: String? = "MP Police SI Preparation",
    currentUser: UserEntity?,
    canNavigateBack: Boolean = false,
    onBackClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    onAuthClick: () -> Unit = {}
) {
    Surface(
        color = NavyPrimary,
        tonalElevation = 4.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (canNavigateBack) {
                        IconButton(
                            onClick = onBackClick,
                            modifier = Modifier.testTag("nav_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Go back",
                                tint = Color.White
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(NavyDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "MPSI Police Badge",
                                tint = GoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        if (subtitle != null) {
                            Text(
                                text = subtitle,
                                color = GoldBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // User Actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (currentUser != null) {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (currentUser.role == "ADMIN") Color(0xFFDC2626) else NavyDark,
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (currentUser.role == "ADMIN") Icons.Default.AdminPanelSettings else Icons.Default.Person,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (currentUser.role == "ADMIN") "ADMIN" else currentUser.fullName.split(" ").firstOrNull() ?: "Student",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(
                            onClick = onLogoutClick,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = Color(0xFFF87171),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    } else {
                        IconButton(
                            onClick = onAuthClick,
                            modifier = Modifier.testTag("login_nav_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Login",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

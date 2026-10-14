package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldBright
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.theme.RedLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun AccessDeniedScreen(
    reason: String,
    canPurchase: Boolean,
    seriesId: String?,
    onPurchaseClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(24.dp)
            .testTag("access_denied_screen"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(RedLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Access Denied",
                tint = RedError,
                modifier = Modifier.size(38.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Access Denied",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RedError
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Secure Test Authorization Policy",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = NavyPrimary
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = reason,
                    fontSize = 14.sp,
                    color = TextDark,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Questions remain encrypted and protected on the server.",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (canPurchase && seriesId != null) {
            Button(
                onClick = { onPurchaseClick(seriesId) },
                colors = ButtonDefaults.buttonColors(containerColor = GoldBright, contentColor = NavyDark),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("access_denied_enroll_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Buy Now • Unlock Series", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        OutlinedButton(
            onClick = onBackClick,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text("Go Back to Dashboard", color = TextDark)
        }
    }
}

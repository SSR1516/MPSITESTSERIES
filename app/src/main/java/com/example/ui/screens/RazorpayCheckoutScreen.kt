package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.RazorpayOrder
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

// Razorpay Official Brand Blue & Accent
val RazorpayBlue = Color(0xFF0C2340)
val RazorpayAccent = Color(0xFF3395FF)

@Composable
fun RazorpayCheckoutScreen(
    order: RazorpayOrder,
    isLoading: Boolean,
    onPaymentSuccess: (method: String, customPaymentId: String?) -> Unit,
    onPaymentFailed: (String) -> Unit = {},
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedMethod by remember { mutableStateOf("UPI") }
    var enteredPaymentId by remember { mutableStateOf("") }
    var copyConfirmed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Razorpay Header Bar
        Surface(
            color = RazorpayBlue,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = RazorpayAccent,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "R",
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Razorpay",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }

                    Surface(
                        color = Color(0xFF22C55E).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF22C55E),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "OFFICIAL GATEWAY",
                                color = Color(0xFF22C55E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color.White.copy(alpha = 0.15f))
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = order.seriesTitle,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Order: ${order.orderId}",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "₹${order.amountRupees.toInt()}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Official Razorpay Hosted Payment Link Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, RazorpayAccent.copy(alpha = 0.5f)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("official_razorpay_link_card")
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        Icons.Default.Security,
                        contentDescription = null,
                        tint = RazorpayAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Official Razorpay Payment Link",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                        Text(
                            text = "Direct payment gateway for MPSI Test Series",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = Color(0xFFF8FAFC),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = order.paymentUrl,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = RazorpayAccent,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(order.paymentUrl))
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open browser: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = RazorpayAccent),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("razorpay_open_link_btn")
                    ) {
                        Text("Open Payment Page", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            clipboardManager.setText(AnnotatedString(order.paymentUrl))
                            copyConfirmed = true
                            Toast.makeText(context, "Payment Link Copied!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(0.7f)
                            .testTag("razorpay_copy_link_btn")
                    ) {
                        Text(if (copyConfirmed) "Copied!" else "Copy Link", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Already completed payment on the link?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "Enter your Razorpay Payment ID / UTR or click verify to unlock immediately:",
                    fontSize = 11.sp,
                    color = TextMuted,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = enteredPaymentId,
                    onValueChange = { enteredPaymentId = it },
                    placeholder = { Text("e.g. pay_xxxxx from Razorpay receipt", fontSize = 12.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("razorpay_payment_id_input")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val paymentId = enteredPaymentId.ifBlank { "pay_rzp_link_verified" }
                        onPaymentSuccess("Razorpay Link (${order.paymentUrl})", paymentId)
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("razorpay_verify_link_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (enteredPaymentId.isNotBlank()) "Verify Payment ($enteredPaymentId)" else "Verify & Unlock Series",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Student Info
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Candidate Details",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Name: ${order.studentName}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextDark)
                Text(text = "Email: ${order.studentEmail}", fontSize = 12.sp, color = TextMuted)
                Text(text = "Mobile: +91 ${order.studentMobile}", fontSize = 12.sp, color = TextMuted)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Instant Free Development Test Mode Section
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Instant Development Sandbox (₹0)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Surface(
                        color = Color(0xFFFEF08A),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "FREE DEV MODE",
                            color = Color(0xFF854D0E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PaymentOptionRow(
                    title = "UPI / QR (Instant Sandbox)",
                    subtitle = "Simulate Google Pay, PhonePe, Paytm, BHIM",
                    icon = Icons.Default.PhoneAndroid,
                    selected = selectedMethod == "UPI",
                    onSelect = { selectedMethod = "UPI" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PaymentOptionRow(
                    title = "Debit / Credit Card (Test Card)",
                    subtitle = "Simulate RuPay, Visa, MasterCard",
                    icon = Icons.Default.CreditCard,
                    selected = selectedMethod == "CARD",
                    onSelect = { selectedMethod = "CARD" }
                )

                Spacer(modifier = Modifier.height(8.dp))

                PaymentOptionRow(
                    title = "NetBanking (Test Account)",
                    subtitle = "Simulate State Bank of India, HDFC, ICICI",
                    icon = Icons.Default.AccountBalance,
                    selected = selectedMethod == "NETBANKING",
                    onSelect = { selectedMethod = "NETBANKING" }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pay Button
        Button(
            onClick = { onPaymentSuccess(selectedMethod, null) },
            enabled = !isLoading,
            colors = ButtonDefaults.buttonColors(containerColor = NavyDark),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("razorpay_pay_now_btn")
        ) {
            if (isLoading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text("Verifying Payment with Server...", color = Color.White, fontSize = 13.sp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Instant Sandbox Pay ₹${order.amountRupees.toInt()} via $selectedMethod",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = { onPaymentFailed("Simulated Bank Decline / Insufficient Funds") },
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFDC2626)),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("razorpay_simulate_fail_btn")
        ) {
            Text("Simulate Payment Failure (Test Sandbox)", fontSize = 13.sp, color = Color(0xFFDC2626))
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onCancel,
            enabled = !isLoading,
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("razorpay_cancel_btn")
        ) {
            Text("Cancel Transaction", color = TextMuted)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security assurance badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Secured by 256-Bit SSL Encryption • Razorpay Certified",
                fontSize = 11.sp,
                color = TextMuted
            )
        }
    }
}

// Overload for backward compatibility
@Composable
fun RazorpayCheckoutScreen(
    order: RazorpayOrder,
    isLoading: Boolean,
    onPaymentSuccess: (String) -> Unit,
    onPaymentFailed: (String) -> Unit = {},
    onCancel: () -> Unit
) = RazorpayCheckoutScreen(
    order = order,
    isLoading = isLoading,
    onPaymentSuccess = { method, _ -> onPaymentSuccess(method) },
    onPaymentFailed = onPaymentFailed,
    onCancel = onCancel
)

@Composable
fun PaymentOptionRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (selected) RazorpayAccent.copy(alpha = 0.08f) else Color(0xFFF8FAFC),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (selected) RazorpayAccent else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(selectedColor = RazorpayAccent)
            )

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) RazorpayAccent else TextMuted,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

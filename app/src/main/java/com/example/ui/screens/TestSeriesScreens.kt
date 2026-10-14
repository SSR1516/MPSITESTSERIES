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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EnrollmentEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun TestSeriesListScreen(
    seriesList: List<TestSeriesEntity>,
    enrollments: List<EnrollmentEntity>,
    onSeriesClick: (String) -> Unit,
    onPurchaseClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("series_list_lazy_column")
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "MPSI Mock Test Series",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Targeted test packs matching the latest MP Police SI pattern & syllabus.",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )
        }

        items(seriesList) { series ->
            val isEnrolled = enrollments.any { it.seriesId == series.id }

            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .clickable { onSeriesClick(series.id) }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                color = NavyPrimary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = series.exam,
                                    color = NavyPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = series.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "₹${series.price.toInt()}",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoldAccent
                            )
                            Text(
                                text = "₹${series.originalPrice.toInt()}",
                                fontSize = 12.sp,
                                color = TextMuted,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = series.description,
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Features spec grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SpecChip("${series.numberOfTests} Mock Tests")
                        SpecChip("${series.questionsPerTest} Qs / Test")
                        SpecChip("${series.durationMinutes} Minutes")
                        SpecChip("-${series.negativeMarking} Neg Mark")
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Validity: ${series.validityDays} Days from enrollment date",
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onSeriesClick(series.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = TextDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("View Tests", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        if (isEnrolled) {
                            Button(
                                onClick = { onSeriesClick(series.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Enrolled", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Button(
                                onClick = { onPurchaseClick(series.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldBright, contentColor = NavyDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("buy_series_${series.id}")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Buy Now • ₹${series.price.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SpecChip(label: String) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = TextDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun SeriesDetailScreen(
    series: TestSeriesEntity,
    tests: List<TestEntity>,
    isEnrolled: Boolean,
    onPurchaseClick: () -> Unit,
    onStartTest: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("series_detail_lazy_column")
            .padding(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = series.exam,
                        color = GoldBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = series.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = series.description,
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Price: ₹${series.price.toInt()}",
                                color = GoldBright,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Validity: ${series.validityDays} Days",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }

                        if (!isEnrolled) {
                            Button(
                                onClick = onPurchaseClick,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldBright, contentColor = NavyDark),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("detail_buy_now_btn")
                            ) {
                                Text("Buy Now • ₹${series.price.toInt()}", fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = GreenSuccess.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = GreenSuccess, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Active Enrollment", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (!isEnrolled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color.White.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = GoldBright, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Razorpay Gateway Link: rzp.io/rzp/gQC02wc (₹100)",
                                    color = Color(0xFFE2E8F0),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Included Tests (${tests.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(tests) { test ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = test.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${test.durationMinutes} mins • ${test.totalMarks} marks • -${test.negativeMarks} negative",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                        Text(
                            text = "Max attempts: ${test.maxAttempts}",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    if (isEnrolled) {
                        Button(
                            onClick = { onStartTest(test.id) },
                            colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("start_test_btn_${test.id}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Attempt", fontSize = 12.sp)
                            }
                        }
                    } else {
                        Button(
                            onClick = onPurchaseClick,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0), contentColor = TextMuted),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Unlock", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

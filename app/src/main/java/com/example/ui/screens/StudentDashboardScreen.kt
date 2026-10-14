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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyLight
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun StudentDashboardScreen(
    user: UserEntity,
    enrollments: List<EnrollmentEntity>,
    publishedSeries: List<TestSeriesEntity>,
    allTests: List<TestEntity>,
    attempts: List<AttemptEntity>,
    onExploreSeries: () -> Unit,
    onStartTest: (String) -> Unit,
    onViewResultsHistory: () -> Unit,
    onViewAnalytics: () -> Unit,
    onViewResultDetail: (String) -> Unit
) {
    // Computed Metrics
    val mySeriesCount = enrollments.size
    val completedTestsCount = attempts.size
    val averageScore = if (attempts.isNotEmpty()) {
        attempts.map { it.obtainedMarks }.average()
    } else 0.0
    val bestScore = attempts.maxOfOrNull { it.obtainedMarks } ?: 0.0
    val averageAccuracy = if (attempts.isNotEmpty()) {
        attempts.map { it.accuracy }.average()
    } else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("student_dashboard_lazy_column")
            .padding(horizontal = 16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(14.dp))

            // Welcome Student Banner
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = GoldBright.copy(alpha = 0.2f),
                                modifier = Modifier.padding(bottom = 6.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MilitaryTech,
                                        contentDescription = null,
                                        tint = GoldBright,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "MPSI Sub-Inspector Cadet",
                                        color = GoldBright,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(
                                text = "Welcome, ${user.fullName}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "MP Police Exam Preparation in Progress",
                                fontSize = 12.sp,
                                color = Color(0xFFCBD5E1)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(NavyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = GoldBright,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onExploreSeries,
                            colors = ButtonDefaults.buttonColors(containerColor = GoldBright, contentColor = NavyDark),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("All Series", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = onViewAnalytics,
                            colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Analytics", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Metrics Grid (Cards)
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "My Exam Telemetry",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(10.dp))

            val metrics = listOf(
                MetricItem("My Test Series", "$mySeriesCount Active", Icons.Default.ShoppingBag, BlueBright),
                MetricItem("Completed Tests", "$completedTestsCount Attempted", Icons.Default.Assignment, NavyPrimary),
                MetricItem("Average Score", String.format("%.1f", averageScore) + " pts", Icons.Default.TrendingUp, Color(0xFF0284C7)),
                MetricItem("Best Score", String.format("%.1f", bestScore) + " pts", Icons.Default.EmojiEvents, GoldAccent),
                MetricItem("Accuracy", String.format("%.1f", averageAccuracy) + "%", Icons.Default.Speed, GreenSuccess),
                MetricItem("Total Tests Available", "${allTests.size} Tests", Icons.Default.CheckCircle, NavyLight)
            )

            metrics.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { m ->
                        DashboardMetricCard(
                            metric = m,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Quick Ready Mock Tests to Attempt
        item {
            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "High-Yield Mock Tests",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    color = BlueBright,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onExploreSeries() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }

        // Tests Cards
        items(allTests.take(3)) { test ->
            val isEnrolled = enrollments.any { it.seriesId == test.seriesId }

            Card(
                shape = RoundedCornerShape(14.dp),
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isEnrolled) GreenSuccess.copy(alpha = 0.15f) else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = if (isEnrolled) "UNLOCKED" else "LOCKED",
                                    color = if (isEnrolled) GreenSuccess else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${test.durationMinutes} Mins • ${test.totalMarks} Marks",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = test.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextDark
                        )

                        Text(
                            text = "-${test.negativeMarks} Neg Marking • Max ${test.maxAttempts} Attempts",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = { onStartTest(test.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isEnrolled) GreenSuccess else NavyPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("start_test_${test.id}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEnrolled) "Start Test" else "Access",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Recent Attempts Section
        if (attempts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Test Attempts",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    Text(
                        text = "History (${attempts.size})",
                        fontSize = 12.sp,
                        color = BlueBright,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onViewResultsHistory() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }

            items(attempts.take(3)) { att ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable { onViewResultDetail(att.id) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = att.testTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextDark
                            )
                            Text(
                                text = "Correct: ${att.correct} | Wrong: ${att.wrong} | Accuracy: ${att.accuracy.toInt()}%",
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${att.obtainedMarks} / ${att.totalMarks.toInt()} pts",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = GoldAccent
                            )
                            Text(
                                text = "View Scorecard",
                                fontSize = 11.sp,
                                color = BlueBright,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

data class MetricItem(
    val title: String,
    val value: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun DashboardMetricCard(metric: MetricItem, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = metric.title,
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(metric.color.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = metric.icon,
                        contentDescription = null,
                        tint = metric.color,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = metric.value,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )
        }
    }
}

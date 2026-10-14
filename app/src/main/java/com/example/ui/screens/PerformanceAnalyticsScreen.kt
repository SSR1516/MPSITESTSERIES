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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.model.AttemptEntity
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted

@Composable
fun PerformanceAnalyticsScreen(
    attempts: List<AttemptEntity>
) {
    val totalAttempts = attempts.size
    val avgScore = if (attempts.isNotEmpty()) attempts.map { it.obtainedMarks }.average() else 0.0
    val bestScore = attempts.maxOfOrNull { it.obtainedMarks } ?: 0.0
    val lowestScore = attempts.minOfOrNull { it.obtainedMarks } ?: 0.0
    val avgAccuracy = if (attempts.isNotEmpty()) attempts.map { it.accuracy }.average() else 0.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("analytics_screen")
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Performance Analytics",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Data-driven insights into your Madhya Pradesh SI readiness.",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )

            // High level Banner
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Overall MPSI Preparedness",
                                color = GoldBright,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = String.format("%.1f", avgAccuracy) + "% Accuracy",
                                color = Color.White,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(GoldBright.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Speed, contentDescription = null, tint = GoldBright, modifier = Modifier.size(26.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    LinearProgressIndicator(
                        progress = { (avgAccuracy / 100.0).toFloat().coerceIn(0f, 1f) },
                        color = GoldBright,
                        trackColor = NavyDark,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Key Metrics",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            val stats = listOf(
                MetricItem("Average Score", String.format("%.1f", avgScore) + " pts", Icons.Default.AutoGraph, BlueBright),
                MetricItem("Best Score", String.format("%.1f", bestScore) + " pts", Icons.Default.EmojiEvents, GoldAccent),
                MetricItem("Lowest Score", String.format("%.1f", lowestScore) + " pts", Icons.Default.TrendingDown, TextMuted),
                MetricItem("Average Accuracy", String.format("%.1f", avgAccuracy) + "%", Icons.Default.Speed, GreenSuccess)
            )

            stats.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowItems.forEach { item ->
                        DashboardMetricCard(metric = item, modifier = Modifier.weight(1f))
                    }
                    if (rowItems.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        // Subject Breakdown Cards
        item {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Subject-Wise Strength Analysis",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Simulated curriculum tracking across MP Police core subjects.",
                fontSize = 12.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
            )

            val subjects = listOf(
                Triple("Madhya Pradesh Specific GK (MP GK)", 0.85f, GreenSuccess),
                Triple("General Hindi (Paper 1 Grammar)", 0.80f, BlueBright),
                Triple("Maths & Analytical Reasoning", 0.70f, GoldAccent),
                Triple("General Science & Ecology", 0.75f, Color(0xFF0284C7))
            )

            subjects.forEach { (subName, progress, color) ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = subName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
                            Text(text = "${(progress * 100).toInt()}%", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = color)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { progress },
                            color = color,
                            trackColor = Color(0xFFF1F5F9),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )
                    }
                }
            }
        }

        // Test progression history
        if (attempts.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Test-Wise Score Progression",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            items(attempts) { att ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = att.testTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                            Text(text = "Accuracy: ${att.accuracy.toInt()}% • Time: ${att.timeTakenSeconds / 60}m", fontSize = 11.sp, color = TextMuted)
                        }

                        Text(
                            text = "${att.obtainedMarks} / ${att.totalMarks.toInt()} pts",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 15.sp,
                            color = GoldAccent
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttemptEntity
import com.example.data.model.QuestionEntity
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GreenLight
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.theme.RedLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TestResultScreen(
    attempt: AttemptEntity,
    onViewAnswerKey: () -> Unit,
    onBackToDashboard: () -> Unit
) {
    val mins = attempt.timeTakenSeconds / 60
    val secs = attempt.timeTakenSeconds % 60
    val timeFormatted = "${mins}m ${secs}s"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("test_result_screen")
            .padding(16.dp)
    ) {
        item {
            // Main Scorecard Card
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavyPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(GoldBright.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = GoldBright,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = attempt.testTitle,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Obtained Marks Display
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = String.format("%.2f", attempt.obtainedMarks),
                            color = GoldBright,
                            fontSize = 38.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = " / ${attempt.totalMarks.toInt()} Marks",
                            color = Color(0xFFCBD5E1),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val performanceLabel = when {
                        attempt.percentage >= 75.0 -> "Outstanding Performance! Qualified for Physical Stage."
                        attempt.percentage >= 50.0 -> "Good Attempt! Focus on Accuracy & Revision."
                        else -> "Needs Revision. Review Answer Solutions carefully."
                    }

                    Text(
                        text = performanceLabel,
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.12f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Mini Stats Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        ResultMetricCol("Percentage", String.format("%.1f", attempt.percentage) + "%")
                        ResultMetricCol("Accuracy", String.format("%.1f", attempt.accuracy) + "%")
                        ResultMetricCol("Time Taken", timeFormatted)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Performance Breakdown",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Breakdown grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    title = "Correct",
                    value = "${attempt.correct}",
                    subtitle = "+${attempt.correct * 1.0} pts",
                    color = GreenSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Wrong",
                    value = "${attempt.wrong}",
                    subtitle = "-${attempt.wrong * 0.25} pts",
                    color = RedError,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Skipped",
                    value = "${attempt.unattempted}",
                    subtitle = "0.0 pts",
                    color = TextMuted,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))

            // Anti-cheat audit note
            if (attempt.violationCount > 0) {
                Surface(
                    color = RedLight,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = RedError, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Audit Note: ${attempt.violationCount} proctoring tab switches detected during this session.",
                            fontSize = 12.sp,
                            color = RedError,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Action Buttons
            Button(
                onClick = onViewAnswerKey,
                colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("view_answer_key_btn")
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MenuBook, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("View Detailed Answer Key & Explanations", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = onBackToDashboard,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("back_to_dashboard_btn")
            ) {
                Text("Return to Dashboard", color = TextDark, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ResultMetricCol(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        Text(text = label, color = Color(0xFF94A3B8), fontSize = 11.sp)
    }
}

@Composable
fun StatCard(title: String, value: String, subtitle: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = subtitle, fontSize = 10.sp, color = TextMuted)
        }
    }
}

@Composable
fun AnswerKeyScreen(
    attempt: AttemptEntity,
    questions: List<QuestionEntity>,
    onBack: () -> Unit
) {
    // Parse user answers: "qId:opt|qId:opt"
    val userAnswersMap = remember(attempt.userAnswersJson) {
        attempt.userAnswersJson.split("|")
            .filter { it.contains(":") }
            .associate {
                val parts = it.split(":")
                parts[0] to parts.getOrElse(1) { "" }
            }
    }

    var selectedFilter by remember { mutableStateOf("ALL") } // "ALL", "CORRECT", "WRONG", "SKIPPED"

    val filteredQuestions = remember(selectedFilter, questions) {
        when (selectedFilter) {
            "CORRECT" -> questions.filter {
                val ans = userAnswersMap[it.id]
                ans.equals(it.correctAnswer, ignoreCase = true)
            }
            "WRONG" -> questions.filter {
                val ans = userAnswersMap[it.id]
                ans != null && !ans.equals(it.correctAnswer, ignoreCase = true)
            }
            "SKIPPED" -> questions.filter {
                userAnswersMap[it.id].isNullOrBlank()
            }
            else -> questions
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("answer_key_screen")
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "Official Answer Key & Solutions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "${attempt.testTitle} • Score: ${attempt.obtainedMarks}/${attempt.totalMarks.toInt()} pts",
                fontSize = 12.sp,
                color = TextMuted
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == "ALL",
                    onClick = { selectedFilter = "ALL" },
                    label = { Text("All (${questions.size})", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedFilter == "CORRECT",
                    onClick = { selectedFilter = "CORRECT" },
                    label = { Text("Correct (${attempt.correct})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = GreenLight)
                )
                FilterChip(
                    selected = selectedFilter == "WRONG",
                    onClick = { selectedFilter = "WRONG" },
                    label = { Text("Wrong (${attempt.wrong})", fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = RedLight)
                )
                FilterChip(
                    selected = selectedFilter == "SKIPPED",
                    onClick = { selectedFilter = "SKIPPED" },
                    label = { Text("Skipped (${attempt.unattempted})", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        items(filteredQuestions) { q ->
            val userChoice = userAnswersMap[q.id]
            val isCorrect = userChoice.equals(q.correctAnswer, ignoreCase = true)
            val isSkipped = userChoice.isNullOrBlank()

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Status Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                isCorrect -> GreenLight
                                isSkipped -> Color(0xFFF1F5F9)
                                else -> RedLight
                            }
                        ) {
                            Text(
                                text = when {
                                    isCorrect -> "✓ Correct (+1.0)"
                                    isSkipped -> "— Skipped (0.0)"
                                    else -> "✗ Incorrect (-0.25)"
                                },
                                color = when {
                                    isCorrect -> GreenSuccess
                                    isSkipped -> TextMuted
                                    else -> RedError
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = q.subject,
                            fontSize = 11.sp,
                            color = BlueBright,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Q${q.questionNumber}. ${q.questionText}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextDark,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Options list
                    val opts = listOf(
                        "A" to q.optionA,
                        "B" to q.optionB,
                        "C" to q.optionC,
                        "D" to q.optionD
                    )

                    opts.forEach { (key, optText) ->
                        val isUserPick = userChoice == key
                        val isAnswer = q.correctAnswer.equals(key, ignoreCase = true)

                        val bg = when {
                            isAnswer -> GreenLight
                            isUserPick && !isCorrect -> RedLight
                            else -> Color(0xFFF8FAFC)
                        }

                        val borderCol = when {
                            isAnswer -> GreenSuccess
                            isUserPick && !isCorrect -> RedError
                            else -> Color(0xFFE2E8F0)
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = bg,
                            border = androidx.compose.foundation.BorderStroke(1.dp, borderCol),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "$key.",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (isAnswer) GreenSuccess else TextDark
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = optText,
                                    fontSize = 12.sp,
                                    color = TextDark,
                                    modifier = Modifier.weight(1f)
                                )

                                if (isAnswer) {
                                    Text(
                                        text = "CORRECT",
                                        color = GreenSuccess,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                } else if (isUserPick) {
                                    Text(
                                        text = "YOUR CHOICE",
                                        color = RedError,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Detailed Explanation Box
                    Surface(
                        color = Color(0xFFFFFBEB),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFDE68A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Detailed Solution & Concept Explanation:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GoldAccent
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = q.explanation,
                                fontSize = 12.sp,
                                color = TextDark,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            }
        }

        item {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text("Back to Scorecard")
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ResultHistoryScreen(
    attempts: List<AttemptEntity>,
    onViewResult: (String) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("result_history_screen")
            .padding(16.dp)
    ) {
        item {
            Text(
                text = "My Attempt History",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(
                text = "Review all previous mock exam submissions, scores, and answer keys.",
                fontSize = 13.sp,
                color = TextMuted,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
            )
        }

        if (attempts.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("No tests attempted yet.", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Start your first MPSI mock test to build performance history.", fontSize = 12.sp, color = TextMuted)
                    }
                }
            }
        }

        items(attempts) { att ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
                    .clickable { onViewResult(att.id) }
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = att.testTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextDark
                            )
                            Text(
                                text = dateFormat.format(Date(att.submittedAt)),
                                fontSize = 11.sp,
                                color = TextMuted
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${att.obtainedMarks} / ${att.totalMarks.toInt()} pts",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 16.sp,
                                color = GoldAccent
                            )
                            Text(
                                text = "${att.percentage.toInt()}% Score",
                                fontSize = 11.sp,
                                color = GreenSuccess,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Correct: ${att.correct} • Wrong: ${att.wrong} • Accuracy: ${att.accuracy.toInt()}%",
                            fontSize = 11.sp,
                            color = TextMuted
                        )

                        Text(
                            text = "View Scorecard →",
                            fontSize = 12.sp,
                            color = BlueBright,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

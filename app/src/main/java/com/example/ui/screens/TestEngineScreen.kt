package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.PurpleReview
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.TestEngineState

@OptIn(ExperimentalLayoutApi::class, androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun TestEngineScreen(
    state: TestEngineState,
    onSelectOption: (String, String) -> Unit,
    onClearAnswer: (String) -> Unit,
    onToggleReview: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onPrevQuestion: () -> Unit,
    onJumpToQuestion: (Int) -> Unit,
    onTogglePalette: (Boolean) -> Unit,
    onToggleSubmitConfirm: (Boolean) -> Unit,
    onSubmitConfirm: () -> Unit,
    onViolationSimulated: () -> Unit,
    onDismissViolation: () -> Unit
) {
    val currentQ: QuestionEntity? = state.questions.getOrNull(state.currentQuestionIndex)
    val remainingMins = state.remainingSeconds / 60
    val remainingSecs = state.remainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", remainingMins, remainingSecs)
    val isTimeLow = state.remainingSeconds in 1..180

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("test_engine_screen")
    ) {
        // Sticky Top Exam Header
        Surface(
            color = NavyPrimary,
            shadowElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.test?.title ?: "MPSI Online Exam",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "Question ${(state.currentQuestionIndex + 1)} of ${state.questions.size}",
                            color = GoldBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Sticky Countdown Timer Badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isTimeLow) RedError else NavyDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isTimeLow) Color.White else GoldAccent
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = null,
                                tint = if (isTimeLow) Color.White else GoldBright,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = timeFormatted,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Anti-Cheating Violation Bar
                if (state.violationCount > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = RedError.copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proctoring Warnings: ${state.violationCount}/3 (Auto-submit at 3)",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Question Details & Body
        if (currentQ != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(14.dp)
            ) {
                // Metadata chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = BlueBright.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = currentQ.subject,
                                color = BlueBright,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Surface(
                            color = Color(0xFFFEF3C7),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = currentQ.difficulty,
                                color = Color(0xFFB45309),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Text(
                        text = "+1.0  |  -0.25",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Question Box
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Q${state.currentQuestionIndex + 1}. ${currentQ.questionText}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDark,
                            lineHeight = 22.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Options (A, B, C, D)
                val selectedOption = state.userAnswers[currentQ.id]

                val options = listOf(
                    "A" to currentQ.optionA,
                    "B" to currentQ.optionB,
                    "C" to currentQ.optionC,
                    "D" to currentQ.optionD
                )

                options.forEach { (key, text) ->
                    val isSelected = selectedOption == key
                    ExamOptionCard(
                        optionKey = key,
                        optionText = text,
                        isSelected = isSelected,
                        onClick = { onSelectOption(currentQ.id, key) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Bar inside Question
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isMarked = state.markedForReview.contains(currentQ.id)

                    OutlinedButton(
                        onClick = { onToggleReview(currentQ.id) },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (isMarked) PurpleReview else TextMuted
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isMarked) PurpleReview else Color(0xFFCBD5E1)
                        ),
                        modifier = Modifier.testTag("mark_for_review_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isMarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMarked) "Marked" else "Mark for Review",
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (selectedOption != null) {
                        Button(
                            onClick = { onClearAnswer(currentQ.id) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF1F5F9),
                                contentColor = RedError
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("clear_answer_btn")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Clear Answer", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }

        // Bottom Navigation Bar
        Surface(
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Prev
                OutlinedButton(
                    onClick = onPrevQuestion,
                    enabled = state.currentQuestionIndex > 0,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("exam_prev_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Prev", fontSize = 12.sp)
                }

                // Palette Toggle
                Button(
                    onClick = { onTogglePalette(true) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyDark,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("question_palette_btn")
                ) {
                    Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Palette", fontSize = 12.sp)
                }

                // Next or Submit
                if (state.currentQuestionIndex < state.questions.size - 1) {
                    Button(
                        onClick = onNextQuestion,
                        colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("exam_next_btn")
                    ) {
                        Text("Next", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                } else {
                    Button(
                        onClick = { onToggleSubmitConfirm(true) },
                        colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("exam_submit_btn")
                    ) {
                        Text("Submit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }

    // Question Palette Bottom Sheet
    if (state.showPaletteDrawer) {
        ModalBottomSheet(
            onDismissRequest = { onTogglePalette(false) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Question Palette",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )

                    IconButton(onClick = { onTogglePalette(false) }) {
                        Icon(Icons.Default.Close, contentDescription = "Close palette")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    LegendItem(color = GreenSuccess, label = "Answered")
                    LegendItem(color = Color(0xFF94A3B8), label = "Unanswered")
                    LegendItem(color = PurpleReview, label = "Review")
                    LegendItem(color = GoldAccent, label = "Ans & Review")
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Number Grid
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    state.questions.forEachIndexed { index, q ->
                        val isAnswered = state.userAnswers.containsKey(q.id)
                        val isReview = state.markedForReview.contains(q.id)
                        val isCurrent = state.currentQuestionIndex == index

                        val pillColor = when {
                            isAnswered && isReview -> GoldAccent
                            isAnswered -> GreenSuccess
                            isReview -> PurpleReview
                            else -> Color(0xFFE2E8F0)
                        }

                        val textColor = if (pillColor == Color(0xFFE2E8F0)) TextDark else Color.White

                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(pillColor)
                                .border(
                                    width = if (isCurrent) 2.dp else 0.dp,
                                    color = if (isCurrent) NavyPrimary else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onJumpToQuestion(index) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                color = textColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onTogglePalette(false)
                        onToggleSubmitConfirm(true)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Proceed to Submit Test", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Submit Confirmation Dialog
    if (state.showSubmitConfirmDialog) {
        val total = state.questions.size
        val answered = state.userAnswers.size
        val review = state.markedForReview.size
        val unanswered = total - answered

        AlertDialog(
            onDismissRequest = { onToggleSubmitConfirm(false) },
            title = {
                Text(
                    text = "Confirm Exam Submission",
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            },
            text = {
                Column {
                    Text(
                        text = "Are you sure you want to finish and submit your MPSI mock test?",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Questions:", fontSize = 12.sp, color = TextDark)
                                Text("$total", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Answered:", fontSize = 12.sp, color = GreenSuccess)
                                Text("$answered", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = GreenSuccess)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Marked for Review:", fontSize = 12.sp, color = PurpleReview)
                                Text("$review", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PurpleReview)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Unanswered:", fontSize = 12.sp, color = RedError)
                                Text("$unanswered", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = RedError)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = onSubmitConfirm,
                    colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                    modifier = Modifier.testTag("confirm_final_submit_btn")
                ) {
                    Text("Yes, Submit Test", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { onToggleSubmitConfirm(false) }) {
                    Text("Resume Test")
                }
            }
        )
    }

    // Anti-Cheating Violation Warning Dialog
    if (state.showViolationDialog) {
        AlertDialog(
            onDismissRequest = onDismissViolation,
            icon = {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = RedError,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = if (state.violationCount >= 3) "Violation Limit Exceeded!" else "Proctoring Security Warning",
                    fontWeight = FontWeight.Bold,
                    color = RedError
                )
            },
            text = {
                Text(
                    text = if (state.violationCount >= 3) {
                        "You have reached 3 proctoring violations by switching tabs or minimizing the examination. Your test is being automatically submitted for evaluation."
                    } else {
                        "Warning ${state.violationCount} of 3: Leaving the test window or switching applications is strictly monitored. Reaching 3 violations will auto-submit your test."
                    },
                    fontSize = 13.sp,
                    color = TextDark,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = onDismissViolation,
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) {
                    Text("I Understand")
                }
            }
        )
    }
}

@Composable
fun ExamOptionCard(
    optionKey: String,
    optionText: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) BlueBright.copy(alpha = 0.08f) else Color.White,
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) BlueBright else Color(0xFFE2E8F0)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("option_${optionKey}")
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) BlueBright else Color(0xFFF1F5F9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionKey,
                    color = if (isSelected) Color.White else TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = optionText,
                color = TextDark,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun LegendItem(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, fontSize = 11.sp, color = TextMuted)
    }
}

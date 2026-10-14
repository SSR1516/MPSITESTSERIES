package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttemptEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AdminMetrics
import com.example.ui.theme.BlueBright
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GoldBright
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.NavyDark
import com.example.ui.theme.NavyPrimary
import com.example.ui.theme.RedError
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun AdminDashboardScreen(
    metrics: AdminMetrics,
    seriesList: List<TestSeriesEntity>,
    testsList: List<TestEntity>,
    studentsList: List<UserEntity>,
    paymentsList: List<PaymentEntity>,
    attemptsList: List<AttemptEntity>,
    onSaveSeries: (TestSeriesEntity) -> Unit,
    onDeleteSeries: (String) -> Unit,
    onSaveTest: (TestEntity) -> Unit,
    onDeleteTest: (String) -> Unit,
    onSaveQuestion: (QuestionEntity) -> Unit,
    onToggleStudentStatus: (UserEntity) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Overview", "Test Series", "Tests", "Question Bank", "Students", "Payments", "Attempts", "Settings")

    // Dialog States
    var showCreateSeriesDialog by remember { mutableStateOf(false) }
    var seriesToEdit by remember { mutableStateOf<TestSeriesEntity?>(null) }
    var showCreateTestDialog by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Top Admin Alert Header
        Surface(
            color = Color(0xFF991B1B),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MPSI Police Recruitment Board Admin Console",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        // Horizontal Tabs Bar
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = NavyDark,
            contentColor = Color.White,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = GoldBright
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (selectedTab == index) GoldBright else Color(0xFF94A3B8)
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> AdminOverviewTab(metrics, paymentsList, attemptsList)
                1 -> AdminSeriesTab(
                    seriesList = seriesList,
                    onAddNew = { showCreateSeriesDialog = true },
                    onEditSeries = { seriesToEdit = it },
                    onDelete = onDeleteSeries
                )
                2 -> AdminTestsTab(testsList, seriesList, onAddNew = { showCreateTestDialog = true }, onDelete = onDeleteTest)
                3 -> AdminQuestionBankTab(testsList, onAddNew = { showAddQuestionDialog = true }, onSaveQuestion = onSaveQuestion)
                4 -> AdminStudentsTab(studentsList, onToggleSuspend = onToggleStudentStatus)
                5 -> AdminPaymentsTab(paymentsList)
                6 -> AdminAttemptsTab(attemptsList)
                7 -> AdminSettingsTab()
            }
        }
    }

    // Dialog: Create Test Series
    if (showCreateSeriesDialog) {
        CreateSeriesDialog(
            onDismiss = { showCreateSeriesDialog = false },
            onConfirm = { series ->
                onSaveSeries(series)
                showCreateSeriesDialog = false
            }
        )
    }

    // Dialog: Edit Test Series Price & Settings
    seriesToEdit?.let { series ->
        EditSeriesPriceDialog(
            series = series,
            onDismiss = { seriesToEdit = null },
            onConfirm = { updated ->
                onSaveSeries(updated)
                seriesToEdit = null
            }
        )
    }

    // Dialog: Create Test
    if (showCreateTestDialog) {
        CreateTestDialog(
            seriesList = seriesList,
            onDismiss = { showCreateTestDialog = false },
            onConfirm = { test ->
                onSaveTest(test)
                showCreateTestDialog = false
            }
        )
    }

    // Dialog: Add Question
    if (showAddQuestionDialog) {
        AddQuestionDialog(
            testsList = testsList,
            onDismiss = { showAddQuestionDialog = false },
            onConfirm = { question ->
                onSaveQuestion(question)
                showAddQuestionDialog = false
            }
        )
    }
}

// ---------------- TAB 0: OVERVIEW ----------------
@Composable
fun AdminOverviewTab(metrics: AdminMetrics, payments: List<PaymentEntity>, attempts: List<AttemptEntity>) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "System Metrics",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(10.dp))

            val stats = listOf(
                MetricItem("Total Students", "${metrics.totalStudents}", Icons.Default.People, NavyPrimary),
                MetricItem("Paid Students", "${metrics.paidStudents}", Icons.Default.CheckCircle, GreenSuccess),
                MetricItem("Gross Revenue", "₹${metrics.totalRevenue.toInt()}", Icons.Default.AttachMoney, GoldAccent),
                MetricItem("Total Series", "${metrics.totalSeries}", Icons.Default.ShoppingBag, BlueBright),
                MetricItem("Total Tests", "${metrics.totalTests}", Icons.Default.Assignment, NavyDark),
                MetricItem("Total Attempts", "${metrics.totalAttempts}", Icons.Default.TrendingUp, Color(0xFF9333EA))
            )

            stats.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    row.forEach { item ->
                        DashboardMetricCard(metric = item, modifier = Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Recent Transactions",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        items(payments.take(4)) { pay ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = pay.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        Text(text = "${pay.seriesTitle} • ${pay.razorpayOrderId}", fontSize = 11.sp, color = TextMuted)
                    }
                    Text(text = "₹${pay.amount.toInt()}", fontWeight = FontWeight.Black, color = GreenSuccess, fontSize = 14.sp)
                }
            }
        }
    }
}

// ---------------- TAB 1: TEST SERIES MANAGEMENT ----------------
@Composable
fun AdminSeriesTab(
    seriesList: List<TestSeriesEntity>,
    onAddNew: () -> Unit,
    onEditSeries: (TestSeriesEntity) -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Test Series Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_series_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Series", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(seriesList) { series ->
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
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = series.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                            Text(
                                text = "Price: ₹${series.price.toInt()} (Configured) • ${series.numberOfTests} Tests • ${series.validityDays} Days Validity",
                                fontSize = 12.sp,
                                color = TextMuted
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { onEditSeries(series) }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Price & Settings", tint = BlueBright)
                            }
                            IconButton(onClick = { onDelete(series.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------------- TAB 2: TESTS MANAGEMENT ----------------
@Composable
fun AdminTestsTab(
    testsList: List<TestEntity>,
    seriesList: List<TestSeriesEntity>,
    onAddNew: () -> Unit,
    onDelete: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tests Management",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_test_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("New Test", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(testsList) { test ->
            val parentSeries = seriesList.find { it.id == test.seriesId }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = test.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Text(text = "Series: ${parentSeries?.title ?: test.seriesId}", fontSize = 11.sp, color = BlueBright)
                        Text(text = "${test.durationMinutes}m | ${test.totalMarks} Marks | -${test.negativeMarks} Neg", fontSize = 11.sp, color = TextMuted)
                    }

                    IconButton(onClick = { onDelete(test.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = RedError)
                    }
                }
            }
        }
    }
}

// ---------------- TAB 3: QUESTION BANK ----------------
@Composable
fun AdminQuestionBankTab(
    testsList: List<TestEntity>,
    onAddNew: () -> Unit,
    onSaveQuestion: (QuestionEntity) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Question Bank",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Button(
                    onClick = onAddNew,
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_question_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Question", fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bulk Question Ingestion / CSV Support",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Questions are stored in the relational question repository with full support for MCQ formats, Hindi/English Unicode, explanations, subjects, and topics.",
                        fontSize = 12.sp,
                        color = TextMuted,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onAddNew,
                        colors = ButtonDefaults.buttonColors(containerColor = BlueBright),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Add New Question to Exam", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ---------------- TAB 4: STUDENTS MANAGEMENT ----------------
@Composable
fun AdminStudentsTab(
    students: List<UserEntity>,
    onToggleSuspend: (UserEntity) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "Student Accounts (${students.size})",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(students) { student ->
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
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = student.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                        Text(text = "${student.email} • +91 ${student.mobile}", fontSize = 12.sp, color = TextMuted)
                        Text(text = "Joined: ${dateFormat.format(Date(student.createdAt))}", fontSize = 11.sp, color = TextMuted)
                    }

                    Button(
                        onClick = { onToggleSuspend(student) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (student.isSuspended) GreenSuccess else Color(0xFFDC2626)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (student.isSuspended) "Reactivate" else "Suspend",
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

// ---------------- TAB 5: PAYMENTS AUDIT ----------------
@Composable
fun AdminPaymentsTab(payments: List<PaymentEntity>) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "Razorpay Payments Ledger",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )
            Text(text = "Audit trail of student transactions and enrollments.", fontSize = 12.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(10.dp))
        }

        items(payments) { pay ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = pay.studentName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        Text(
                            text = "₹${pay.amount.toInt()}",
                            fontWeight = FontWeight.Black,
                            color = if (pay.status == "PAID") GreenSuccess else Color(0xFFDC2626),
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = pay.seriesTitle, fontSize = 12.sp, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (pay.status) {
                                "PAID" -> GreenSuccess.copy(alpha = 0.15f)
                                "FAILED" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFF3F4F6)
                            }
                        ) {
                            Text(
                                text = pay.status,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = when (pay.status) {
                                    "PAID" -> GreenSuccess
                                    "FAILED" -> Color(0xFFDC2626)
                                    else -> TextMuted
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Order: ${pay.razorpayOrderId}", fontSize = 11.sp, color = TextMuted)
                    Text(text = "Payment ID: ${pay.razorpayPaymentId}", fontSize = 11.sp, color = TextMuted)
                    Text(text = dateFormat.format(Date(pay.createdAt)), fontSize = 11.sp, color = TextMuted)
                }
            }
        }
    }
}

// ---------------- TAB 6: ATTEMPTS LOG ----------------
@Composable
fun AdminAttemptsTab(attempts: List<AttemptEntity>) {
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()) }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Text(
                text = "Exam Attempt Logs",
                fontSize = 18.sp,
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
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = att.testTitle, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        Text(text = "Submitted: ${dateFormat.format(Date(att.submittedAt))}", fontSize = 11.sp, color = TextMuted)
                        Text(text = "Correct: ${att.correct} | Wrong: ${att.wrong} | Skipped: ${att.unattempted}", fontSize = 11.sp, color = TextMuted)
                        if (att.violationCount > 0) {
                            Text(text = "⚠ Proctoring violations: ${att.violationCount}", fontSize = 11.sp, color = RedError, fontWeight = FontWeight.Bold)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "${att.obtainedMarks} / ${att.totalMarks.toInt()} pts", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = GoldAccent)
                        Text(text = "${att.accuracy.toInt()}% Acc", fontSize = 11.sp, color = GreenSuccess, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// ---------------- TAB 7: SETTINGS ----------------
@Composable
fun AdminSettingsTab() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Payment Gateway & Security Configuration",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(12.dp))

        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Razorpay Integration Credentials",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Active Mode: MOCK TEST SANDBOX MODE",
                    color = GreenSuccess,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Server-side payment verification is enabled. Live keys can be injected securely via environment secrets (RAZORPAY_KEY_ID & RAZORPAY_KEY_SECRET) without altering client code.",
                    fontSize = 12.sp,
                    color = TextMuted,
                    lineHeight = 17.sp
                )
            }
        }
    }
}

// --- CREATE TEST SERIES DIALOG ---
@Composable
fun CreateSeriesDialog(
    onDismiss: () -> Unit,
    onConfirm: (TestSeriesEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var priceStr by remember { mutableStateOf("100") }
    var validityStr by remember { mutableStateOf("180") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New MPSI Test Series", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Series Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Price (₹ INR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = validityStr,
                    onValueChange = { validityStr = it },
                    label = { Text("Validity Days") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val series = TestSeriesEntity(
                            id = "series_" + UUID.randomUUID().toString().take(8),
                            title = title.trim(),
                            description = description.trim(),
                            numberOfTests = 5,
                            questionsPerTest = 50,
                            durationMinutes = 60,
                            totalMarks = 50,
                            price = priceStr.toDoubleOrNull() ?: 100.0,
                            validityDays = validityStr.toIntOrNull() ?: 180
                        )
                        onConfirm(series)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Create Series")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// --- EDIT TEST SERIES PRICE & SETTINGS DIALOG ---
@Composable
fun EditSeriesPriceDialog(
    series: TestSeriesEntity,
    onDismiss: () -> Unit,
    onConfirm: (TestSeriesEntity) -> Unit
) {
    var title by remember { mutableStateOf(series.title) }
    var description by remember { mutableStateOf(series.description) }
    var priceStr by remember { mutableStateOf(series.price.toInt().toString()) }
    var validityStr by remember { mutableStateOf(series.validityDays.toString()) }
    var isPublished by remember { mutableStateOf(series.isPublished) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Configure Test Series & Pricing", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Series Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = priceStr,
                    onValueChange = { priceStr = it },
                    label = { Text("Configured Price (₹ INR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = validityStr,
                    onValueChange = { validityStr = it },
                    label = { Text("Validity Days") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                ) {
                    Text("Published & Active for Students", fontSize = 13.sp, color = TextDark)
                    androidx.compose.material3.Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it }
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedPrice = priceStr.toDoubleOrNull() ?: 100.0
                    val updated = series.copy(
                        title = title.trim(),
                        description = description.trim(),
                        price = updatedPrice,
                        validityDays = validityStr.toIntOrNull() ?: 180,
                        isPublished = isPublished
                    )
                    onConfirm(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Configuration")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// --- CREATE TEST DIALOG ---
@Composable
fun CreateTestDialog(
    seriesList: List<TestSeriesEntity>,
    onDismiss: () -> Unit,
    onConfirm: (TestEntity) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var durationStr by remember { mutableStateOf("15") }
    var marksStr by remember { mutableStateOf("10") }
    var selectedSeriesId by remember { mutableStateOf(seriesList.firstOrNull()?.id ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Test to Series", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Test Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = durationStr,
                    onValueChange = { durationStr = it },
                    label = { Text("Duration (Minutes)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = marksStr,
                    onValueChange = { marksStr = it },
                    label = { Text("Total Marks") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val test = TestEntity(
                            id = "test_" + UUID.randomUUID().toString().take(8),
                            seriesId = selectedSeriesId,
                            title = title.trim(),
                            description = "MPSI mock test",
                            durationMinutes = durationStr.toIntOrNull() ?: 15,
                            totalMarks = marksStr.toIntOrNull() ?: 10,
                            marksPerQuestion = 1.0,
                            negativeMarks = 0.25,
                            maxAttempts = 5
                        )
                        onConfirm(test)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Add Test")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// --- ADD QUESTION DIALOG ---
@Composable
fun AddQuestionDialog(
    testsList: List<TestEntity>,
    onDismiss: () -> Unit,
    onConfirm: (QuestionEntity) -> Unit
) {
    var qText by remember { mutableStateOf("") }
    var optA by remember { mutableStateOf("") }
    var optB by remember { mutableStateOf("") }
    var optC by remember { mutableStateOf("") }
    var optD by remember { mutableStateOf("") }
    var correctAns by remember { mutableStateOf("A") }
    var explanation by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("MP Specific GK") }
    var testId by remember { mutableStateOf(testsList.firstOrNull()?.id ?: "test_01") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Question to Exam", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = qText,
                    onValueChange = { qText = it },
                    label = { Text("Question Text (Hindi/English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optA,
                    onValueChange = { optA = it },
                    label = { Text("Option A") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optB,
                    onValueChange = { optB = it },
                    label = { Text("Option B") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optC,
                    onValueChange = { optC = it },
                    label = { Text("Option C") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = optD,
                    onValueChange = { optD = it },
                    label = { Text("Option D") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = correctAns,
                    onValueChange = { correctAns = it.uppercase().take(1) },
                    label = { Text("Correct Answer (A, B, C, or D)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = explanation,
                    onValueChange = { explanation = it },
                    label = { Text("Detailed Explanation / Solution") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (qText.isNotBlank()) {
                        val q = QuestionEntity(
                            id = "q_" + UUID.randomUUID().toString().take(8),
                            testId = testId,
                            questionNumber = 11,
                            questionText = qText.trim(),
                            optionA = optA.ifBlank { "Option A" },
                            optionB = optB.ifBlank { "Option B" },
                            optionC = optC.ifBlank { "Option C" },
                            optionD = optD.ifBlank { "Option D" },
                            correctAnswer = correctAns.ifBlank { "A" },
                            explanation = explanation.ifBlank { "Detailed concept explanation." },
                            subject = subject,
                            topic = "General Topic",
                            difficulty = "Medium"
                        )
                        onConfirm(q)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save Question")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

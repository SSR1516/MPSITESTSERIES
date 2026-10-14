package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.TopNavBar
import com.example.ui.screens.AccessDeniedScreen
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AnswerKeyScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PerformanceAnalyticsScreen
import com.example.ui.screens.RazorpayCheckoutScreen
import com.example.ui.screens.ResultHistoryScreen
import com.example.ui.screens.SeriesDetailScreen
import com.example.ui.screens.StudentDashboardScreen
import com.example.ui.screens.TestEngineScreen
import com.example.ui.screens.TestResultScreen
import com.example.ui.screens.TestSeriesListScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MpsiViewModel
import com.example.ui.viewmodel.Screen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: MpsiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MpsiApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MpsiApp(viewModel: MpsiViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val authError by viewModel.authError.collectAsStateWithLifecycle()
    val authLoading by viewModel.authLoading.collectAsStateWithLifecycle()

    val publishedSeries by viewModel.publishedSeries.collectAsStateWithLifecycle()
    val allSeries by viewModel.allSeries.collectAsStateWithLifecycle()
    val allTests by viewModel.allTests.collectAsStateWithLifecycle()
    val userEnrollments by viewModel.userEnrollments.collectAsStateWithLifecycle()
    val userAttempts by viewModel.userAttempts.collectAsStateWithLifecycle()
    val adminMetrics by viewModel.adminMetrics.collectAsStateWithLifecycle()
    val allStudents by viewModel.allStudents.collectAsStateWithLifecycle()
    val allPayments by viewModel.allPayments.collectAsStateWithLifecycle()
    val allAttempts by viewModel.allAttempts.collectAsStateWithLifecycle()

    val testEngineState by viewModel.testEngineState.collectAsStateWithLifecycle()
    val paymentLoading by viewModel.paymentLoading.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Show toast / snackbar messages
    LaunchedEffect(message) {
        message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    // Handle BackHandler for non-home screens
    val isRootScreen = currentScreen is Screen.Home
    if (!isRootScreen) {
        BackHandler {
            if (currentScreen is Screen.TestEngine) {
                // If in test engine, prompt submission confirmation rather than abrupt exit
                viewModel.toggleSubmitConfirm(true)
            } else {
                viewModel.navigateBack()
            }
        }
    }

    // Lifecycle monitoring for Proctoring Anti-Cheating during TestEngine
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, currentScreen) {
        val observer = LifecycleEventObserver { _, event ->
            if (currentScreen is Screen.TestEngine && !testEngineState.isSubmitted) {
                if (event == Lifecycle.Event.ON_PAUSE || event == Lifecycle.Event.ON_STOP) {
                    viewModel.registerProctoringViolation()
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen !is Screen.TestEngine) {
                TopNavBar(
                    title = "MPSI TEST SERIES",
                    subtitle = when (currentScreen) {
                        is Screen.AdminDashboard -> "Admin Board Console"
                        is Screen.StudentDashboard -> "Student Dashboard"
                        is Screen.TestSeriesList -> "All Test Series"
                        is Screen.PerformanceAnalytics -> "Performance Analytics"
                        is Screen.ResultHistory -> "Exam History"
                        else -> "MP Police SI Preparation"
                    },
                    currentUser = currentUser,
                    canNavigateBack = !isRootScreen,
                    onBackClick = { viewModel.navigateBack() },
                    onLogoutClick = { viewModel.logout() },
                    onAuthClick = { viewModel.navigateTo(Screen.Auth) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val screen = currentScreen) {
                is Screen.Home -> {
                    HomeScreen(
                        currentUser = currentUser,
                        testSeriesList = publishedSeries,
                        onExploreClick = { viewModel.navigateTo(Screen.TestSeriesList) },
                        onLoginClick = { viewModel.navigateTo(Screen.Auth) },
                        onDashboardClick = {
                            if (currentUser?.role == "ADMIN") {
                                viewModel.refreshAdminMetrics()
                                viewModel.navigateTo(Screen.AdminDashboard)
                            } else {
                                viewModel.navigateTo(Screen.StudentDashboard)
                            }
                        },
                        onSeriesClick = { seriesId ->
                            viewModel.navigateTo(Screen.SeriesDetail(seriesId))
                        },
                        onDemoLogin = { role ->
                            viewModel.quickDemoLogin(role)
                        }
                    )
                }

                is Screen.Auth -> {
                    AuthScreen(
                        authError = authError,
                        isLoading = authLoading,
                        onLogin = { email, pass -> viewModel.login(email, pass) },
                        onRegister = { name, mobile, email, pass -> viewModel.register(name, mobile, email, pass) },
                        onQuickDemo = { role -> viewModel.quickDemoLogin(role) }
                    )
                }

                is Screen.StudentDashboard -> {
                    currentUser?.let { user ->
                        StudentDashboardScreen(
                            user = user,
                            enrollments = userEnrollments,
                            publishedSeries = publishedSeries,
                            allTests = allTests,
                            attempts = userAttempts,
                            onExploreSeries = { viewModel.navigateTo(Screen.TestSeriesList) },
                            onStartTest = { testId -> viewModel.startTestIfAuthorized(testId) },
                            onViewResultsHistory = { viewModel.navigateTo(Screen.ResultHistory) },
                            onViewAnalytics = { viewModel.navigateTo(Screen.PerformanceAnalytics) },
                            onViewResultDetail = { attemptId -> viewModel.navigateTo(Screen.TestResult(attemptId)) }
                        )
                    } ?: run {
                        viewModel.navigateTo(Screen.Auth)
                    }
                }

                is Screen.TestSeriesList -> {
                    TestSeriesListScreen(
                        seriesList = publishedSeries,
                        enrollments = userEnrollments,
                        onSeriesClick = { seriesId -> viewModel.navigateTo(Screen.SeriesDetail(seriesId)) },
                        onPurchaseClick = { seriesId -> viewModel.initiatePurchase(seriesId) }
                    )
                }

                is Screen.SeriesDetail -> {
                    val series = publishedSeries.find { it.id == screen.seriesId }
                        ?: allSeries.find { it.id == screen.seriesId }

                    if (series != null) {
                        val testsForSeries = allTests.filter { it.seriesId == series.id }
                        val isEnrolled = userEnrollments.any { it.seriesId == series.id }
                        SeriesDetailScreen(
                            series = series,
                            tests = testsForSeries,
                            isEnrolled = isEnrolled,
                            onPurchaseClick = { viewModel.initiatePurchase(series.id) },
                            onStartTest = { testId -> viewModel.startTestIfAuthorized(testId) }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }

                is Screen.RazorpayCheckout -> {
                    RazorpayCheckoutScreen(
                        order = screen.order,
                        isLoading = paymentLoading,
                        onPaymentSuccess = { method, customId ->
                            viewModel.executeMockRazorpayPayment(screen.order, method, customId)
                        },
                        onPaymentFailed = { reason ->
                            viewModel.simulateFailedRazorpayPayment(screen.order, reason)
                        },
                        onCancel = {
                            viewModel.cancelRazorpayPayment(screen.order)
                        }
                    )
                }

                is Screen.AccessDenied -> {
                    AccessDeniedScreen(
                        reason = screen.reason,
                        canPurchase = screen.canPurchase,
                        seriesId = screen.seriesId,
                        onPurchaseClick = { seriesId -> viewModel.initiatePurchase(seriesId) },
                        onBackClick = { viewModel.navigateBack() }
                    )
                }

                is Screen.TestEngine -> {
                    TestEngineScreen(
                        state = testEngineState,
                        onSelectOption = { qId, opt -> viewModel.selectOption(qId, opt) },
                        onClearAnswer = { qId -> viewModel.clearAnswer(qId) },
                        onToggleReview = { qId -> viewModel.toggleReview(qId) },
                        onNextQuestion = { viewModel.nextQuestion() },
                        onPrevQuestion = { viewModel.prevQuestion() },
                        onJumpToQuestion = { idx -> viewModel.goToQuestion(idx) },
                        onTogglePalette = { show -> viewModel.togglePalette(show) },
                        onToggleSubmitConfirm = { show -> viewModel.toggleSubmitConfirm(show) },
                        onSubmitConfirm = { viewModel.submitTest() },
                        onViolationSimulated = { viewModel.registerProctoringViolation() },
                        onDismissViolation = { viewModel.dismissViolationDialog() }
                    )
                }

                is Screen.TestResult -> {
                    val attempt = userAttempts.find { it.id == screen.attemptId }
                        ?: allAttempts.find { it.id == screen.attemptId }

                    if (attempt != null) {
                        TestResultScreen(
                            attempt = attempt,
                            onViewAnswerKey = { viewModel.navigateTo(Screen.AnswerKey(attempt.id)) },
                            onBackToDashboard = {
                                if (currentUser?.role == "ADMIN") {
                                    viewModel.navigateTo(Screen.AdminDashboard)
                                } else {
                                    viewModel.navigateTo(Screen.StudentDashboard)
                                }
                            }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }

                is Screen.AnswerKey -> {
                    val attempt = userAttempts.find { it.id == screen.attemptId }
                        ?: allAttempts.find { it.id == screen.attemptId }

                    if (attempt != null) {
                        val questions = testEngineState.questions.ifEmpty {
                            // If not in engine state, load questions from repo
                            remember(attempt.testId) {
                                emptyList()
                            }
                        }

                        // Also fetch questions safely
                        var loadedQuestions by remember { androidx.compose.runtime.mutableStateOf(testEngineState.questions) }
                        LaunchedEffect(attempt.testId) {
                            if (loadedQuestions.isEmpty()) {
                                loadedQuestions = viewModel.repository.getQuestionsForTestOnce(attempt.testId)
                            }
                        }

                        AnswerKeyScreen(
                            attempt = attempt,
                            questions = loadedQuestions,
                            onBack = { viewModel.navigateBack() }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }

                is Screen.ResultHistory -> {
                    ResultHistoryScreen(
                        attempts = userAttempts,
                        onViewResult = { attemptId -> viewModel.navigateTo(Screen.TestResult(attemptId)) }
                    )
                }

                is Screen.PerformanceAnalytics -> {
                    PerformanceAnalyticsScreen(
                        attempts = userAttempts
                    )
                }

                is Screen.AdminDashboard -> {
                    AdminDashboardScreen(
                        metrics = adminMetrics,
                        seriesList = allSeries,
                        testsList = allTests,
                        studentsList = allStudents,
                        paymentsList = allPayments,
                        attemptsList = allAttempts,
                        onSaveSeries = { viewModel.saveTestSeries(it) },
                        onDeleteSeries = { viewModel.deleteTestSeries(it) },
                        onSaveTest = { viewModel.saveTest(it) },
                        onDeleteTest = { viewModel.deleteTest(it) },
                        onSaveQuestion = { viewModel.saveQuestion(it) },
                        onToggleStudentStatus = { viewModel.toggleStudentStatus(it) }
                    )
                }
            }
        }
    }
}

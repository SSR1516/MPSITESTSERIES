package com.example.ui.viewmodel

import android.app.Application
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SeedDataLoader
import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import com.example.data.repository.AccessCheckResult
import com.example.data.repository.AdminMetrics
import com.example.data.repository.AuthResult
import com.example.data.repository.MpsiRepository
import com.example.data.repository.RazorpayOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Auth : Screen()
    object StudentDashboard : Screen()
    object TestSeriesList : Screen()
    data class SeriesDetail(val seriesId: String) : Screen()
    data class RazorpayCheckout(val order: RazorpayOrder) : Screen()
    data class AccessDenied(val reason: String, val canPurchase: Boolean, val seriesId: String?) : Screen()
    data class TestEngine(val testId: String) : Screen()
    data class TestResult(val attemptId: String) : Screen()
    data class AnswerKey(val attemptId: String) : Screen()
    object ResultHistory : Screen()
    object PerformanceAnalytics : Screen()
    object AdminDashboard : Screen()
}

data class TestEngineState(
    val test: TestEntity? = null,
    val series: TestSeriesEntity? = null,
    val questions: List<QuestionEntity> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, String> = emptyMap(), // questionId -> "A"|"B"|"C"|"D"
    val markedForReview: Set<String> = emptySet(), // set of questionIds
    val remainingSeconds: Long = 0,
    val totalSeconds: Long = 0,
    val violationCount: Int = 0,
    val showViolationDialog: Boolean = false,
    val showSubmitConfirmDialog: Boolean = false,
    val showPaletteDrawer: Boolean = false,
    val isSubmitted: Boolean = false,
    val submittedAttemptId: String? = null
)

class MpsiViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    val databaseHelper = com.example.data.DatabaseHelper.getInstance(application)
    val repository = MpsiRepository(database, databaseHelper)

    // Current Screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Home)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation back stack for BackHandler
    private val screenStack = mutableListOf<Screen>()

    // Current Logged-in User
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Auth State
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _authLoading = MutableStateFlow(false)
    val authLoading: StateFlow<Boolean> = _authLoading.asStateFlow()

    // Published Test Series list
    val publishedSeries: StateFlow<List<TestSeriesEntity>> = repository.getPublishedTestSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Test Series (for Admin)
    val allSeries: StateFlow<List<TestSeriesEntity>> = repository.getAllTestSeries()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Tests (for Admin)
    val allTests: StateFlow<List<TestEntity>> = repository.getAllTests()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Student Enrollments
    private val _userEnrollments = MutableStateFlow<List<EnrollmentEntity>>(emptyList())
    val userEnrollments: StateFlow<List<EnrollmentEntity>> = _userEnrollments.asStateFlow()

    // Student Attempts
    private val _userAttempts = MutableStateFlow<List<AttemptEntity>>(emptyList())
    val userAttempts: StateFlow<List<AttemptEntity>> = _userAttempts.asStateFlow()

    // Admin Metrics
    private val _adminMetrics = MutableStateFlow(AdminMetrics(0, 0, 0, 0, 0, 0.0))
    val adminMetrics: StateFlow<AdminMetrics> = _adminMetrics.asStateFlow()

    val allStudents: StateFlow<List<UserEntity>> = repository.getAllStudents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allPayments: StateFlow<List<PaymentEntity>> = repository.getAllPayments()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<AttemptEntity>> = repository.getAllAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Test Engine State & Timer
    private val _testEngineState = MutableStateFlow(TestEngineState())
    val testEngineState: StateFlow<TestEngineState> = _testEngineState.asStateFlow()

    private var examTimer: CountDownTimer? = null

    // Payment Sandbox State
    private val _paymentLoading = MutableStateFlow(false)
    val paymentLoading: StateFlow<Boolean> = _paymentLoading.asStateFlow()

    private val _paymentSuccessMessage = MutableStateFlow<String?>(null)
    val paymentSuccessMessage: StateFlow<String?> = _paymentSuccessMessage.asStateFlow()

    // Notification / Toast message
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        // Pre-load demo student and verify seed data on startup
        viewModelScope.launch(Dispatchers.IO) {
            if (database.testSeriesDao().countSeries() == 0) {
                SeedDataLoader.populateInitialData(database)
            }
            val defaultStudent = repository.getUserById("student_01")
            if (defaultStudent != null) {
                _currentUser.value = defaultStudent
                loadUserData(defaultStudent.id)
            }
        }
    }

    fun navigateTo(screen: Screen) {
        val current = _currentScreen.value
        if (current != screen) {
            screenStack.add(current)
            _currentScreen.value = screen
        }
    }

    fun navigateBack() {
        if (screenStack.isNotEmpty()) {
            _currentScreen.value = screenStack.removeAt(screenStack.size - 1)
        } else {
            _currentScreen.value = if (_currentUser.value?.role == "ADMIN") Screen.AdminDashboard else Screen.Home
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    fun clearAuthError() {
        _authError.value = null
    }

    // --- Authentication ---
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            when (val res = repository.login(email, pass)) {
                is AuthResult.Success -> {
                    _currentUser.value = res.user
                    loadUserData(res.user.id)
                    _authLoading.value = false
                    if (res.user.role == "ADMIN") {
                        refreshAdminMetrics()
                        _currentScreen.value = Screen.AdminDashboard
                    } else {
                        _currentScreen.value = Screen.StudentDashboard
                    }
                }
                is AuthResult.Error -> {
                    _authLoading.value = false
                    _authError.value = res.message
                }
            }
        }
    }

    fun register(name: String, mobile: String, email: String, pass: String) {
        viewModelScope.launch {
            _authLoading.value = true
            _authError.value = null
            when (val res = repository.registerStudent(name, mobile, email, pass)) {
                is AuthResult.Success -> {
                    _currentUser.value = res.user
                    loadUserData(res.user.id)
                    _authLoading.value = false
                    _currentScreen.value = Screen.StudentDashboard
                    _message.value = "Registration successful! Welcome to MPSI Test Series."
                }
                is AuthResult.Error -> {
                    _authLoading.value = false
                    _authError.value = res.message
                }
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userEnrollments.value = emptyList()
        _userAttempts.value = emptyList()
        screenStack.clear()
        _currentScreen.value = Screen.Home
    }

    fun quickDemoLogin(role: String) {
        if (role == "ADMIN") {
            login("admin@mpsi.com", "admin123")
        } else {
            login("student@mpsi.com", "student123")
        }
    }

    private fun loadUserData(userId: String) {
        viewModelScope.launch {
            repository.getEnrollmentsForUser(userId).collect {
                _userEnrollments.value = it
            }
        }
        viewModelScope.launch {
            repository.getAttemptsForUser(userId).collect {
                _userAttempts.value = it
            }
        }
    }

    fun refreshAdminMetrics() {
        viewModelScope.launch {
            _adminMetrics.value = repository.getAdminMetrics()
        }
    }

    // --- Secure Test Access & Starting ---
    fun startTestIfAuthorized(testId: String) {
        val user = _currentUser.value
        if (user == null) {
            navigateTo(Screen.Auth)
            return
        }

        viewModelScope.launch {
            when (val result = repository.verifyTestAccess(user.id, testId)) {
                is AccessCheckResult.Allowed -> {
                    val questions = repository.getQuestionsForTestOnce(testId)
                    val totalSecs = (result.test.durationMinutes * 60).toLong()

                    _testEngineState.value = TestEngineState(
                        test = result.test,
                        series = result.series,
                        questions = questions,
                        currentQuestionIndex = 0,
                        userAnswers = emptyMap(),
                        markedForReview = emptySet(),
                        remainingSeconds = totalSecs,
                        totalSeconds = totalSecs,
                        violationCount = 0,
                        showViolationDialog = false,
                        showSubmitConfirmDialog = false,
                        showPaletteDrawer = false,
                        isSubmitted = false,
                        submittedAttemptId = null
                    )

                    startTimer(totalSecs)
                    navigateTo(Screen.TestEngine(testId))
                }
                is AccessCheckResult.Denied -> {
                    navigateTo(Screen.AccessDenied(result.reason, result.canPurchase, result.seriesId))
                }
            }
        }
    }

    // --- Payment Flow (Razorpay Sandbox) ---
    fun initiatePurchase(seriesId: String) {
        val user = _currentUser.value
        if (user == null) {
            navigateTo(Screen.Auth)
            return
        }
        viewModelScope.launch {
            try {
                val order = repository.createRazorpayOrder(user.id, seriesId)
                navigateTo(Screen.RazorpayCheckout(order))
            } catch (e: Exception) {
                _message.value = "Error preparing checkout: ${e.message}"
            }
        }
    }

    fun executeMockRazorpayPayment(
        order: RazorpayOrder,
        paymentMethod: String,
        customPaymentId: String? = null
    ) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _paymentLoading.value = true
            // Simulated secure gateway transaction
            kotlinx.coroutines.delay(1000)
            val paymentId = if (!customPaymentId.isNullOrBlank()) {
                customPaymentId.trim()
            } else {
                "pay_rzp_" + java.util.UUID.randomUUID().toString().replace("-", "").take(12)
            }
            val success = repository.completePaymentAndEnroll(order, user.id, paymentId, "PAID")
            _paymentLoading.value = false

            if (success) {
                _paymentSuccessMessage.value = "Payment Successful ($paymentId) via $paymentMethod! Enrolled in ${order.seriesTitle}"
                _message.value = "Payment Verified ($paymentId)! Unlocked ${order.seriesTitle}."
                navigateBack()
            } else {
                _message.value = "Payment verification failed. Test series remains locked."
            }
        }
    }

    fun simulateFailedRazorpayPayment(order: RazorpayOrder, reason: String = "Transaction declined by bank") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            _paymentLoading.value = true
            kotlinx.coroutines.delay(800)
            val paymentId = "pay_fail_" + java.util.UUID.randomUUID().toString().replace("-", "").take(12)
            repository.completePaymentAndEnroll(order, user.id, paymentId, "FAILED")
            _paymentLoading.value = false
            _message.value = "Payment Failed: $reason. Test series remains locked."
            navigateBack()
        }
    }

    fun cancelRazorpayPayment(order: RazorpayOrder) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val paymentId = "pay_canc_" + java.util.UUID.randomUUID().toString().replace("-", "").take(12)
            repository.completePaymentAndEnroll(order, user.id, paymentId, "CANCELLED")
            _message.value = "Payment Cancelled. Test series remains locked."
            navigateBack()
        }
    }

    // --- Test Engine Operations ---
    private fun startTimer(durationSeconds: Long) {
        examTimer?.cancel()
        examTimer = object : CountDownTimer(durationSeconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val remaining = millisUntilFinished / 1000L
                _testEngineState.value = _testEngineState.value.copy(remainingSeconds = remaining)
            }

            override fun onFinish() {
                _testEngineState.value = _testEngineState.value.copy(remainingSeconds = 0)
                // Auto-submit when countdown timer reaches zero
                submitTest(autoTriggered = true)
            }
        }.start()
    }

    fun selectOption(questionId: String, option: String) {
        val updatedAnswers = _testEngineState.value.userAnswers.toMutableMap()
        updatedAnswers[questionId] = option
        _testEngineState.value = _testEngineState.value.copy(userAnswers = updatedAnswers)
    }

    fun clearAnswer(questionId: String) {
        val updatedAnswers = _testEngineState.value.userAnswers.toMutableMap()
        updatedAnswers.remove(questionId)
        _testEngineState.value = _testEngineState.value.copy(userAnswers = updatedAnswers)
    }

    fun toggleMarkForReview(questionId: String) {
        val set = _testEngineState.value.markedForReview.toMutableSet()
        if (set.contains(questionId)) {
            set.remove(questionId)
        } else {
            set.add(questionId)
        }
        _testEngineState.value = _testEngineState.value.copy(markedForReview = set)
    }

    fun toggleReview(questionId: String) = toggleMarkForReview(questionId)

    fun goToQuestion(index: Int) {
        if (index in 0 until _testEngineState.value.questions.size) {
            _testEngineState.value = _testEngineState.value.copy(
                currentQuestionIndex = index,
                showPaletteDrawer = false
            )
        }
    }

    fun nextQuestion() {
        val next = _testEngineState.value.currentQuestionIndex + 1
        if (next < _testEngineState.value.questions.size) {
            _testEngineState.value = _testEngineState.value.copy(currentQuestionIndex = next)
        }
    }

    fun prevQuestion() {
        val prev = _testEngineState.value.currentQuestionIndex - 1
        if (prev >= 0) {
            _testEngineState.value = _testEngineState.value.copy(currentQuestionIndex = prev)
        }
    }

    fun togglePalette(show: Boolean) {
        _testEngineState.value = _testEngineState.value.copy(showPaletteDrawer = show)
    }

    fun toggleSubmitConfirm(show: Boolean) {
        _testEngineState.value = _testEngineState.value.copy(showSubmitConfirmDialog = show)
    }

    // Basic Anti-Cheating violation registration
    fun registerProctoringViolation() {
        val state = _testEngineState.value
        if (state.isSubmitted || state.test == null) return

        val newCount = state.violationCount + 1
        if (newCount >= 3) {
            // Maximum violations reached: auto submit
            _testEngineState.value = state.copy(
                violationCount = newCount,
                showViolationDialog = true
            )
            submitTest(autoTriggered = true)
        } else {
            _testEngineState.value = state.copy(
                violationCount = newCount,
                showViolationDialog = true
            )
        }
    }

    fun dismissViolationDialog() {
        _testEngineState.value = _testEngineState.value.copy(showViolationDialog = false)
    }

    fun submitTest(autoTriggered: Boolean = false) {
        val state = _testEngineState.value
        val user = _currentUser.value ?: return
        val test = state.test ?: return
        if (state.isSubmitted) return

        examTimer?.cancel()
        examTimer = null

        viewModelScope.launch {
            val timeTaken = state.totalSeconds - state.remainingSeconds
            val attempt = repository.submitTestAttempt(
                userId = user.id,
                testId = test.id,
                userAnswers = state.userAnswers,
                timeTakenSeconds = maxOf(1L, timeTaken),
                violationCount = state.violationCount
            )

            _testEngineState.value = state.copy(
                isSubmitted = true,
                showSubmitConfirmDialog = false,
                submittedAttemptId = attempt.id
            )

            // Reload user attempts
            loadUserData(user.id)

            // Direct to results
            _currentScreen.value = Screen.TestResult(attempt.id)
        }
    }

    // --- Admin Operations ---
    fun toggleStudentStatus(student: UserEntity) {
        viewModelScope.launch {
            repository.toggleStudentSuspension(student)
            refreshAdminMetrics()
        }
    }

    fun saveTestSeries(series: TestSeriesEntity) {
        viewModelScope.launch {
            repository.saveTestSeries(series)
            refreshAdminMetrics()
            _message.value = "Test Series saved successfully."
        }
    }

    fun deleteTestSeries(id: String) {
        viewModelScope.launch {
            repository.deleteTestSeries(id)
            refreshAdminMetrics()
            _message.value = "Test Series deleted."
        }
    }

    fun saveTest(test: TestEntity) {
        viewModelScope.launch {
            repository.saveTest(test)
            refreshAdminMetrics()
            _message.value = "Test saved successfully."
        }
    }

    fun deleteTest(id: String) {
        viewModelScope.launch {
            repository.deleteTest(id)
            refreshAdminMetrics()
            _message.value = "Test deleted."
        }
    }

    fun saveQuestion(question: QuestionEntity) {
        viewModelScope.launch {
            repository.saveQuestion(question)
            _message.value = "Question added to Question Bank."
        }
    }

    fun deleteQuestion(questionId: String) {
        viewModelScope.launch {
            repository.deleteQuestion(questionId)
            _message.value = "Question deleted."
        }
    }

    override fun onCleared() {
        super.onCleared()
        examTimer?.cancel()
    }
}

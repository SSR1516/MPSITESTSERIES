package com.example.data.repository

import com.example.data.AppDatabase
import com.example.data.DatabaseHelper
import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

sealed class AccessCheckResult {
    data class Allowed(
        val test: TestEntity,
        val series: TestSeriesEntity,
        val remainingAttempts: Int
    ) : AccessCheckResult()
    data class Denied(val reason: String, val canPurchase: Boolean = false, val seriesId: String? = null) : AccessCheckResult()
}

data class RazorpayOrder(
    val orderId: String,
    val seriesId: String,
    val seriesTitle: String,
    val amountRupees: Double,
    val amountPaise: Long,
    val studentName: String,
    val studentEmail: String,
    val studentMobile: String,
    val paymentUrl: String = "https://rzp.io/rzp/gQC02wc"
)

data class SubjectPerformance(
    val subject: String,
    val totalAnswered: Int,
    val correctCount: Int,
    val accuracy: Double
)

class MpsiRepository(
    private val database: AppDatabase,
    private val databaseHelper: DatabaseHelper? = null
) {

    // --- Authentication ---
    suspend fun registerStudent(
        fullName: String,
        mobile: String,
        email: String,
        password: String
    ): AuthResult = withContext(Dispatchers.IO) {
        if (fullName.isBlank() || mobile.isBlank() || email.isBlank() || password.isBlank()) {
            return@withContext AuthResult.Error("All fields are required")
        }
        val existing = database.userDao().getUserByEmail(email)
        if (existing != null) {
            return@withContext AuthResult.Error("An account with this email already exists")
        }
        val newUser = UserEntity(
            id = "user_" + UUID.randomUUID().toString().take(8),
            fullName = fullName.trim(),
            mobile = mobile.trim(),
            email = email.trim().lowercase(),
            passwordHash = password, // For MVP / sandbox
            role = "STUDENT"
        )
        database.userDao().insertUser(newUser)
        try { databaseHelper?.saveUser(newUser) } catch (_: Exception) {}
        AuthResult.Success(newUser)
    }

    suspend fun login(email: String, password: String): AuthResult = withContext(Dispatchers.IO) {
        val user = database.userDao().getUserByEmail(email.trim().lowercase())
            ?: return@withContext AuthResult.Error("Invalid email or password")
        if (user.passwordHash != password) {
            return@withContext AuthResult.Error("Invalid email or password")
        }
        if (user.isSuspended) {
            return@withContext AuthResult.Error("Your account has been suspended by the administrator")
        }
        AuthResult.Success(user)
    }

    suspend fun getUserById(userId: String): UserEntity? = withContext(Dispatchers.IO) {
        database.userDao().getUserByIdOnce(userId)
    }

    fun getAllStudents(): Flow<List<UserEntity>> = database.userDao().getAllStudents()

    suspend fun toggleStudentSuspension(user: UserEntity) = withContext(Dispatchers.IO) {
        database.userDao().updateUser(user.copy(isSuspended = !user.isSuspended))
    }

    // --- Test Series ---
    fun getPublishedTestSeries(): Flow<List<TestSeriesEntity>> = database.testSeriesDao().getPublishedTestSeries()
    fun getAllTestSeries(): Flow<List<TestSeriesEntity>> = database.testSeriesDao().getAllTestSeries()
    fun getSeriesById(seriesId: String): Flow<TestSeriesEntity?> = database.testSeriesDao().getSeriesById(seriesId)

    suspend fun saveTestSeries(series: TestSeriesEntity) = withContext(Dispatchers.IO) {
        database.testSeriesDao().insertSeries(series)
        try { databaseHelper?.saveTestSeries(series) } catch (_: Exception) {}
    }

    suspend fun deleteTestSeries(seriesId: String) = withContext(Dispatchers.IO) {
        database.testSeriesDao().deleteSeriesById(seriesId)
    }

    // --- Tests ---
    fun getPublishedTestsForSeries(seriesId: String): Flow<List<TestEntity>> =
        database.testDao().getPublishedTestsForSeries(seriesId)

    fun getAllTests(): Flow<List<TestEntity>> = database.testDao().getAllTests()

    fun getTestById(testId: String): Flow<TestEntity?> = database.testDao().getTestById(testId)

    suspend fun saveTest(test: TestEntity) = withContext(Dispatchers.IO) {
        database.testDao().insertTest(test)
        try { databaseHelper?.saveTest(test) } catch (_: Exception) {}
    }

    suspend fun deleteTest(testId: String) = withContext(Dispatchers.IO) {
        database.testDao().deleteTestById(testId)
    }

    // --- Questions ---
    fun getQuestionsForTest(testId: String): Flow<List<QuestionEntity>> =
        database.questionDao().getQuestionsForTest(testId)

    suspend fun getQuestionsForTestOnce(testId: String): List<QuestionEntity> = withContext(Dispatchers.IO) {
        database.questionDao().getQuestionsForTestOnce(testId)
    }

    suspend fun saveQuestion(question: QuestionEntity) = withContext(Dispatchers.IO) {
        database.questionDao().insertQuestion(question)
        try { databaseHelper?.saveQuestion(question) } catch (_: Exception) {}
    }

    suspend fun deleteQuestion(questionId: String) = withContext(Dispatchers.IO) {
        database.questionDao().deleteQuestionById(questionId)
    }

    // --- Secure Test Access Control ---
    suspend fun verifyTestAccess(userId: String, testId: String): AccessCheckResult = withContext(Dispatchers.IO) {
        val user = database.userDao().getUserByIdOnce(userId)
            ?: return@withContext AccessCheckResult.Denied("User not authenticated.")

        if (user.isSuspended) {
            return@withContext AccessCheckResult.Denied("Your student account is suspended.")
        }

        val test = database.testDao().getTestByIdOnce(testId)
            ?: return@withContext AccessCheckResult.Denied("Test not found.")

        // Admins have unrestricted access for test review
        if (user.role == "ADMIN") {
            val series = database.testSeriesDao().getSeriesByIdOnce(test.seriesId)
                ?: TestSeriesEntity(
                    id = test.seriesId,
                    title = "General Series",
                    description = "",
                    numberOfTests = 1,
                    questionsPerTest = 10,
                    durationMinutes = 60,
                    totalMarks = 10
                )
            return@withContext AccessCheckResult.Allowed(test, series, remainingAttempts = 999)
        }

        if (!test.isPublished) {
            return@withContext AccessCheckResult.Denied("This test is currently unpublished by the board.")
        }

        val series = database.testSeriesDao().getSeriesByIdOnce(test.seriesId)
            ?: return@withContext AccessCheckResult.Denied("Associated test series not found.")

        if (!series.isPublished) {
            return@withContext AccessCheckResult.Denied("Test series is not currently active.")
        }

        // Check enrollment & payment
        val enrollment = database.enrollmentDao().getActiveEnrollment(userId, series.id)
        if (enrollment == null) {
            return@withContext AccessCheckResult.Denied(
                reason = "You have not purchased '${series.title}'. Please enroll to access this test.",
                canPurchase = true,
                seriesId = series.id
            )
        }

        if (enrollment.expiryDate < System.currentTimeMillis()) {
            return@withContext AccessCheckResult.Denied(
                reason = "Your enrollment for this test series has expired.",
                canPurchase = true,
                seriesId = series.id
            )
        }

        // Check attempts limit
        val priorAttempts = database.attemptDao().countAttempts(userId, testId)
        if (priorAttempts >= test.maxAttempts) {
            return@withContext AccessCheckResult.Denied(
                reason = "Maximum attempt limit of ${test.maxAttempts} reached for this test."
            )
        }

        val remainingAttempts = test.maxAttempts - priorAttempts
        AccessCheckResult.Allowed(test, series, remainingAttempts)
    }

    // --- Payment & Razorpay Architecture ---
    suspend fun createRazorpayOrder(userId: String, seriesId: String): RazorpayOrder = withContext(Dispatchers.IO) {
        val user = database.userDao().getUserByIdOnce(userId) ?: error("User not found")
        val series = database.testSeriesDao().getSeriesByIdOnce(seriesId) ?: error("Series not found")
        val orderId = "order_mpsi_" + UUID.randomUUID().toString().replace("-", "").take(14)
        val amountPaise = (series.price * 100).toLong()

        RazorpayOrder(
            orderId = orderId,
            seriesId = series.id,
            seriesTitle = series.title,
            amountRupees = series.price,
            amountPaise = amountPaise,
            studentName = user.fullName,
            studentEmail = user.email,
            studentMobile = user.mobile,
            paymentUrl = "https://rzp.io/rzp/gQC02wc"
        )
    }

    suspend fun completePaymentAndEnroll(
        order: RazorpayOrder,
        userId: String,
        paymentId: String,
        status: String
    ): Boolean = withContext(Dispatchers.IO) {
        val paymentRecord = PaymentEntity(
            id = "pay_" + UUID.randomUUID().toString().take(10),
            userId = userId,
            studentName = order.studentName,
            seriesId = order.seriesId,
            seriesTitle = order.seriesTitle,
            amount = order.amountRupees,
            currency = "INR",
            razorpayOrderId = order.orderId,
            razorpayPaymentId = paymentId,
            status = status
        )
        database.paymentDao().insertPayment(paymentRecord)
        try { databaseHelper?.savePayment(paymentRecord) } catch (_: Exception) {}

        if (status == "PAID") {
            val series = database.testSeriesDao().getSeriesByIdOnce(order.seriesId)
            val validityDays = series?.validityDays ?: 180
            val expiry = System.currentTimeMillis() + (validityDays.toLong() * 24 * 60 * 60 * 1000)
            val enrollment = EnrollmentEntity(
                id = "enr_" + UUID.randomUUID().toString().take(10),
                userId = userId,
                seriesId = order.seriesId,
                paymentId = paymentRecord.id,
                status = "ACTIVE",
                purchaseDate = System.currentTimeMillis(),
                expiryDate = expiry
            )
            database.enrollmentDao().insertEnrollment(enrollment)
            try { databaseHelper?.saveEnrollment(enrollment) } catch (_: Exception) {}
            true
        } else {
            false
        }
    }

    fun getEnrollmentsForUser(userId: String): Flow<List<EnrollmentEntity>> =
        database.enrollmentDao().getEnrollmentsForUser(userId)

    suspend fun isEnrolledInSeries(userId: String, seriesId: String): Boolean = withContext(Dispatchers.IO) {
        val enrollment = database.enrollmentDao().getActiveEnrollment(userId, seriesId)
        enrollment != null && enrollment.expiryDate > System.currentTimeMillis()
    }

    suspend fun getSeriesByIdOnce(seriesId: String): TestSeriesEntity? = withContext(Dispatchers.IO) {
        database.testSeriesDao().getSeriesByIdOnce(seriesId)
    }

    fun getPaymentsForUser(userId: String): Flow<List<PaymentEntity>> =
        database.paymentDao().getPaymentsForUser(userId)

    fun getAllPayments(): Flow<List<PaymentEntity>> = database.paymentDao().getAllPayments()

    // --- Test Submission & Results Engine ---
    suspend fun submitTestAttempt(
        userId: String,
        testId: String,
        userAnswers: Map<String, String>, // questionId -> selected Option ("A", "B", "C", "D")
        timeTakenSeconds: Long,
        violationCount: Int
    ): AttemptEntity = withContext(Dispatchers.IO) {
        val test = database.testDao().getTestByIdOnce(testId) ?: error("Test not found")
        val questions = database.questionDao().getQuestionsForTestOnce(testId)

        var correctCount = 0
        var wrongCount = 0
        var attemptedCount = 0

        for (q in questions) {
            val selected = userAnswers[q.id]
            if (!selected.isNullOrBlank()) {
                attemptedCount++
                if (selected.equals(q.correctAnswer, ignoreCase = true)) {
                    correctCount++
                } else {
                    wrongCount++
                }
            }
        }

        val unattemptedCount = questions.size - attemptedCount
        val marksEarned = (correctCount * test.marksPerQuestion) - (wrongCount * test.negativeMarks)
        val finalMarks = maxOf(0.0, marksEarned)
        val percentage = if (test.totalMarks > 0) (finalMarks / test.totalMarks) * 100 else 0.0
        val accuracy = if (attemptedCount > 0) (correctCount.toDouble() / attemptedCount) * 100 else 0.0

        val encodedAnswers = userAnswers.entries.joinToString("|") { "${it.key}:${it.value}" }

        val attempt = AttemptEntity(
            id = "att_" + UUID.randomUUID().toString().take(10),
            userId = userId,
            testId = testId,
            testTitle = test.title,
            seriesId = test.seriesId,
            totalQuestions = questions.size,
            attempted = attemptedCount,
            unattempted = unattemptedCount,
            correct = correctCount,
            wrong = wrongCount,
            obtainedMarks = finalMarks,
            totalMarks = test.totalMarks.toDouble(),
            percentage = percentage,
            accuracy = accuracy,
            timeTakenSeconds = timeTakenSeconds,
            userAnswersJson = encodedAnswers,
            violationCount = violationCount,
            submittedAt = System.currentTimeMillis()
        )

        database.attemptDao().insertAttempt(attempt)
        try { databaseHelper?.saveAttempt(attempt) } catch (_: Exception) {}
        attempt
    }

    fun getAttemptsForUser(userId: String): Flow<List<AttemptEntity>> =
        database.attemptDao().getAttemptsForUser(userId)

    fun getAttemptById(attemptId: String): Flow<AttemptEntity?> =
        database.attemptDao().getAttemptById(attemptId)

    suspend fun getAttemptByIdOnce(attemptId: String): AttemptEntity? = withContext(Dispatchers.IO) {
        database.attemptDao().getAttemptByIdOnce(attemptId)
    }

    fun getAllAttempts(): Flow<List<AttemptEntity>> = database.attemptDao().getAllAttempts()

    // --- Admin Dashboard Metrics ---
    suspend fun getAdminMetrics(): AdminMetrics = withContext(Dispatchers.IO) {
        val totalStudents = database.userDao().countStudents()
        val paidStudents = database.enrollmentDao().countPaidStudents()
        val totalSeries = database.testSeriesDao().countSeries()
        val totalTests = database.testDao().countTests()
        val totalAttempts = database.attemptDao().countTotalAttempts()
        val revenue = database.paymentDao().getTotalRevenue() ?: 0.0
        AdminMetrics(
            totalStudents = totalStudents,
            paidStudents = paidStudents,
            totalSeries = totalSeries,
            totalTests = totalTests,
            totalAttempts = totalAttempts,
            totalRevenue = revenue
        )
    }
}

data class AdminMetrics(
    val totalStudents: Int,
    val paidStudents: Int,
    val totalSeries: Int,
    val totalTests: Int,
    val totalAttempts: Int,
    val totalRevenue: Double
)

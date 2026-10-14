package com.example.data

import android.content.Context
import android.util.Log
import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * DatabaseHelper initializes and manages Google Cloud Firestore integration
 * for MPSI Test Series: managing users, tests, payments, enrollments, and attempts.
 * Supports seamless fallback to local storage when Firebase backend is not configured.
 */
class DatabaseHelper private constructor(context: Context) {

    val firestore: FirebaseFirestore? by lazy {
        try {
            val app = if (FirebaseApp.getApps(context).isNotEmpty()) {
                FirebaseApp.getInstance()
            } else {
                try {
                    FirebaseApp.initializeApp(context)
                } catch (t: Throwable) {
                    null
                }
            }

            if (app == null) {
                Log.d(TAG, "FirebaseApp is not configured in this build. Running in local persistence mode.")
                return@lazy null
            }

            val dbIdRes = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
            if (dbIdRes != 0) {
                val dbId = context.getString(dbIdRes)
                FirebaseFirestore.getInstance(app, dbId)
            } else {
                FirebaseFirestore.getInstance(app)
            }
        } catch (t: Throwable) {
            Log.d(TAG, "Firestore initialization deferred: ${t.message}")
            null
        }
    }

    // ==========================================
    // 1. USER MANAGEMENT
    // ==========================================

    suspend fun saveUser(user: UserEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val userMap = hashMapOf(
                "id" to user.id,
                "fullName" to user.fullName,
                "mobile" to user.mobile,
                "email" to user.email,
                "role" to user.role,
                "isSuspended" to user.isSuspended,
                "createdAt" to user.createdAt
            )
            db.collection("users").document(user.id)
                .set(userMap, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore user sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    fun observeUser(userId: String): Flow<UserEntity?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }
        val listener = db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user: $userId", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val user = UserEntity(
                        id = snapshot.getString("id") ?: userId,
                        fullName = snapshot.getString("fullName") ?: "",
                        mobile = snapshot.getString("mobile") ?: "",
                        email = snapshot.getString("email") ?: "",
                        passwordHash = "", // Passwords not stored in cloud document
                        role = snapshot.getString("role") ?: "STUDENT",
                        isSuspended = snapshot.getBoolean("isSuspended") ?: false,
                        createdAt = snapshot.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                    trySend(user)
                } else {
                    trySend(null)
                }
            }
        awaitClose { listener.remove() }
    }

    fun observeAllStudents(): Flow<List<UserEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("users")
            .whereEqualTo("role", "STUDENT")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for students", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    UserEntity(
                        id = doc.getString("id") ?: doc.id,
                        fullName = doc.getString("fullName") ?: "",
                        mobile = doc.getString("mobile") ?: "",
                        email = doc.getString("email") ?: "",
                        passwordHash = "",
                        role = "STUDENT",
                        isSuspended = doc.getBoolean("isSuspended") ?: false,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    // ==========================================
    // 2. TEST SERIES & TESTS MANAGEMENT
    // ==========================================

    suspend fun saveTestSeries(series: TestSeriesEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val map = hashMapOf(
                "id" to series.id,
                "title" to series.title,
                "exam" to series.exam,
                "description" to series.description,
                "numberOfTests" to series.numberOfTests,
                "questionsPerTest" to series.questionsPerTest,
                "durationMinutes" to series.durationMinutes,
                "totalMarks" to series.totalMarks,
                "negativeMarking" to series.negativeMarking,
                "validityDays" to series.validityDays,
                "price" to series.price,
                "originalPrice" to series.originalPrice,
                "isPublished" to series.isPublished,
                "createdAt" to series.createdAt
            )
            db.collection("test_series").document(series.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore test series sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    fun observePublishedTestSeries(): Flow<List<TestSeriesEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("test_series")
            .whereEqualTo("isPublished", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for published test series", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    TestSeriesEntity(
                        id = doc.getString("id") ?: doc.id,
                        title = doc.getString("title") ?: "",
                        exam = doc.getString("exam") ?: "Madhya Pradesh Sub-Inspector (MPSI)",
                        description = doc.getString("description") ?: "",
                        numberOfTests = (doc.getLong("numberOfTests") ?: 10).toInt(),
                        questionsPerTest = (doc.getLong("questionsPerTest") ?: 100).toInt(),
                        durationMinutes = (doc.getLong("durationMinutes") ?: 120).toInt(),
                        totalMarks = (doc.getLong("totalMarks") ?: 100).toInt(),
                        negativeMarking = doc.getDouble("negativeMarking") ?: 0.25,
                        validityDays = (doc.getLong("validityDays") ?: 180).toInt(),
                        price = doc.getDouble("price") ?: 100.0,
                        originalPrice = doc.getDouble("originalPrice") ?: 499.0,
                        isPublished = doc.getBoolean("isPublished") ?: true,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveTest(test: TestEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val map = hashMapOf(
                "id" to test.id,
                "seriesId" to test.seriesId,
                "title" to test.title,
                "description" to test.description,
                "durationMinutes" to test.durationMinutes,
                "totalMarks" to test.totalMarks,
                "marksPerQuestion" to test.marksPerQuestion,
                "negativeMarks" to test.negativeMarks,
                "maxAttempts" to test.maxAttempts,
                "isPublished" to test.isPublished,
                "isAnswerKeyVisible" to test.isAnswerKeyVisible,
                "createdAt" to test.createdAt
            )
            db.collection("tests").document(test.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore test sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    fun observeTestsForSeries(seriesId: String): Flow<List<TestEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("tests")
            .whereEqualTo("seriesId", seriesId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for tests in series $seriesId", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    TestEntity(
                        id = doc.getString("id") ?: doc.id,
                        seriesId = doc.getString("seriesId") ?: seriesId,
                        title = doc.getString("title") ?: "",
                        description = doc.getString("description") ?: "",
                        durationMinutes = (doc.getLong("durationMinutes") ?: 15).toInt(),
                        totalMarks = (doc.getLong("totalMarks") ?: 10).toInt(),
                        marksPerQuestion = doc.getDouble("marksPerQuestion") ?: 1.0,
                        negativeMarks = doc.getDouble("negativeMarks") ?: 0.25,
                        maxAttempts = (doc.getLong("maxAttempts") ?: 5).toInt(),
                        isPublished = doc.getBoolean("isPublished") ?: true,
                        isAnswerKeyVisible = doc.getBoolean("isAnswerKeyVisible") ?: true,
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveQuestion(question: QuestionEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val map = hashMapOf(
                "id" to question.id,
                "testId" to question.testId,
                "questionNumber" to question.questionNumber,
                "questionText" to question.questionText,
                "optionA" to question.optionA,
                "optionB" to question.optionB,
                "optionC" to question.optionC,
                "optionD" to question.optionD,
                "correctAnswer" to question.correctAnswer,
                "explanation" to question.explanation,
                "subject" to question.subject,
                "topic" to question.topic,
                "difficulty" to question.difficulty
            )
            db.collection("tests").document(question.testId)
                .collection("questions").document(question.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore question sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    suspend fun getQuestionsForTest(testId: String): List<QuestionEntity> {
        val db = firestore ?: return emptyList()
        return try {
            val snapshot = db.collection("tests").document(testId)
                .collection("questions")
                .orderBy("questionNumber")
                .get()
                .await()
            snapshot.documents.mapNotNull { doc ->
                QuestionEntity(
                    id = doc.getString("id") ?: doc.id,
                    testId = doc.getString("testId") ?: testId,
                    questionNumber = (doc.getLong("questionNumber") ?: 1).toInt(),
                    questionText = doc.getString("questionText") ?: "",
                    optionA = doc.getString("optionA") ?: "",
                    optionB = doc.getString("optionB") ?: "",
                    optionC = doc.getString("optionC") ?: "",
                    optionD = doc.getString("optionD") ?: "",
                    correctAnswer = doc.getString("correctAnswer") ?: "A",
                    explanation = doc.getString("explanation") ?: "",
                    subject = doc.getString("subject") ?: "General Knowledge",
                    topic = doc.getString("topic") ?: "",
                    difficulty = doc.getString("difficulty") ?: "Medium"
                )
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firestore questions fetch deferred: ${e.message}")
            emptyList()
        }
    }

    // ==========================================
    // 3. PAYMENTS & ENROLLMENTS MANAGEMENT
    // ==========================================

    suspend fun savePayment(payment: PaymentEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val map = hashMapOf(
                "id" to payment.id,
                "userId" to payment.userId,
                "studentName" to payment.studentName,
                "seriesId" to payment.seriesId,
                "seriesTitle" to payment.seriesTitle,
                "amount" to payment.amount,
                "currency" to payment.currency,
                "razorpayOrderId" to payment.razorpayOrderId,
                "razorpayPaymentId" to payment.razorpayPaymentId,
                "status" to payment.status,
                "createdAt" to payment.createdAt
            )
            db.collection("payments").document(payment.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore payment sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    fun observePaymentsForUser(userId: String): Flow<List<PaymentEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("payments")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user payments", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    PaymentEntity(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: userId,
                        studentName = doc.getString("studentName") ?: "",
                        seriesId = doc.getString("seriesId") ?: "",
                        seriesTitle = doc.getString("seriesTitle") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        currency = doc.getString("currency") ?: "INR",
                        razorpayOrderId = doc.getString("razorpayOrderId") ?: "",
                        razorpayPaymentId = doc.getString("razorpayPaymentId") ?: "",
                        status = doc.getString("status") ?: "PAID",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    fun observeAllPayments(): Flow<List<PaymentEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("payments")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for all payments", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    PaymentEntity(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: "",
                        studentName = doc.getString("studentName") ?: "",
                        seriesId = doc.getString("seriesId") ?: "",
                        seriesTitle = doc.getString("seriesTitle") ?: "",
                        amount = doc.getDouble("amount") ?: 0.0,
                        currency = doc.getString("currency") ?: "INR",
                        razorpayOrderId = doc.getString("razorpayOrderId") ?: "",
                        razorpayPaymentId = doc.getString("razorpayPaymentId") ?: "",
                        status = doc.getString("status") ?: "PAID",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    suspend fun saveEnrollment(enrollment: EnrollmentEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val map = hashMapOf(
                "id" to enrollment.id,
                "userId" to enrollment.userId,
                "seriesId" to enrollment.seriesId,
                "paymentId" to enrollment.paymentId,
                "status" to enrollment.status,
                "purchaseDate" to enrollment.purchaseDate,
                "expiryDate" to enrollment.expiryDate
            )
            db.collection("enrollments").document(enrollment.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore enrollment sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    // ==========================================
    // 4. ATTEMPTS & TEST RESULTS
    // ==========================================

    suspend fun saveAttempt(attempt: AttemptEntity): Result<Unit> {
        val db = firestore ?: return Result.success(Unit)
        return try {
            val map = hashMapOf(
                "id" to attempt.id,
                "userId" to attempt.userId,
                "testId" to attempt.testId,
                "testTitle" to attempt.testTitle,
                "seriesId" to attempt.seriesId,
                "totalQuestions" to attempt.totalQuestions,
                "attempted" to attempt.attempted,
                "unattempted" to attempt.unattempted,
                "correct" to attempt.correct,
                "wrong" to attempt.wrong,
                "obtainedMarks" to attempt.obtainedMarks,
                "totalMarks" to attempt.totalMarks,
                "percentage" to attempt.percentage,
                "accuracy" to attempt.accuracy,
                "timeTakenSeconds" to attempt.timeTakenSeconds,
                "userAnswersJson" to attempt.userAnswersJson,
                "violationCount" to attempt.violationCount,
                "submittedAt" to attempt.submittedAt
            )
            db.collection("attempts").document(attempt.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.w(TAG, "Firestore attempt sync deferred: ${e.message}")
            Result.success(Unit)
        }
    }

    fun observeAttemptsForUser(userId: String): Flow<List<AttemptEntity>> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener = db.collection("attempts")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user attempts", error)
                    return@addSnapshotListener
                }
                val list = snapshot?.documents?.mapNotNull { doc ->
                    AttemptEntity(
                        id = doc.getString("id") ?: doc.id,
                        userId = doc.getString("userId") ?: userId,
                        testId = doc.getString("testId") ?: "",
                        testTitle = doc.getString("testTitle") ?: "",
                        seriesId = doc.getString("seriesId") ?: "",
                        totalQuestions = (doc.getLong("totalQuestions") ?: 0).toInt(),
                        attempted = (doc.getLong("attempted") ?: 0).toInt(),
                        unattempted = (doc.getLong("unattempted") ?: 0).toInt(),
                        correct = (doc.getLong("correct") ?: 0).toInt(),
                        wrong = (doc.getLong("wrong") ?: 0).toInt(),
                        obtainedMarks = doc.getDouble("obtainedMarks") ?: 0.0,
                        totalMarks = doc.getDouble("totalMarks") ?: 0.0,
                        percentage = doc.getDouble("percentage") ?: 0.0,
                        accuracy = doc.getDouble("accuracy") ?: 0.0,
                        timeTakenSeconds = doc.getLong("timeTakenSeconds") ?: 0L,
                        userAnswersJson = doc.getString("userAnswersJson") ?: "",
                        violationCount = (doc.getLong("violationCount") ?: 0).toInt(),
                        submittedAt = doc.getLong("submittedAt") ?: System.currentTimeMillis()
                    )
                } ?: emptyList()
                trySend(list)
            }
        awaitClose { listener.remove() }
    }

    companion object {
        private const val TAG = "DatabaseHelper"

        @Volatile
        private var INSTANCE: DatabaseHelper? = null

        fun getInstance(context: Context): DatabaseHelper {
            return INSTANCE ?: synchronized(this) {
                val instance = DatabaseHelper(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}

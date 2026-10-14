package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val mobile: String,
    val email: String,
    val passwordHash: String,
    val role: String, // "STUDENT" or "ADMIN"
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_series")
data class TestSeriesEntity(
    @PrimaryKey val id: String,
    val title: String,
    val exam: String = "Madhya Pradesh Sub-Inspector (MPSI)",
    val description: String,
    val numberOfTests: Int,
    val questionsPerTest: Int,
    val durationMinutes: Int,
    val totalMarks: Int,
    val negativeMarking: Double = 0.25,
    val validityDays: Int = 180,
    val price: Double = 100.0,
    val originalPrice: Double = 499.0,
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tests")
data class TestEntity(
    @PrimaryKey val id: String,
    val seriesId: String,
    val title: String,
    val description: String,
    val durationMinutes: Int = 15,
    val totalMarks: Int = 10,
    val marksPerQuestion: Double = 1.0,
    val negativeMarks: Double = 0.25,
    val maxAttempts: Int = 5,
    val isPublished: Boolean = true,
    val isAnswerKeyVisible: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: String,
    val testId: String,
    val questionNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctAnswer: String, // "A", "B", "C", "D"
    val explanation: String,
    val subject: String,
    val topic: String,
    val difficulty: String // "Easy", "Medium", "Hard"
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val studentName: String,
    val seriesId: String,
    val seriesTitle: String,
    val amount: Double,
    val currency: String = "INR",
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val status: String, // "PAID", "PENDING", "FAILED", "REFUNDED"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "enrollments")
data class EnrollmentEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val seriesId: String,
    val paymentId: String,
    val status: String = "ACTIVE", // "ACTIVE", "EXPIRED"
    val purchaseDate: Long = System.currentTimeMillis(),
    val expiryDate: Long = System.currentTimeMillis() + (180L * 24 * 60 * 60 * 1000)
)

@Entity(tableName = "attempts")
data class AttemptEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val testId: String,
    val testTitle: String,
    val seriesId: String,
    val totalQuestions: Int,
    val attempted: Int,
    val unattempted: Int,
    val correct: Int,
    val wrong: Int,
    val obtainedMarks: Double,
    val totalMarks: Double,
    val percentage: Double,
    val accuracy: Double,
    val timeTakenSeconds: Long,
    val userAnswersJson: String, // format: "qId:opt|qId:opt"
    val violationCount: Int = 0,
    val submittedAt: Long = System.currentTimeMillis()
)

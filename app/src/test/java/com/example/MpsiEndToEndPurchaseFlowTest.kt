package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.DatabaseHelper
import com.example.data.MpsiQuestionBank
import com.example.data.SeedDataLoader
import com.example.data.repository.AccessCheckResult
import com.example.data.repository.AuthResult
import com.example.data.repository.MpsiRepository
import com.example.data.repository.RazorpayOrder
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.UUID

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class MpsiEndToEndPurchaseFlowTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var repository: MpsiRepository

    @Before
    fun setUp() = runBlocking {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        SeedDataLoader.populateInitialData(database)

        val databaseHelper = DatabaseHelper.getInstance(context)
        repository = MpsiRepository(database, databaseHelper)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testCompleteEndToEndMpsiPurchaseFlowAnd100QuestionTestEngine() = runBlocking {
        // =========================================================================
        // STEP 1: Create/login as a demo student
        // =========================================================================
        val authResult = repository.login("student@mpsi.com", "student123")
        assertTrue("Student login must succeed", authResult is AuthResult.Success)
        val student = (authResult as AuthResult.Success).user
        assertEquals("student_01", student.id)
        assertEquals("STUDENT", student.role)
        println("STEP 1 PASSED: Successfully logged in as demo student: ${student.fullName}")

        // =========================================================================
        // STEP 2: Open MPSI Test Series
        // =========================================================================
        val series = repository.getSeriesByIdOnce("series_complete")
        assertNotNull("MPSI Complete series must exist", series)
        val completeSeries = series!!
        assertEquals("MPSI Complete Mock Test Series", completeSeries.title)
        println("STEP 2 PASSED: Opened MPSI Test Series: ${completeSeries.title}")

        // =========================================================================
        // STEP 3: Verify displayed price is ₹100
        // =========================================================================
        assertEquals("Series price must be exactly ₹100", 100.0, completeSeries.price, 0.001)
        println("STEP 3 PASSED: Verified displayed price is ₹100.0")

        // =========================================================================
        // STEP 4: Verify the series is LOCKED before purchase
        // =========================================================================
        val initialEnrollment = repository.isEnrolledInSeries(student.id, completeSeries.id)
        assertFalse("Series must NOT be enrolled before purchase", initialEnrollment)

        val accessBeforePurchase = repository.verifyTestAccess(student.id, "test_01")
        assertTrue("Test access must be DENIED before purchase", accessBeforePurchase is AccessCheckResult.Denied)
        val deniedReason = (accessBeforePurchase as AccessCheckResult.Denied).reason
        println("STEP 4 PASSED: Series is LOCKED before purchase. Access denied reason: $deniedReason")

        // =========================================================================
        // STEP 5: Click Buy Now (Create Razorpay Order for ₹100)
        // =========================================================================
        val razorpayOrder = repository.createRazorpayOrder(student.id, completeSeries.id)
        assertNotNull("Razorpay order must be created", razorpayOrder)
        assertEquals("Order amount must be ₹100", 100.0, razorpayOrder.amountRupees, 0.001)
        assertEquals("Order amount in paise must be 10000", 10000L, razorpayOrder.amountPaise)
        assertEquals(completeSeries.id, razorpayOrder.seriesId)
        assertEquals(student.fullName, razorpayOrder.studentName)
        assertEquals("https://rzp.io/rzp/gQC02wc", razorpayOrder.paymentUrl)
        println("STEP 5 PASSED: Razorpay order created for ₹100 (Order ID: ${razorpayOrder.orderId}, Payment URL: ${razorpayOrder.paymentUrl})")

        // =========================================================================
        // STEP 6: Complete Razorpay Sandbox/Development payment flow
        // =========================================================================
        val mockPaymentId = "pay_test_" + UUID.randomUUID().toString().take(10)
        val paymentSuccess = repository.completePaymentAndEnroll(
            order = razorpayOrder,
            userId = student.id,
            paymentId = mockPaymentId,
            status = "PAID"
        )
        assertTrue("Payment verification and enrollment must succeed", paymentSuccess)
        println("STEP 6 PASSED: Completed Razorpay Sandbox payment (Payment ID: $mockPaymentId)")

        // =========================================================================
        // STEP 7: Verify payment is recorded as PAID
        // =========================================================================
        val userPayments = repository.getPaymentsForUser(student.id).first()
        val recordedPayment = userPayments.find { it.razorpayPaymentId == mockPaymentId }
        assertNotNull("Payment must be saved in database", recordedPayment)
        assertEquals("PAID", recordedPayment!!.status)
        assertEquals(100.0, recordedPayment.amount, 0.001)
        assertEquals(completeSeries.id, recordedPayment.seriesId)
        println("STEP 7 PASSED: Payment record verified: status=${recordedPayment.status}, amount=₹${recordedPayment.amount}")

        // =========================================================================
        // STEP 8: Verify an ACTIVE enrollment is created
        // =========================================================================
        val userEnrollments = repository.getEnrollmentsForUser(student.id).first()
        val activeEnrollment = userEnrollments.find { it.seriesId == completeSeries.id }
        assertNotNull("Active enrollment must exist for series", activeEnrollment)
        assertEquals("ACTIVE", activeEnrollment!!.status)
        assertTrue("Enrollment expiry must be in the future", activeEnrollment.expiryDate > System.currentTimeMillis())
        println("STEP 8 PASSED: Verified ACTIVE enrollment for series: status=${activeEnrollment.status}")

        // =========================================================================
        // STEP 9: Verify the test series becomes UNLOCKED
        // =========================================================================
        val isEnrolledNow = repository.isEnrolledInSeries(student.id, completeSeries.id)
        assertTrue("User must now be reported as enrolled", isEnrolledNow)

        val accessAfterPurchase = repository.verifyTestAccess(student.id, "test_01")
        assertTrue("Test access must now be ALLOWED", accessAfterPurchase is AccessCheckResult.Allowed)
        println("STEP 9 PASSED: Test series is now UNLOCKED!")

        // =========================================================================
        // STEP 10: Start the test
        // =========================================================================
        val allowedResult = accessAfterPurchase as AccessCheckResult.Allowed
        assertEquals("test_01", allowedResult.test.id)
        val questions = repository.getQuestionsForTestOnce(allowedResult.test.id)
        assertNotNull(questions)
        println("STEP 10 PASSED: Test started successfully: ${allowedResult.test.title}")

        // =========================================================================
        // STEP 11: Verify the 100-question test engine works
        // =========================================================================
        assertEquals("Test must have exactly 100 questions for MPSI", 100, questions.size)
        // Check question distribution
        assertEquals(1, questions[0].questionNumber)
        assertEquals("MP Specific GK", questions[0].subject)
        assertEquals(50, questions[49].questionNumber)
        assertEquals(100, questions[99].questionNumber)
        assertEquals("Police Administration", questions[99].subject)
        println("STEP 11 PASSED: Verified 100-question test engine: Loaded all ${questions.size} questions spanning MP GK, Hindi, Maths, Science & Police Administration")

        // =========================================================================
        // STEP 12: Answer and submit the test
        // =========================================================================
        // Answer questions 1 to 80 (answer correctly based on key)
        val userAnswers = mutableMapOf<String, String>()
        for (i in 0 until 80) {
            val q = questions[i]
            // Answer accurately for first 70, intentionally choose wrong for next 10
            if (i < 70) {
                userAnswers[q.id] = q.correctAnswer
            } else {
                // Wrong answer
                userAnswers[q.id] = if (q.correctAnswer == "A") "B" else "A"
            }
        }
        // Remaining 20 questions left unattempted
        assertEquals(80, userAnswers.size)

        val attempt = repository.submitTestAttempt(
            userId = student.id,
            testId = "test_01",
            userAnswers = userAnswers,
            timeTakenSeconds = 1800L,
            violationCount = 0
        )
        assertNotNull(attempt)
        println("STEP 12 PASSED: Test submitted with 80 answers out of 100 questions")

        // =========================================================================
        // STEP 13: Verify the result is saved
        // =========================================================================
        val savedAttempt = repository.getAttemptByIdOnce(attempt.id)
        assertNotNull("Saved attempt must be retrieved from database", savedAttempt)
        assertEquals(100, savedAttempt!!.totalQuestions)
        assertEquals(80, savedAttempt.attempted)
        assertEquals(20, savedAttempt.unattempted)
        assertEquals(70, savedAttempt.correct)
        assertEquals(10, savedAttempt.wrong)
        // 70 correct (+70) - 10 wrong (-2.5) = 67.5 marks
        assertEquals(67.5, savedAttempt.obtainedMarks, 0.001)
        println("STEP 13 PASSED: Test result saved: Score = ${savedAttempt.obtainedMarks}/100, Correct = ${savedAttempt.correct}, Wrong = ${savedAttempt.wrong}, Skipped = ${savedAttempt.unattempted}")

        // =========================================================================
        // STEP 14: Verify answer key and explanations are available
        // =========================================================================
        val storedQuestions = repository.getQuestionsForTestOnce(savedAttempt.testId)
        assertTrue("Answer key must be visible for test", allowedResult.test.isAnswerKeyVisible)
        assertTrue("All 100 questions must have explanations", storedQuestions.all { it.explanation.isNotBlank() })
        assertTrue("All 100 questions must have correct answers", storedQuestions.all { it.correctAnswer.isNotBlank() })
        println("STEP 14 PASSED: Verified official answer key and explanations are available for all 100 questions")

        // =========================================================================
        // STEP 15: Login as admin
        // =========================================================================
        val adminAuth = repository.login("admin@mpsi.com", "admin123")
        assertTrue("Admin login must succeed", adminAuth is AuthResult.Success)
        val admin = (adminAuth as AuthResult.Success).user
        assertEquals("ADMIN", admin.role)
        println("STEP 15 PASSED: Logged in as Admin: ${admin.fullName}")

        // =========================================================================
        // STEP 16: Verify the payment appears in the admin payment ledger
        // =========================================================================
        val allPayments = repository.getAllPayments().first()
        val adminLedgerEntry = allPayments.find { it.razorpayPaymentId == mockPaymentId }
        assertNotNull("Student ₹100 payment must appear in Admin payment ledger", adminLedgerEntry)
        assertEquals("PAID", adminLedgerEntry!!.status)
        assertEquals(100.0, adminLedgerEntry.amount, 0.001)
        assertEquals("series_complete", adminLedgerEntry.seriesId)
        println("STEP 16 PASSED: Payment ledger entry verified in Admin panel: ID=${adminLedgerEntry.id}, ₹${adminLedgerEntry.amount}")

        // =========================================================================
        // STEP 17: Verify the ₹100 transaction appears in revenue
        // =========================================================================
        val adminMetrics = repository.getAdminMetrics()
        assertTrue("Total revenue must be at least ₹100", adminMetrics.totalRevenue >= 100.0)
        assertTrue("Paid students count must be at least 1", adminMetrics.paidStudents >= 1)
        println("STEP 17 PASSED: Verified revenue in Admin overview: Total Revenue = ₹${adminMetrics.totalRevenue}, Paid Students = ${adminMetrics.paidStudents}")

        // =========================================================================
        // STEP 18: Test a failed/cancelled payment and verify that the test remains LOCKED
        // =========================================================================
        // Create order for a different series (e.g. series_mpgk_special)
        val failedOrder = repository.createRazorpayOrder(student.id, "series_mpgk_special")
        assertEquals(100.0, failedOrder.amountRupees, 0.001)

        // 18A: Simulate Failed Payment
        val failPaymentId = "pay_fail_" + UUID.randomUUID().toString().take(10)
        val failResult = repository.completePaymentAndEnroll(
            order = failedOrder,
            userId = student.id,
            paymentId = failPaymentId,
            status = "FAILED"
        )
        assertFalse("completePaymentAndEnroll must return false for FAILED payment", failResult)

        // Verify FAILED payment record exists
        val paymentRecords = repository.getPaymentsForUser(student.id).first()
        val failedRecord = paymentRecords.find { it.razorpayPaymentId == failPaymentId }
        assertNotNull("Failed payment must be recorded in payment history", failedRecord)
        assertEquals("FAILED", failedRecord!!.status)

        // Verify that series_mpgk_special is STILL LOCKED
        val isFailedSeriesEnrolled = repository.isEnrolledInSeries(student.id, "series_mpgk_special")
        assertFalse("Failed series must NOT be enrolled", isFailedSeriesEnrolled)

        // 18B: Simulate Cancelled Payment
        val cancelOrder = repository.createRazorpayOrder(student.id, "series_hindi_booster")
        val cancelPaymentId = "pay_canc_" + UUID.randomUUID().toString().take(10)
        val cancelResult = repository.completePaymentAndEnroll(
            order = cancelOrder,
            userId = student.id,
            paymentId = cancelPaymentId,
            status = "CANCELLED"
        )
        assertFalse("completePaymentAndEnroll must return false for CANCELLED payment", cancelResult)

        val cancelledRecord = repository.getPaymentsForUser(student.id).first().find { it.razorpayPaymentId == cancelPaymentId }
        assertNotNull("Cancelled payment must be recorded in history", cancelledRecord)
        assertEquals("CANCELLED", cancelledRecord!!.status)

        val isCancelledSeriesEnrolled = repository.isEnrolledInSeries(student.id, "series_hindi_booster")
        assertFalse("Cancelled series must NOT be enrolled and remain LOCKED", isCancelledSeriesEnrolled)

        println("STEP 18 PASSED: Failed and Cancelled payments correctly recorded and test series remain strictly LOCKED!")
    }
}

package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.AppDatabase
import com.example.data.SeedDataLoader
import com.example.data.model.TestSeriesEntity
import com.example.data.repository.AccessCheckResult
import com.example.data.repository.MpsiRepository
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class PricingPaymentRobolectricTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: MpsiRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MpsiRepository(database, null)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun testDefaultPriceIs100() = runBlocking {
        SeedDataLoader.populateInitialData(database)
        val series = database.testSeriesDao().getSeriesByIdOnce("series_complete")
        org.junit.Assert.assertNotNull(series)
        assertEquals(100.0, series!!.price, 0.001)

        val defaultEntity = TestSeriesEntity(
            id = "test_custom",
            title = "Custom Test Series",
            description = "Description",
            numberOfTests = 5,
            questionsPerTest = 50,
            durationMinutes = 60,
            totalMarks = 50
        )
        assertEquals(100.0, defaultEntity.price, 0.001)
    }

    @Test
    fun testRazorpayOrderUsesConfiguredPrice() = runBlocking {
        SeedDataLoader.populateInitialData(database)
        val order100 = repository.createRazorpayOrder("student_01", "series_complete")
        assertEquals(100.0, order100.amountRupees, 0.001)
        assertEquals(10000L, order100.amountPaise)

        // Configurable price updated to 150
        val series = database.testSeriesDao().getSeriesByIdOnce("series_complete")!!
        val updatedSeries = series.copy(price = 150.0)
        database.testSeriesDao().insertSeries(updatedSeries)

        val order150 = repository.createRazorpayOrder("student_01", "series_complete")
        assertEquals(150.0, order150.amountRupees, 0.001)
        assertEquals(15000L, order150.amountPaise)
    }

    @Test
    fun testPaymentSuccessUnlocksSeriesAndFailureLeavesLocked() = runBlocking {
        SeedDataLoader.populateInitialData(database)

        // Before payment: Test access should be denied
        val accessBefore = repository.verifyTestAccess("student_01", "test_01")
        assertTrue(accessBefore is AccessCheckResult.Denied)

        val order = repository.createRazorpayOrder("student_01", "series_complete")

        // Failed payment simulation
        val failSuccess = repository.completePaymentAndEnroll(order, "student_01", "pay_fail_123", "FAILED")
        assertFalse(failSuccess)

        // Access must still be denied after failed payment
        val accessAfterFail = repository.verifyTestAccess("student_01", "test_01")
        assertTrue(accessAfterFail is AccessCheckResult.Denied)

        // Verified successful payment
        val paidSuccess = repository.completePaymentAndEnroll(order, "student_01", "pay_success_123", "PAID")
        assertTrue(paidSuccess)

        // Access must now be allowed after successful verified payment
        val accessAfterPaid = repository.verifyTestAccess("student_01", "test_01")
        assertTrue(accessAfterPaid is AccessCheckResult.Allowed)
    }
}

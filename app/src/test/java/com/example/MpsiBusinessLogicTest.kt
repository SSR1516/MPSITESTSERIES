package com.example

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MpsiBusinessLogicTest {

    @Test
    fun `calculate score with positive and negative marking`() {
        val totalQuestions = 10
        val correctCount = 8
        val wrongCount = 2
        val marksPerQuestion = 1.0
        val negativeMarks = 0.25

        val earned = (correctCount * marksPerQuestion) - (wrongCount * negativeMarks)
        val percentage = (earned / (totalQuestions * marksPerQuestion)) * 100
        val accuracy = (correctCount.toDouble() / (correctCount + wrongCount)) * 100

        assertEquals(7.5, earned, 0.001)
        assertEquals(75.0, percentage, 0.001)
        assertEquals(80.0, accuracy, 0.001)
    }

    @Test
    fun `score cannot drop below zero floor`() {
        val correctCount = 0
        val wrongCount = 10
        val marksPerQuestion = 1.0
        val negativeMarks = 0.25

        val raw = (correctCount * marksPerQuestion) - (wrongCount * negativeMarks)
        val floored = maxOf(0.0, raw)

        assertEquals(-2.5, raw, 0.001)
        assertEquals(0.0, floored, 0.001)
    }

    @Test
    fun `time formatting calculation`() {
        val totalSeconds = 540L // 9 minutes
        val mins = totalSeconds / 60
        val secs = totalSeconds % 60
        val formatted = String.format("%02d:%02d", mins, secs)

        assertEquals("09:00", formatted)
    }

    @Test
    fun `anti-cheat violation threshold auto submit limit`() {
        val maxAllowedViolations = 3
        val currentViolations = 3
        val shouldAutoSubmit = currentViolations >= maxAllowedViolations

        assertTrue(shouldAutoSubmit)
    }
}

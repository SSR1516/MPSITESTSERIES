package com.example.data

import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity

object SeedDataLoader {

    suspend fun populateInitialData(database: AppDatabase) {
        // Users
        val admin = UserEntity(
            id = "admin_01",
            fullName = "Admin Inspector (MPSI Board)",
            mobile = "9826012345",
            email = "admin@mpsi.com",
            passwordHash = "admin123",
            role = "ADMIN"
        )
        val student = UserEntity(
            id = "student_01",
            fullName = "Vikram Rathore",
            mobile = "9876543210",
            email = "student@mpsi.com",
            passwordHash = "student123",
            role = "STUDENT"
        )
        database.userDao().insertUser(admin)
        database.userDao().insertUser(student)

        // Test Series
        val series1 = TestSeriesEntity(
            id = "series_complete",
            title = "MPSI Complete Mock Test Series",
            exam = "Madhya Pradesh Sub-Inspector (MPSI)",
            description = "Complete full syllabus test series with 10 high-standard mock exams, realistic MP Police examination timer, detailed Hindi/English solutions, and in-depth performance analytics.",
            numberOfTests = 10,
            questionsPerTest = 100,
            durationMinutes = 120,
            totalMarks = 100,
            negativeMarking = 0.25,
            validityDays = 180,
            price = 100.0,
            originalPrice = 499.0,
            isPublished = true
        )

        val series2 = TestSeriesEntity(
            id = "series_mpgk_special",
            title = "MPSI MP GK & State Affairs Special",
            exam = "Madhya Pradesh Sub-Inspector (MPSI)",
            description = "Focused sectional test series covering MP rivers, history, tribes, art, national parks, geographical features, and MP budget & policing current affairs.",
            numberOfTests = 5,
            questionsPerTest = 50,
            durationMinutes = 60,
            totalMarks = 50,
            negativeMarking = 0.25,
            validityDays = 90,
            price = 100.0,
            originalPrice = 499.0,
            isPublished = true
        )

        val series3 = TestSeriesEntity(
            id = "series_hindi_booster",
            title = "MPSI General Hindi Score Booster",
            exam = "Madhya Pradesh Sub-Inspector (MPSI)",
            description = "High-scoring Paper 1 master series: Sandhi, Samas, Ras, Chhand, Alankar, Tatsam-Tadbhav, Idioms, Antonyms, Synonyms, and Sentence Correction.",
            numberOfTests = 5,
            questionsPerTest = 70,
            durationMinutes = 60,
            totalMarks = 70,
            negativeMarking = 0.25,
            validityDays = 90,
            price = 100.0,
            originalPrice = 499.0,
            isPublished = true
        )

        database.testSeriesDao().insertSeries(series1)
        database.testSeriesDao().insertSeries(series2)
        database.testSeriesDao().insertSeries(series3)

        // Tests
        val test1 = TestEntity(
            id = "test_01",
            seriesId = "series_complete",
            title = "MPSI Mock Test 01 - Full Length Practice",
            description = "Real exam simulation with 100 representative MCQs, 90 minutes countdown, negative marking of -0.25, and auto-evaluation.",
            durationMinutes = 90,
            totalMarks = 100,
            marksPerQuestion = 1.0,
            negativeMarks = 0.25,
            maxAttempts = 5,
            isPublished = true,
            isAnswerKeyVisible = true
        )

        val test2 = TestEntity(
            id = "test_02",
            seriesId = "series_complete",
            title = "MPSI Mock Test 02 - Speed & Accuracy",
            description = "Specialized test focusing on rapid question solving and time management under pressure.",
            durationMinutes = 15,
            totalMarks = 10,
            marksPerQuestion = 1.0,
            negativeMarks = 0.25,
            maxAttempts = 3,
            isPublished = true,
            isAnswerKeyVisible = true
        )

        database.testDao().insertTest(test1)
        database.testDao().insertTest(test2)

        // Seed 100 full-length practice questions for Test 1
        val questionsList = MpsiQuestionBank.getFullMpsiMockTestQuestions("test_01")
        database.questionDao().insertQuestions(questionsList)
    }
}

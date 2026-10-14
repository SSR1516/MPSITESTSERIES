package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun getUserById(id: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserByIdOnce(id: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE role = 'STUDENT' ORDER BY createdAt DESC")
    fun getAllStudents(): Flow<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users WHERE role = 'STUDENT'")
    suspend fun countStudents(): Int
}

@Dao
interface TestSeriesDao {
    @Query("SELECT * FROM test_series ORDER BY createdAt DESC")
    fun getAllTestSeries(): Flow<List<TestSeriesEntity>>

    @Query("SELECT * FROM test_series WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getPublishedTestSeries(): Flow<List<TestSeriesEntity>>

    @Query("SELECT * FROM test_series WHERE id = :id LIMIT 1")
    fun getSeriesById(id: String): Flow<TestSeriesEntity?>

    @Query("SELECT * FROM test_series WHERE id = :id LIMIT 1")
    suspend fun getSeriesByIdOnce(id: String): TestSeriesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSeries(series: TestSeriesEntity)

    @Update
    suspend fun updateSeries(series: TestSeriesEntity)

    @Query("DELETE FROM test_series WHERE id = :id")
    suspend fun deleteSeriesById(id: String)

    @Query("SELECT COUNT(*) FROM test_series")
    suspend fun countSeries(): Int
}

@Dao
interface TestDao {
    @Query("SELECT * FROM tests WHERE seriesId = :seriesId ORDER BY createdAt ASC")
    fun getTestsForSeries(seriesId: String): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE seriesId = :seriesId AND isPublished = 1 ORDER BY createdAt ASC")
    fun getPublishedTestsForSeries(seriesId: String): Flow<List<TestEntity>>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    fun getTestById(id: String): Flow<TestEntity?>

    @Query("SELECT * FROM tests WHERE id = :id LIMIT 1")
    suspend fun getTestByIdOnce(id: String): TestEntity?

    @Query("SELECT * FROM tests ORDER BY createdAt DESC")
    fun getAllTests(): Flow<List<TestEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: TestEntity)

    @Update
    suspend fun updateTest(test: TestEntity)

    @Query("DELETE FROM tests WHERE id = :id")
    suspend fun deleteTestById(id: String)

    @Query("SELECT COUNT(*) FROM tests")
    suspend fun countTests(): Int
}

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY questionNumber ASC")
    fun getQuestionsForTest(testId: String): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions WHERE testId = :testId ORDER BY questionNumber ASC")
    suspend fun getQuestionsForTestOnce(testId: String): List<QuestionEntity>

    @Query("SELECT * FROM questions ORDER BY questionNumber ASC")
    fun getAllQuestions(): Flow<List<QuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionEntity>)

    @Update
    suspend fun updateQuestion(question: QuestionEntity)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteQuestionById(id: String)

    @Query("SELECT COUNT(*) FROM questions WHERE testId = :testId")
    suspend fun countQuestionsForTest(testId: String): Int
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments ORDER BY createdAt DESC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("SELECT * FROM payments WHERE userId = :userId ORDER BY createdAt DESC")
    fun getPaymentsForUser(userId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Update
    suspend fun updatePayment(payment: PaymentEntity)

    @Query("SELECT SUM(amount) FROM payments WHERE status = 'PAID'")
    suspend fun getTotalRevenue(): Double?
}

@Dao
interface EnrollmentDao {
    @Query("SELECT * FROM enrollments WHERE userId = :userId AND status = 'ACTIVE'")
    fun getEnrollmentsForUser(userId: String): Flow<List<EnrollmentEntity>>

    @Query("SELECT * FROM enrollments WHERE userId = :userId AND seriesId = :seriesId AND status = 'ACTIVE' LIMIT 1")
    suspend fun getActiveEnrollment(userId: String, seriesId: String): EnrollmentEntity?

    @Query("SELECT * FROM enrollments ORDER BY purchaseDate DESC")
    fun getAllEnrollments(): Flow<List<EnrollmentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnrollment(enrollment: EnrollmentEntity)

    @Query("SELECT COUNT(DISTINCT userId) FROM enrollments WHERE status = 'ACTIVE'")
    suspend fun countPaidStudents(): Int
}

@Dao
interface AttemptDao {
    @Query("SELECT * FROM attempts WHERE userId = :userId ORDER BY submittedAt DESC")
    fun getAttemptsForUser(userId: String): Flow<List<AttemptEntity>>

    @Query("SELECT * FROM attempts WHERE userId = :userId AND testId = :testId ORDER BY submittedAt DESC")
    fun getAttemptsForUserAndTest(userId: String, testId: String): Flow<List<AttemptEntity>>

    @Query("SELECT COUNT(*) FROM attempts WHERE userId = :userId AND testId = :testId")
    suspend fun countAttempts(userId: String, testId: String): Int

    @Query("SELECT * FROM attempts WHERE id = :id LIMIT 1")
    fun getAttemptById(id: String): Flow<AttemptEntity?>

    @Query("SELECT * FROM attempts WHERE id = :id LIMIT 1")
    suspend fun getAttemptByIdOnce(id: String): AttemptEntity?

    @Query("SELECT * FROM attempts ORDER BY submittedAt DESC")
    fun getAllAttempts(): Flow<List<AttemptEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempt(attempt: AttemptEntity)

    @Query("SELECT COUNT(*) FROM attempts")
    suspend fun countTotalAttempts(): Int
}

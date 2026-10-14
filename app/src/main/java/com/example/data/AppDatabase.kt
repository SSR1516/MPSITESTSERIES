package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.AttemptDao
import com.example.data.dao.EnrollmentDao
import com.example.data.dao.PaymentDao
import com.example.data.dao.QuestionDao
import com.example.data.dao.TestDao
import com.example.data.dao.TestSeriesDao
import com.example.data.dao.UserDao
import com.example.data.model.AttemptEntity
import com.example.data.model.EnrollmentEntity
import com.example.data.model.PaymentEntity
import com.example.data.model.QuestionEntity
import com.example.data.model.TestEntity
import com.example.data.model.TestSeriesEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        TestSeriesEntity::class,
        TestEntity::class,
        QuestionEntity::class,
        PaymentEntity::class,
        EnrollmentEntity::class,
        AttemptEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun testSeriesDao(): TestSeriesDao
    abstract fun testDao(): TestDao
    abstract fun questionDao(): QuestionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun enrollmentDao(): EnrollmentDao
    abstract fun attemptDao(): AttemptDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mpsi_test_series.db"
                ).fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                SeedDataLoader.populateInitialData(database)
                            }
                        }
                    }

                    override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
                        super.onDestructiveMigration(db)
                        INSTANCE?.let { database ->
                            CoroutineScope(Dispatchers.IO).launch {
                                SeedDataLoader.populateInitialData(database)
                            }
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

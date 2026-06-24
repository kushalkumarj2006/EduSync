package com.example.data.local

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<Course>)

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: String): Course?
}

@Dao
interface UserProgressDao {
    @Query("SELECT * FROM user_progress")
    fun getAllProgress(): Flow<List<UserProgress>>

    @Query("SELECT * FROM user_progress WHERE courseId = :courseId LIMIT 1")
    fun getProgressForCourse(courseId: String): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgress)

    @Query("SELECT * FROM user_progress WHERE isSynced = 0")
    suspend fun getUnsyncedProgress(): List<UserProgress>

    @Query("UPDATE user_progress SET isSynced = 1 WHERE courseId = :courseId")
    suspend fun markProgressAsSynced(courseId: String)
}

@Dao
interface VideoWatchTimeDao {
    @Query("SELECT * FROM video_watch_time WHERE courseId = :courseId LIMIT 1")
    fun getWatchTimeForCourse(courseId: String): Flow<VideoWatchTime?>

    @Query("SELECT * FROM video_watch_time WHERE courseId = :courseId LIMIT 1")
    suspend fun getWatchTimeForCourseSync(courseId: String): VideoWatchTime?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchTime(watchTime: VideoWatchTime)

    @Query("SELECT * FROM video_watch_time WHERE isSynced = 0")
    suspend fun getUnsyncedWatchTimes(): List<VideoWatchTime>

    @Query("UPDATE video_watch_time SET isSynced = 1 WHERE courseId = :courseId")
    suspend fun markWatchTimeAsSynced(courseId: String)
}

@Dao
interface QuizAnswerDao {
    @Query("SELECT * FROM quiz_answers WHERE courseId = :courseId")
    fun getAnswersForCourse(courseId: String): Flow<List<QuizAnswer>>

    @Query("SELECT * FROM quiz_answers WHERE courseId = :courseId")
    suspend fun getAnswersForCourseSync(courseId: String): List<QuizAnswer>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: QuizAnswer)

    @Query("SELECT * FROM quiz_answers WHERE isSynced = 0")
    suspend fun getUnsyncedAnswers(): List<QuizAnswer>

    @Query("UPDATE quiz_answers SET isSynced = 1 WHERE courseId = :courseId AND questionIndex = :questionIndex")
    suspend fun markAnswerAsSynced(courseId: String, questionIndex: Int)
}

@Dao
interface DataUsageDao {
    @Query("SELECT * FROM data_usage WHERE dateString = :dateString LIMIT 1")
    fun getUsageForDate(dateString: String): Flow<DataUsage?>

    @Query("SELECT * FROM data_usage WHERE dateString = :dateString LIMIT 1")
    suspend fun getUsageForDateSync(dateString: String): DataUsage?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsage(usage: DataUsage)
}

@Database(
    entities = [
        Course::class,
        UserProgress::class,
        VideoWatchTime::class,
        QuizAnswer::class,
        DataUsage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EduSyncDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun userProgressDao(): UserProgressDao
    abstract fun videoWatchTimeDao(): VideoWatchTimeDao
    abstract fun quizAnswerDao(): QuizAnswerDao
    abstract fun dataUsageDao(): DataUsageDao

    companion object {
        @Volatile
        private var INSTANCE: EduSyncDatabase? = null

        fun getDatabase(context: android.content.Context): EduSyncDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduSyncDatabase::class.java,
                    "edusync_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

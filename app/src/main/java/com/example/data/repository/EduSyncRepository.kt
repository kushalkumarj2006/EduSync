package com.example.data.repository

import android.content.Context
import com.example.data.local.EduSyncDatabase
import com.example.data.mock.MockData
import com.example.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EduSyncRepository(private val context: Context) {
    private val database = EduSyncDatabase.getDatabase(context)
    private val courseDao = database.courseDao()
    private val userProgressDao = database.userProgressDao()
    private val videoWatchTimeDao = database.videoWatchTimeDao()
    private val quizAnswerDao = database.quizAnswerDao()
    private val dataUsageDao = database.dataUsageDao()

    // Exposed Flows
    val allCourses: Flow<List<Course>> = courseDao.getAllCourses()
    val allProgress: Flow<List<UserProgress>> = userProgressDao.getAllProgress()

    // Live auth state (mock)
    private val _currentUser = MutableStateFlow<String?>(null)
    val currentUser: StateFlow<String?> = _currentUser.asStateFlow()

    init {
        // Pre-populate courses on startup if empty
        _currentUser.value = context.getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            .getString("logged_in_user", null)
    }

    suspend fun checkAndPrepopulate() {
        withContext(Dispatchers.IO) {
            val existing = allCourses.first()
            if (existing.isEmpty()) {
                courseDao.insertCourses(MockData.courses)
            }
        }
    }

    // Auth Actions
    suspend fun login(email: String): Boolean {
        delay(600) // Realistic UI delay
        _currentUser.value = email
        context.getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("logged_in_user", email)
            .apply()
        return true
    }

    suspend fun signup(email: String): Boolean {
        delay(800)
        _currentUser.value = email
        context.getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            .edit()
            .putString("logged_in_user", email)
            .apply()
        return true
    }

    fun logout() {
        _currentUser.value = null
        context.getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            .edit()
            .remove("logged_in_user")
            .apply()
    }

    // Course Detail
    suspend fun getCourseById(id: String): Course? {
        return withContext(Dispatchers.IO) {
            courseDao.getCourseById(id)
        }
    }

    fun getProgressForCourse(courseId: String): Flow<UserProgress?> {
        return userProgressDao.getProgressForCourse(courseId)
    }

    fun getWatchTimeForCourse(courseId: String): Flow<VideoWatchTime?> {
        return videoWatchTimeDao.getWatchTimeForCourse(courseId)
    }

    fun getAnswersForCourse(courseId: String): Flow<List<QuizAnswer>> {
        return quizAnswerDao.getAnswersForCourse(courseId)
    }

    // Save playback position
    suspend fun saveVideoProgress(courseId: String, currentPositionSeconds: Int, totalDurationSeconds: Int) {
        withContext(Dispatchers.IO) {
            val percentage = if (totalDurationSeconds > 0) {
                ((currentPositionSeconds.toFloat() / totalDurationSeconds.toFloat()) * 100).toInt().coerceIn(0, 100)
            } else 0

            // Update watch time
            videoWatchTimeDao.insertWatchTime(
                VideoWatchTime(
                    courseId = courseId,
                    lastWatchedPositionSeconds = currentPositionSeconds,
                    updatedAt = System.currentTimeMillis(),
                    isSynced = false
                )
            )

            // Update user progress percentage
            userProgressDao.insertProgress(
                UserProgress(
                    courseId = courseId,
                    progressPercentage = percentage,
                    lastWatchedTimestamp = System.currentTimeMillis(),
                    isSynced = false
                )
            )
        }
    }

    // Save quiz answer
    suspend fun saveQuizAnswer(courseId: String, questionIndex: Int, selectedOptionIndex: Int, isCorrect: Boolean) {
        withContext(Dispatchers.IO) {
            quizAnswerDao.insertAnswer(
                QuizAnswer(
                    courseId = courseId,
                    questionIndex = questionIndex,
                    selectedOptionIndex = selectedOptionIndex,
                    isCorrect = isCorrect,
                    updatedAt = System.currentTimeMillis(),
                    isSynced = false
                )
            )
        }
    }

    // Data Saver and Usage Metrics
    fun getTodayDateString(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun getTodayDataUsage(): Flow<DataUsage?> {
        return dataUsageDao.getUsageForDate(getTodayDateString())
    }

    suspend fun addDataUsage(bytes: Long) {
        withContext(Dispatchers.IO) {
            val dateStr = getTodayDateString()
            val existing = dataUsageDao.getUsageForDateSync(dateStr)
            val currentBytes = existing?.bytesUsed ?: 0L
            dataUsageDao.insertUsage(
                DataUsage(dateString = dateStr, bytesUsed = currentBytes + bytes)
            )
        }
    }

    // Sync Engine (Store-and-Forward)
    // Counts how many items are currently waiting to be uploaded to cloud
    fun getPendingSyncCountFlow(): Flow<Int> {
        return combine(
            flow { emit(userProgressDao.getUnsyncedProgress()) },
            flow { emit(videoWatchTimeDao.getUnsyncedWatchTimes()) },
            flow { emit(quizAnswerDao.getUnsyncedAnswers()) }
        ) { progress, watchTime, answers ->
            progress.size + watchTime.size + answers.size
        }.flowOn(Dispatchers.IO)
    }

    suspend fun getPendingSyncCount(): Int {
        return withContext(Dispatchers.IO) {
            val progress = userProgressDao.getUnsyncedProgress().size
            val watchTime = videoWatchTimeDao.getUnsyncedWatchTimes().size
            val answers = quizAnswerDao.getUnsyncedAnswers().size
            progress + watchTime + answers
        }
    }

    // Performs mock synchronization. Resolves conflicts using Last Write Wins
    suspend fun syncPendingData(onProgress: (String, Float) -> Unit): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Fetch all unsynced local logs
                val unsyncedProgress = userProgressDao.getUnsyncedProgress()
                val unsyncedWatchTime = videoWatchTimeDao.getUnsyncedWatchTimes()
                val unsyncedAnswers = quizAnswerDao.getUnsyncedAnswers()

                val totalItems = unsyncedProgress.size + unsyncedWatchTime.size + unsyncedAnswers.size
                if (totalItems == 0) return@withContext true

                var processedCount = 0

                // 1. Sync User Progress
                for (progress in unsyncedProgress) {
                    onProgress("Syncing Progress: Course ${progress.courseId} (${progress.progressPercentage}%)", processedCount.toFloat() / totalItems)
                    delay(300) // Simulate network latency
                    userProgressDao.markProgressAsSynced(progress.courseId)
                    processedCount++
                }

                // 2. Sync Video Playback Times
                for (watch in unsyncedWatchTime) {
                    onProgress("Syncing Playback Resume: Course ${watch.courseId} (at ${watch.lastWatchedPositionSeconds}s)", processedCount.toFloat() / totalItems)
                    delay(300)
                    videoWatchTimeDao.markWatchTimeAsSynced(watch.courseId)
                    processedCount++
                }

                // 3. Sync Quiz Answers
                for (answer in unsyncedAnswers) {
                    onProgress("Syncing Quiz Answer: Course ${answer.courseId}, Question ${answer.questionIndex + 1}", processedCount.toFloat() / totalItems)
                    delay(300)
                    quizAnswerDao.markAnswerAsSynced(answer.courseId, answer.questionIndex)
                    processedCount++
                }

                onProgress("Syncing Complete!", 1.0f)
                delay(500)
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }
}

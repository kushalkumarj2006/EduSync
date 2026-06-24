package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.Course
import com.example.data.model.QuizAnswer
import com.example.data.model.QuizQuestion
import com.example.data.model.StudyBadge
import com.example.data.repository.EduSyncRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EduSyncViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EduSyncRepository(application)
    private val moshi = Moshi.Builder().build()

    // Network Status (Simulated + Actual fallback)
    private val _isOnlineSimulated = MutableStateFlow(true)
    val isOnlineSimulated: StateFlow<Boolean> = _isOnlineSimulated.asStateFlow()

    // Data Saver and Usage Tracking
    private val _dataSaverMode = MutableStateFlow(true)
    val dataSaverMode: StateFlow<Boolean> = _dataSaverMode.asStateFlow()

    // Auth States
    val currentUser: StateFlow<String?> = repository.currentUser

    // All Progress State
    val allProgress: StateFlow<List<com.example.data.model.UserProgress>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Answers Flow
    val allAnswers: StateFlow<List<QuizAnswer>> = repository.getAllAnswers()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Study Streak & Badges
    private val _studiedDates = MutableStateFlow<List<String>>(emptyList())
    val studiedDates: StateFlow<List<String>> = _studiedDates.asStateFlow()

    val studyStreakCount: StateFlow<Int> = _studiedDates
        .map { dates ->
            calculateStreak(dates.toSet())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val studyBadges: StateFlow<List<StudyBadge>> = combine(
        allProgress,
        allAnswers,
        dataSaverMode,
        isOnlineSimulated,
        studyStreakCount
    ) { progressList, answersList, dataSaver, onlineSim, streak ->
        // 1. Bronze Starter: Progress on at least one course > 0%
        val hasStarted = progressList.any { it.progressPercentage > 0 }
        val badge1 = StudyBadge(
            id = "bronze_starter",
            name = "Bronze Starter",
            description = "Begin watching your first course video.",
            iconName = "star",
            isUnlocked = hasStarted,
            progressDescription = if (hasStarted) "Unlocked!" else "Watch any course video to unlock",
            category = "Milestone"
        )

        // 2. Gold Finisher: Completion of at least one course = 100%
        val hasCompleted = progressList.any { it.progressPercentage >= 100 }
        val badge2 = StudyBadge(
            id = "gold_finisher",
            name = "Gold Finisher",
            description = "Watch 100% of any course video.",
            iconName = "check_circle",
            isUnlocked = hasCompleted,
            progressDescription = if (hasCompleted) "Unlocked!" else "Complete a course video to unlock",
            category = "Milestone"
        )

        // 3. Quiz Scholar: Completed at least one quiz answer
        val hasDoneQuiz = answersList.isNotEmpty()
        val badge3 = StudyBadge(
            id = "quiz_scholar",
            name = "Quiz Scholar",
            description = "Submit an answer for any course quiz.",
            iconName = "flash_on",
            isUnlocked = hasDoneQuiz,
            progressDescription = if (hasDoneQuiz) "Unlocked!" else "Answer a quiz question to unlock",
            category = "Milestone"
        )

        // 4. Offline Champion: Saved progress or answered a quiz while unsynced (offline)
        val hasOfflineStudy = progressList.any { !it.isSynced } || answersList.any { !it.isSynced }
        val badge4 = StudyBadge(
            id = "offline_champion",
            name = "Offline Champion",
            description = "Save your study progress or quiz answers offline.",
            iconName = "cloud",
            isUnlocked = hasOfflineStudy,
            progressDescription = if (hasOfflineStudy) "Unlocked!" else "Study in offline mode to unlock",
            category = "Milestone"
        )

        // 5. Data Conservator: Study with Data Saver Mode enabled
        val badge5 = StudyBadge(
            id = "data_conservator",
            name = "Data Conservator",
            description = "Protect your bandwidth by enabling Data Saver mode.",
            iconName = "signal_cellular",
            isUnlocked = dataSaver,
            progressDescription = if (dataSaver) "Unlocked!" else "Turn on Data Saver mode to unlock",
            category = "Milestone"
        )

        // 6. Streak Master: Streak of 3 or more days
        val hasStreakOf3 = streak >= 3
        val badge6 = StudyBadge(
            id = "streak_master",
            name = "Streak Master",
            description = "Maintain a consecutive daily study streak of 3 days or more.",
            iconName = "favorite",
            isUnlocked = hasStreakOf3,
            progressDescription = "Current streak: $streak / 3 days",
            category = "Streak"
        )

        listOf(badge1, badge2, badge3, badge4, badge5, badge6)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val todayDataUsage: StateFlow<Long> = repository.getTodayDataUsage()
        .map { it?.bytesUsed ?: 0L }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    // Course List
    val courses: StateFlow<List<Course>> = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<String?>("All")
    val selectedCategory: StateFlow<String?> = _selectedCategory.asStateFlow()

    val filteredCourses: StateFlow<List<Course>> = combine(
        courses, _searchQuery, _selectedCategory
    ) { courseList, query, cat ->
        courseList.filter { course ->
            val matchesSearch = course.title.contains(query, ignoreCase = true) ||
                    course.description.contains(query, ignoreCase = true)
            val matchesCategory = cat == "All" || course.category == cat
            matchesSearch && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Course Navigation State
    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> = _selectedCourse.asStateFlow()

    // Video Playback Settings
    private val _selectedQuality = MutableStateFlow("Auto") // Auto, 1080p, 720p, 480p, Text-Only
    val selectedQuality: StateFlow<String> = _selectedQuality.asStateFlow()

    private val _currentWatchPosition = MutableStateFlow(0)
    val currentWatchPosition: StateFlow<Int> = _currentWatchPosition.asStateFlow()

    private val _isVideoPlaying = MutableStateFlow(false)
    val isVideoPlaying: StateFlow<Boolean> = _isVideoPlaying.asStateFlow()

    // Quiz States
    private val _currentQuizIndex = MutableStateFlow(0)
    val currentQuizIndex: StateFlow<Int> = _currentQuizIndex.asStateFlow()

    private val _selectedAnswerIndex = MutableStateFlow<Int?>(null)
    val selectedAnswerIndex: StateFlow<Int?> = _selectedAnswerIndex.asStateFlow()

    val savedQuizAnswers: StateFlow<List<QuizAnswer>> = _selectedCourse
        .flatMapLatest { course ->
            if (course != null) repository.getAnswersForCourse(course.id)
            else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Store & Forward Sync States
    val pendingSyncCount: StateFlow<Int> = repository.getPendingSyncCountFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _syncMessage = MutableStateFlow("Up to date")
    val syncMessage: StateFlow<String> = _syncMessage.asStateFlow()

    private val _syncFraction = MutableStateFlow(0f)
    val syncFraction: StateFlow<Float> = _syncFraction.asStateFlow()

    private var playbackJob: Job? = null

    init {
        viewModelScope.launch {
            repository.checkAndPrepopulate()
            loadStudiedDates()
            // Detect real internet network state to sync simulated online state
            val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            val hasInternet = capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
            _isOnlineSimulated.value = hasInternet
        }
    }

    // Toggle simulated internet connection (Excellent for offline demonstration)
    fun toggleInternetSimulation() {
        _isOnlineSimulated.value = !_isOnlineSimulated.value
    }

    // Toggle data saver mode
    fun toggleDataSaverMode() {
        _dataSaverMode.value = !_dataSaverMode.value
    }

    // Auth actions
    fun performLogin(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (email.contains("@") && email.length > 5) {
                repository.login(email)
                onSuccess()
            } else {
                onError("Please enter a valid email address.")
            }
        }
    }

    fun performSignup(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            if (email.contains("@") && email.length > 5) {
                repository.signup(email)
                onSuccess()
            } else {
                onError("Please enter a valid email address.")
            }
        }
    }

    fun performLogout() {
        viewModelScope.launch {
            pauseVideo()
            _selectedCourse.value = null
            repository.logout()
        }
    }

    // Course navigation
    fun selectCourse(course: Course?) {
        viewModelScope.launch {
            pauseVideo()
            _selectedCourse.value = course
            _currentQuizIndex.value = 0
            _selectedAnswerIndex.value = null
            if (course != null) {
                // Restore watch position from Room
                val watchTime = repository.getWatchTimeForCourse(course.id).firstOrNull()
                _currentWatchPosition.value = watchTime?.lastWatchedPositionSeconds ?: 0
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategory.value = category
    }

    // Playback control
    fun playVideo() {
        val course = _selectedCourse.value ?: return
        recordStudyActivityToday()
        if (selectedQuality.value == "Text-Only") {
            // Text-Only uses zero video bandwidth, loads transcript instantly!
            _isVideoPlaying.value = true
            startPlaybackLoop(course.id)
            return
        }

        _isVideoPlaying.value = true
        startPlaybackLoop(course.id)

        // Simulated high/low resolution download charge
        viewModelScope.launch {
            val initialLoadCharge = when (selectedQuality.value) {
                "1080p" -> if (dataSaverMode.value) 2_000_000L else 5_000_000L
                "720p" -> if (dataSaverMode.value) 1_000_000L else 2_500_000L
                "480p" -> 500_000L
                "Auto" -> if (dataSaverMode.value) 600_000L else 2_000_000L
                else -> 500_000L
            }
            repository.addDataUsage(initialLoadCharge)
        }
    }

    fun pauseVideo() {
        _isVideoPlaying.value = false
        playbackJob?.cancel()
    }

    fun seekToPosition(seconds: Int) {
        val course = _selectedCourse.value ?: return
        _currentWatchPosition.value = seconds.coerceIn(0, 300)
        viewModelScope.launch {
            repository.saveVideoProgress(course.id, _currentWatchPosition.value, 300)
        }
    }

    fun setQuality(quality: String) {
        _selectedQuality.value = quality
        if (quality == "Text-Only") {
            // Stop any massive video simulation billing
        }
    }

    private fun startPlaybackLoop(courseId: String) {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            var secondsCounter = 0
            while (_isVideoPlaying.value) {
                delay(1000)
                _currentWatchPosition.value = (_currentWatchPosition.value + 1).coerceAtMost(300)
                secondsCounter++

                // Apply dynamic bandwidth charging
                val bytesPerSec = when (_selectedQuality.value) {
                    "1080p" -> if (_dataSaverMode.value) 120_000L else 250_000L
                    "720p" -> if (_dataSaverMode.value) 60_000L else 125_000L
                    "480p" -> 35_000L
                    "Auto" -> if (_dataSaverMode.value) 40_000L else 150_000L
                    "Text-Only" -> 100L // Minimal text progress stream
                    else -> 40_000L
                }
                repository.addDataUsage(bytesPerSec)

                if (secondsCounter >= 5) {
                    repository.saveVideoProgress(courseId, _currentWatchPosition.value, 300)
                    secondsCounter = 0
                }

                if (_currentWatchPosition.value >= 300) {
                    pauseVideo()
                    break
                }
            }
        }
    }

    // Quiz logic
    fun getQuizQuestionsForCurrentCourse(): List<QuizQuestion> {
        val course = _selectedCourse.value ?: return emptyList()
        return try {
            val listType = Types.newParameterizedType(List::class.java, QuizQuestion::class.java)
            val adapter = moshi.adapter<List<QuizQuestion>>(listType)
            adapter.fromJson(course.quizQuestionsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun selectQuizAnswerOption(optionIndex: Int) {
        _selectedAnswerIndex.value = optionIndex
    }

    fun submitQuizAnswer() {
        val course = _selectedCourse.value ?: return
        val currentIdx = _currentQuizIndex.value
        val selectedIdx = _selectedAnswerIndex.value ?: return
        val questions = getQuizQuestionsForCurrentCourse()
        if (currentIdx >= questions.size) return

        val question = questions[currentIdx]
        val isCorrect = selectedIdx == question.correctAnswerIndex

        recordStudyActivityToday()
        viewModelScope.launch {
            repository.saveQuizAnswer(
                courseId = course.id,
                questionIndex = currentIdx,
                selectedOptionIndex = selectedIdx,
                isCorrect = isCorrect
            )
            // Progress to next or reset index
            _selectedAnswerIndex.value = null
            _currentQuizIndex.value = currentIdx + 1
        }
    }

    fun resetQuiz() {
        _currentQuizIndex.value = 0
        _selectedAnswerIndex.value = null
    }

    // Trigger store and forward synchronization to Cloud/Firebase
    fun triggerSync() {
        if (!isOnlineSimulated.value) {
            _syncMessage.value = "Cannot sync: You are currently offline"
            return
        }
        if (_isSyncing.value) return

        viewModelScope.launch {
            _isSyncing.value = true
            repository.syncPendingData { message, progress ->
                _syncMessage.value = message
                _syncFraction.value = progress
            }
            _isSyncing.value = false
            _syncMessage.value = "Synced successfully"
            _syncFraction.value = 0f
        }
    }

    private fun loadStudiedDates() {
        val sharedPrefs = getApplication<Application>().getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
        val dateSet = sharedPrefs.getStringSet("studied_dates", emptySet()) ?: emptySet()
        _studiedDates.value = dateSet.toList().sorted()
    }

    fun recordStudyActivityToday() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            val currentDates = sharedPrefs.getStringSet("studied_dates", emptySet())?.toMutableSet() ?: mutableSetOf()
            val todayStr = repository.getTodayDateString()
            if (!currentDates.contains(todayStr)) {
                currentDates.add(todayStr)
                sharedPrefs.edit().putStringSet("studied_dates", currentDates).apply()
                _studiedDates.value = currentDates.toList().sorted()
            }
        }
    }

    fun simulateStreak(days: Int) {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            val currentDates = mutableSetOf<String>()
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            
            for (i in 0 until days) {
                val cal = Calendar.getInstance()
                cal.add(Calendar.DAY_OF_YEAR, -i)
                currentDates.add(sdf.format(cal.time))
            }
            
            sharedPrefs.edit().putStringSet("studied_dates", currentDates).apply()
            _studiedDates.value = currentDates.toList().sorted()
        }
    }

    fun clearStreakSimulation() {
        viewModelScope.launch {
            val sharedPrefs = getApplication<Application>().getSharedPreferences("edusync_prefs", Context.MODE_PRIVATE)
            sharedPrefs.edit().remove("studied_dates").apply()
            _studiedDates.value = emptyList()
        }
    }

    private fun calculateStreak(dates: Set<String>): Int {
        if (dates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val parsedDates = dates.mapNotNull {
            try { sdf.parse(it) } catch(e: Exception) { null }
        }.sortedDescending() // newest first

        if (parsedDates.isEmpty()) return 0

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val firstDate = parsedDates.first()
        val firstDateTruncated = Calendar.getInstance().apply {
            time = firstDate
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        if (firstDateTruncated != today && firstDateTruncated != yesterday) {
            return 0
        }

        var streak = 1
        var currentCal = Calendar.getInstance().apply { time = firstDateTruncated }

        for (i in 1 until parsedDates.size) {
            val nextDate = parsedDates[i]
            val nextDateTruncated = Calendar.getInstance().apply {
                time = nextDate
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            val expectedPrevDay = (currentCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, -1)
            }.time

            if (nextDateTruncated == expectedPrevDay) {
                streak++
                currentCal.time = nextDateTruncated
            } else if (nextDateTruncated == currentCal.time) {
                // Same day, skip
            } else {
                break
            }
        }
        return streak
    }
}

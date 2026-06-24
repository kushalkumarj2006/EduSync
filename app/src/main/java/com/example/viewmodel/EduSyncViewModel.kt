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
import com.example.data.repository.EduSyncRepository
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EduSyncViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = EduSyncRepository(application)
    private val moshi = Moshi.Builder().build()

    // Auth States
    val currentUser: StateFlow<String?> = repository.currentUser

    // All Progress State
    val allProgress: StateFlow<List<com.example.data.model.UserProgress>> = repository.allProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Network Status (Simulated + Actual fallback)
    private val _isOnlineSimulated = MutableStateFlow(true)
    val isOnlineSimulated: StateFlow<Boolean> = _isOnlineSimulated.asStateFlow()

    // Data Saver and Usage Tracking
    private val _dataSaverMode = MutableStateFlow(true)
    val dataSaverMode: StateFlow<Boolean> = _dataSaverMode.asStateFlow()

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
}

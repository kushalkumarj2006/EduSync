package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.CourseDetailScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EduSyncViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: EduSyncViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Supports full edge-to-edge drawing
        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
                val selectedCourse by viewModel.selectedCourse.collectAsStateWithLifecycle()
                val courses by viewModel.filteredCourses.collectAsStateWithLifecycle()
                val allProgress by viewModel.allProgress.collectAsStateWithLifecycle()
                
                val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
                val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
                
                val isOnline by viewModel.isOnlineSimulated.collectAsStateWithLifecycle()
                val dataSaverActive by viewModel.dataSaverMode.collectAsStateWithLifecycle()
                val bytesUsed by viewModel.todayDataUsage.collectAsStateWithLifecycle()
                
                val pendingSyncCount by viewModel.pendingSyncCount.collectAsStateWithLifecycle()
                val isSyncing by viewModel.isSyncing.collectAsStateWithLifecycle()
                val syncMessage by viewModel.syncMessage.collectAsStateWithLifecycle()
                val syncFraction by viewModel.syncFraction.collectAsStateWithLifecycle()
                
                val watchPosition by viewModel.currentWatchPosition.collectAsStateWithLifecycle()
                val isVideoPlaying by viewModel.isVideoPlaying.collectAsStateWithLifecycle()
                val selectedQuality by viewModel.selectedQuality.collectAsStateWithLifecycle()
                
                val quizIndex by viewModel.currentQuizIndex.collectAsStateWithLifecycle()
                val selectedOption by viewModel.selectedAnswerIndex.collectAsStateWithLifecycle()
                val savedAnswers by viewModel.savedQuizAnswers.collectAsStateWithLifecycle()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize()) {
                        AnimatedContent(
                            targetState = currentUser,
                            transitionSpec = {
                                fadeIn() togetherWith fadeOut()
                            },
                            label = "auth_gate"
                        ) { user ->
                            if (user == null) {
                                LoginScreen(
                                    onLoginSuccess = { email ->
                                        // login successful
                                    },
                                    onLoginClick = { email, onSuccess, onError ->
                                        viewModel.performLogin(email, onSuccess, onError)
                                    }
                                )
                            } else {
                                AnimatedContent(
                                    targetState = selectedCourse,
                                    transitionSpec = {
                                        fadeIn() togetherWith fadeOut()
                                    },
                                    label = "nav_screen"
                                ) { course ->
                                    if (course == null) {
                                        DashboardScreen(
                                            email = user,
                                            courses = courses,
                                            allProgress = allProgress,
                                            searchQuery = searchQuery,
                                            selectedCategory = selectedCategory,
                                            isOnline = isOnline,
                                            dataSaverActive = dataSaverActive,
                                            bytesUsed = bytesUsed,
                                            pendingSyncCount = pendingSyncCount,
                                            isSyncing = isSyncing,
                                            syncMessage = syncMessage,
                                            syncFraction = syncFraction,
                                            onSearchChange = { viewModel.setSearchQuery(it) },
                                            onCategorySelect = { viewModel.setCategoryFilter(it) },
                                            onCourseSelect = { viewModel.selectCourse(it) },
                                            onToggleOnline = { viewModel.toggleInternetSimulation() },
                                            onToggleDataSaver = { viewModel.toggleDataSaverMode() },
                                            onSyncTrigger = { viewModel.triggerSync() },
                                            onLogoutClick = { viewModel.performLogout() }
                                        )
                                    } else {
                                        CourseDetailScreen(
                                            course = course,
                                            watchPosition = watchPosition,
                                            isPlaying = isVideoPlaying,
                                            selectedQuality = selectedQuality,
                                            quizIndex = quizIndex,
                                            selectedOption = selectedOption,
                                            savedAnswers = savedAnswers,
                                            onBackClick = { viewModel.selectCourse(null) },
                                            onPlayToggle = {
                                                if (isVideoPlaying) viewModel.pauseVideo()
                                                else viewModel.playVideo()
                                            },
                                            onSeek = { viewModel.seekToPosition(it) },
                                            onQualitySelect = { viewModel.setQuality(it) },
                                            onOptionSelect = { viewModel.selectQuizAnswerOption(it) },
                                            onSubmitAnswer = { viewModel.submitQuizAnswer() },
                                            onResetQuiz = { viewModel.resetQuiz() }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

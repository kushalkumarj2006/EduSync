package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class Course(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // Mathematics, Science, English, Social Studies
    val thumbnailUrl: String,
    val videoUrl: String,
    val transcript: String,
    val quizQuestionsJson: String // JSON string containing List<QuizQuestion>
)

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int
)

@Entity(tableName = "user_progress")
data class UserProgress(
    @PrimaryKey val courseId: String,
    val progressPercentage: Int, // 0 to 100
    val lastWatchedTimestamp: Long,
    val isSynced: Boolean = false
)

@Entity(tableName = "video_watch_time")
data class VideoWatchTime(
    @PrimaryKey val courseId: String,
    val lastWatchedPositionSeconds: Int,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

@Entity(tableName = "quiz_answers", primaryKeys = ["courseId", "questionIndex"])
data class QuizAnswer(
    val courseId: String,
    val questionIndex: Int,
    val selectedOptionIndex: Int,
    val isCorrect: Boolean,
    val updatedAt: Long,
    val isSynced: Boolean = false
)

@Entity(tableName = "data_usage")
data class DataUsage(
    @PrimaryKey val dateString: String, // e.g. "2026-06-24"
    val bytesUsed: Long
)

data class StudyBadge(
    val id: String,
    val name: String,
    val description: String,
    val iconName: String, // "star", "check_circle", "flash_on", "signal_cellular", "cloud", "favorite"
    val isUnlocked: Boolean,
    val progressDescription: String = "",
    val category: String // "Milestone" or "Streak"
)


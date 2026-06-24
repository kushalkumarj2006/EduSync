package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Course
import com.example.data.model.UserProgress
import com.example.ui.components.*
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen
import com.example.data.model.StudyBadge

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    email: String,
    courses: List<Course>,
    allProgress: List<UserProgress>,
    searchQuery: String,
    selectedCategory: String?,
    isOnline: Boolean,
    dataSaverActive: Boolean,
    bytesUsed: Long,
    pendingSyncCount: Int,
    isSyncing: Boolean,
    syncMessage: String,
    syncFraction: Float,
    streakCount: Int,
    studiedDates: List<String>,
    badges: List<StudyBadge>,
    onSimulateStreak: (Int) -> Unit,
    onClearSimulation: () -> Unit,
    onSearchChange: (String) -> Unit,
    onCategorySelect: (String?) -> Unit,
    onCourseSelect: (Course) -> Unit,
    onToggleOnline: () -> Unit,
    onToggleDataSaver: () -> Unit,
    onSyncTrigger: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf("All", "Mathematics", "Science", "English", "Social Studies")

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            text = "EduSync Dashboard",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = PrimaryBlue
                        )
                        Text(
                            text = "Student: $email",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onLogoutClick,
                        modifier = Modifier
                            .testTag("logout_button")
                            .background(MaterialTheme.colorScheme.errorContainer, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_root")
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Network simulation indicator
            item {
                OfflineIndicator(
                    isOnline = isOnline,
                    onToggle = onToggleOnline,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // 2. Data saver settings and usage trackers
            item {
                DataSaverToggle(
                    dataSaverActive = dataSaverActive,
                    onToggle = onToggleDataSaver
                )
            }

            item {
                DataUsageCounter(
                    bytesUsed = bytesUsed
                )
            }

            // 3. Store and forward synchronization panel
            item {
                SyncBanner(
                    pendingCount = pendingSyncCount,
                    isSyncing = isSyncing,
                    syncMessage = syncMessage,
                    syncFraction = syncFraction,
                    isOnline = isOnline,
                    onSyncTrigger = onSyncTrigger
                )
            }

            // Gamified Streak and Achievements Section
            item {
                StreakTrackerCard(
                    streakCount = streakCount,
                    studiedDates = studiedDates,
                    onSimulateStreak = onSimulateStreak,
                    onClearSimulation = onClearSimulation
                )
            }

            item {
                BadgesSection(
                    badges = badges
                )
            }

            // 4. Search and filters
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchChange,
                    placeholder = { Text("Search subjects or topics...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear Search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_bar")
                )
            }

            // 5. Category quick horizontal slider
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Subject Categories",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            val isSelected = (selectedCategory == category) || (selectedCategory == null && category == "All")
                            FilterChip(
                                selected = isSelected,
                                onClick = { onCategorySelect(if (category == "All") null else category) },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryBlue,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                        }
                    }
                }
            }

            // 6. Courses Listing Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Your Offline Courses",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${courses.size} available",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
            }

            if (courses.isEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "No courses",
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No matching courses found",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Try adjusting your search query or subject filters.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                items(courses) { course ->
                    val progress = allProgress.firstOrNull { it.courseId == course.id }
                    CourseItemCard(
                        course = course,
                        progress = progress,
                        onClick = { onCourseSelect(course) }
                    )
                }
            }
        }
    }
}

@Composable
fun CourseItemCard(
    course: Course,
    progress: UserProgress?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completionFraction = (progress?.progressPercentage ?: 0) / 100f

    // Pick dynamic color tag for categories
    val categoryColor = when (course.category) {
        "Mathematics" -> PrimaryBlue
        "Science" -> Color(0xFF8B5CF6)
        "English" -> SuccessGreen
        "Social Studies" -> AccentOrange
        else -> PrimaryBlue
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("course_card_${course.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Badge
                Box(
                    modifier = Modifier
                        .background(categoryColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = course.category,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }

                // Sync status indicator
                if (progress != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = if (progress.isSynced) Icons.Default.CheckCircle else Icons.Default.Refresh,
                            contentDescription = if (progress.isSynced) "Synced" else "Pending Sync",
                            tint = if (progress.isSynced) SuccessGreen else AccentOrange,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (progress.isSynced) "Synced" else "Pending Sync",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (progress.isSynced) SuccessGreen else AccentOrange
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = course.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = course.description,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (progress != null) "Watched ${progress.progressPercentage}%" else "Not Started",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                if (progress != null && progress.progressPercentage >= 100) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completed",
                        tint = SuccessGreen,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            LinearProgressIndicator(
                progress = { completionFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (completionFraction >= 1.0f) SuccessGreen else PrimaryBlue,
                trackColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (progress == null) PrimaryBlue else PrimaryBlue.copy(alpha = 0.1f),
                    contentColor = if (progress == null) Color.White else PrimaryBlue
                ),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .align(Alignment.End)
                    .height(36.dp)
            ) {
                Text(
                    text = if (progress == null) "Start Course" else "Resume Course",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

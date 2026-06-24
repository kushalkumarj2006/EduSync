package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.StudyBadge
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueLight
import com.example.ui.theme.SuccessGreen
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun StreakTrackerCard(
    streakCount: Int,
    studiedDates: List<String>,
    onSimulateStreak: (Int) -> Unit,
    onClearSimulation: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Generate the last 7 calendar days to show in the row
    val last7Days = remember(studiedDates) {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dayFormat = SimpleDateFormat("E", Locale.getDefault())
        (0..6).map { offset ->
            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -offset)
            val dateStr = sdf.format(cal.time)
            val label = dayFormat.format(cal.time).first().toString() // "M", "T", "W", "T", "F", "S", "S"
            val isToday = offset == 0
            Triple(dateStr, label, isToday)
        }.reversed() // chronological order
    }

    var showSimControls by remember { mutableStateOf(false) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_tracker_card")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(AccentOrange.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Streak",
                            tint = AccentOrange,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = "Daily Study Streak",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Study daily to keep the flame alive",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }

                // Interactive badge showing streak count
                Box(
                    modifier = Modifier
                        .background(
                            Brush.horizontalGradient(listOf(AccentOrange, AccentOrange.copy(alpha = 0.8f))),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "$streakCount ${if (streakCount == 1) "Day" else "Days"}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 7-Day interactive grid indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                last7Days.forEach { (dateStr, label, isToday) ->
                    val studied = studiedDates.contains(dateStr)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal,
                            color = if (isToday) PrimaryBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(
                                    if (studied) AccentOrange.copy(alpha = 0.15f)
                                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                                )
                                .border(
                                    width = if (isToday) 2.dp else 1.dp,
                                    color = if (studied) AccentOrange
                                    else if (isToday) PrimaryBlue.copy(alpha = 0.5f)
                                    else Color.Transparent,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (studied) {
                                Icon(
                                    imageVector = Icons.Default.Favorite,
                                    contentDescription = "Studied",
                                    tint = AccentOrange,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = if (isToday) "•" else "",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Simulation Expander Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSimControls = !showSimControls }
                    .background(
                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.04f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Streak & Achievement Simulation",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
                Icon(
                    imageVector = if (showSimControls) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (showSimControls) "Collapse" else "Expand",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(18.dp)
                )
            }

            // Expanded Sim Controls
            AnimatedVisibility(
                visible = showSimControls,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Manually populate preceding calendar days to test streak detection & unlock rewards instantly:",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSimulateStreak(3) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Text(
                                text = "Simulate 3 Days",
                                fontSize = 10.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { onSimulateStreak(5) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Text(
                                text = "Simulate 5 Days",
                                fontSize = 10.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = onClearSimulation,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                        ) {
                            Text(
                                text = "Clear Streak",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BadgesSection(
    badges: List<StudyBadge>,
    modifier: Modifier = Modifier
) {
    var selectedBadgeForDialog by remember { mutableStateOf<StudyBadge?>(null) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Milestones & Achievements",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Earn achievements for completing offline study actions",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
            }
            
            val unlockedCount = badges.count { it.isUnlocked }
            Box(
                modifier = Modifier
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "$unlockedCount/${badges.size} Unlocked",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }
        }

        // Horizontal scrolling belt of awards
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(badges) { badge ->
                BadgeItemCard(
                    badge = badge,
                    onBadgeClick = { selectedBadgeForDialog = badge }
                )
            }
        }
    }

    // Interactive Celebrate Dialog when badge clicked
    selectedBadgeForDialog?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDialog = null }
        )
    }
}

@Composable
fun BadgeItemCard(
    badge: StudyBadge,
    onBadgeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badgeIcon = getBadgeIcon(badge.iconName)
    val badgeColor = getBadgeColor(badge.iconName)

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .width(135.dp)
            .height(155.dp)
            .clickable { onBadgeClick() }
            .border(
                width = 1.dp,
                color = if (badge.isUnlocked) badgeColor.copy(alpha = 0.3f) else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                shape = RoundedCornerShape(16.dp)
            )
            .testTag("badge_card_${badge.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Icon Ring
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) badgeColor.copy(alpha = 0.12f)
                        else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = badgeIcon,
                    contentDescription = badge.name,
                    tint = if (badge.isUnlocked) badgeColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.25f),
                    modifier = Modifier
                        .size(28.dp)
                        .alpha(if (badge.isUnlocked) 1f else 0.5f)
                )
            }

            // Text labels
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = badge.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = if (badge.isUnlocked) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )

                Text(
                    text = if (badge.isUnlocked) "Unlocked! 🎉" else "Locked",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (badge.isUnlocked) SuccessGreen else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    textAlign = TextAlign.Center
                )
            }

            // Mini progress snippet
            Text(
                text = if (badge.isUnlocked) "Tap to view" else "Tap for info",
                fontSize = 9.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.35f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun BadgeDetailDialog(
    badge: StudyBadge,
    onDismiss: () -> Unit
) {
    val badgeIcon = getBadgeIcon(badge.iconName)
    val badgeColor = getBadgeColor(badge.iconName)

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("badge_detail_dialog_${badge.id}")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Giant Celebration Ring
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            if (badge.isUnlocked) badgeColor.copy(alpha = 0.12f)
                            else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
                        )
                        .border(
                            width = 2.dp,
                            color = if (badge.isUnlocked) badgeColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = badge.name,
                        tint = if (badge.isUnlocked) badgeColor else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                        modifier = Modifier.size(48.dp)
                    )
                }

                // Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = badge.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        textAlign = TextAlign.Center
                    )
                    
                    Box(
                        modifier = Modifier
                            .background(
                                if (badge.isUnlocked) SuccessGreen.copy(alpha = 0.15f) else Color.LightGray.copy(alpha = 0.2f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (badge.isUnlocked) "UNLOCKED!" else "LOCKED",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (badge.isUnlocked) SuccessGreen else Color.Gray
                        )
                    }
                }

                // Description
                Text(
                    text = badge.description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )

                Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.08f))

                // Progress Info
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "How to unlock:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                    Text(
                        text = badge.progressDescription,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (badge.isUnlocked) SuccessGreen else PrimaryBlue,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Text(
                        text = if (badge.isUnlocked) "Awesome!" else "Got it",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

// Map key string names to guaranteed standard vector icons
private fun getBadgeIcon(iconName: String): ImageVector {
    return when (iconName) {
        "star" -> Icons.Default.Star
        "check_circle" -> Icons.Default.CheckCircle
        "flash_on" -> Icons.Default.PlayArrow
        "cloud" -> Icons.Default.Check
        "signal_cellular" -> Icons.Default.Settings
        "favorite" -> Icons.Default.Favorite
        else -> Icons.Default.Star
    }
}

// Pick nice, bright, matching Material theme colors for our awards
private fun getBadgeColor(iconName: String): Color {
    return when (iconName) {
        "star" -> Color(0xFFF59E0B) // Amber Star
        "check_circle" -> SuccessGreen // Emerald Completed
        "flash_on" -> Color(0xFF8B5CF6) // Purple Quiz Scholar
        "cloud" -> Color(0xFF0EA5E9) // Sky Blue Offline
        "signal_cellular" -> PrimaryBlueLight // Accent Blue Data Saver
        "favorite" -> AccentOrange // Red/Orange Streaker
        else -> Color(0xFFF59E0B)
    }
}

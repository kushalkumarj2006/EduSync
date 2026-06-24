package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentOrange
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.SuccessGreen

@Composable
fun OfflineIndicator(
    isOnline: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isOnline) SuccessGreen.copy(alpha = 0.15f) else AccentOrange.copy(alpha = 0.15f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("network_status_card")
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.CheckCircle else Icons.Default.Warning,
                    contentDescription = if (isOnline) "Online" else "Offline",
                    tint = if (isOnline) SuccessGreen else AccentOrange,
                    modifier = Modifier.size(24.dp)
                )
                Column {
                    Text(
                        text = if (isOnline) "Connected Online" else "Offline Mode Active",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isOnline) "Changes sync automatically" else "Logs queued in local database (Room)",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )
                }
            }

            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isOnline) AccentOrange else PrimaryBlue,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("simulate_network_toggle")
            ) {
                Text(
                    text = if (isOnline) "Simulate Offline" else "Simulate Online",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun DataSaverToggle(
    dataSaverActive: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("data_saver_card")
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Data Saver",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Data Saver Mode",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (dataSaverActive) "Auto-compressing image assets (WebP) & limiting video streaming to 480p" else "High-fidelity assets & unlimited videos active",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        modifier = Modifier.widthIn(max = 220.dp)
                    )
                }
            }

            Switch(
                checked = dataSaverActive,
                onCheckedChange = { onToggle() },
                modifier = Modifier.testTag("data_saver_switch")
            )
        }
    }
}

@Composable
fun DataUsageCounter(
    bytesUsed: Long,
    limitBytes: Long = 52_428_800L, // 50MB budget
    modifier: Modifier = Modifier
) {
    val usedMegabytes = bytesUsed.toDouble() / (1024 * 1024)
    val limitMegabytes = limitBytes.toDouble() / (1024 * 1024)
    val fraction = (bytesUsed.toFloat() / limitBytes.toFloat()).coerceIn(0f, 1f)
    
    // Choose progress color depending on limits
    val progressColor = when {
        fraction > 0.9f -> Color.Red
        fraction > 0.7f -> AccentOrange
        else -> PrimaryBlue
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("data_usage_card")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Bandwidth Budget",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                )
                Text(
                    text = String.format("%.2f MB / %.1f MB", usedMegabytes, limitMegabytes),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = progressColor
                )
            }

            LinearProgressIndicator(
                progress = { fraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = progressColor,
                trackColor = progressColor.copy(alpha = 0.15f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Limit: 50MB/day (TRAI rural constraint)",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                )
                if (fraction > 0.9f) {
                    Text(
                        text = "Warning: Approaching limit",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Red
                    )
                }
            }
        }
    }
}

@Composable
fun SyncBanner(
    pendingCount: Int,
    isSyncing: Boolean,
    syncMessage: String,
    syncFraction: Float,
    isOnline: Boolean,
    onSyncTrigger: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (pendingCount > 0) AccentOrange.copy(alpha = 0.08f) else PrimaryBlue.copy(alpha = 0.05f)
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("sync_banner")
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(
                                if (pendingCount > 0) AccentOrange.copy(alpha = 0.15f) else PrimaryBlue.copy(alpha = 0.15f),
                                RoundedCornerShape(8.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sync Indicator",
                            tint = if (pendingCount > 0) AccentOrange else PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Text(
                            text = if (pendingCount > 0) "$pendingCount items pending upload" else "All data synced to cloud",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = if (pendingCount > 0) "Store-and-Forward engine active" else "Offline logs cleared",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                if (pendingCount > 0) {
                    IconButton(
                        onClick = onSyncTrigger,
                        enabled = !isSyncing && isOnline,
                        modifier = Modifier
                            .testTag("manual_sync_button")
                            .background(
                                if (isOnline) PrimaryBlue else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f),
                                RoundedCornerShape(20.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Trigger Sync Now",
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .then(if (isSyncing) Modifier.rotate(rotation) else Modifier)
                        )
                    }
                }
            }

            if (isSyncing) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = syncMessage,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = PrimaryBlue
                    )
                    LinearProgressIndicator(
                        progress = { syncFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = PrimaryBlue,
                        trackColor = PrimaryBlue.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

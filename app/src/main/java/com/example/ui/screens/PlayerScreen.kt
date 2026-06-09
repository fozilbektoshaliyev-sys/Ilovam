package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun PlayerScreen(
    title: String,
    isVideoOffline: Boolean,
    onClosePlayer: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var progressSeconds by remember { mutableStateOf(240) } // Start at 4 minutes
    val totalSeconds = 1440 // 24 minutes typical anime episode length

    var isBuffering by remember { mutableStateOf(false) }
    var selectedResolution by remember { mutableStateOf(if (isVideoOffline) "Oflayn 1080p Uz" else "FHD 1080p") }
    var isMenuExpanded by remember { mutableStateOf(false) }

    // Run custom clock timer in the player
    LaunchedEffect(isPlaying, isBuffering) {
        while (isPlaying && !isBuffering) {
            delay(1000)
            if (progressSeconds < totalSeconds) {
                progressSeconds += 1
                // Randomly simulate a brief 1-second buffer online only
                if (!isVideoOffline && (progressSeconds % 120 == 0) && (0..4).random() == 1) {
                    isBuffering = true
                    delay(1500)
                    isBuffering = false
                }
            } else {
                isPlaying = false
            }
        }
    }

    // Helper functions for timing string convertion
    fun formatTime(seconds: Int): String {
        val min = seconds / 60
        val sec = seconds % 60
        return String.format("%02d:%02d", min, sec)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Overlay Visualizing Simulated Video Content / Animations
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E1E2E).copy(alpha = 0.8f),
                            Color.Black
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (isBuffering) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Yuklanmoqda...",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = if (isVideoOffline) Icons.Default.OfflinePin else Icons.Default.MovieFilter,
                        contentDescription = "Playing content",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = if (isVideoOffline) "Oflayn rejimda ijro etilmoqda" else "Simulyatsiya drayveri onlayn oqim",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Top Controls Panel
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
                .safeDrawingPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                IconButton(onClick = onClosePlayer) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Yopish",
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (isVideoOffline) Color(0xFF2E7D32) else MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Text(
                                text = if (isVideoOffline) "OFLAYN" else "ONLAYN",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = "Uzbek tilida",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            }

            // Resolution selection trigger
            Box {
                TextButton(
                    onClick = { isMenuExpanded = true },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Sifat",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = selectedResolution, fontSize = 13.sp)
                }

                DropdownMenu(
                    expanded = isMenuExpanded,
                    onDismissRequest = { isMenuExpanded = false },
                    modifier = Modifier.background(MaterialTheme.colorScheme.surface)
                ) {
                    val resolutions = if (isVideoOffline) {
                        listOf("Oflayn Asl nusxa", "Oflayn 1080p Uz", "Oflayn Subtitr")
                    } else {
                        listOf("Ultra HD 4K", "FHD 1080p", "HD 720p", "Kamroq trafik (360p)")
                    }
                    resolutions.forEach { res ->
                        DropdownMenuItem(
                            text = { Text(text = res) },
                            onClick = {
                                selectedResolution = res
                                isMenuExpanded = false
                            }
                        )
                    }
                }
            }
        }

        // Bottom Controls HUD bar
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.9f))
                    )
                )
                .navigationBarsPadding()
                .padding(vertical = 16.dp, horizontal = 20.dp)
        ) {
            // Slider / Video Timeline
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTime(progressSeconds),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = progressSeconds.toFloat(),
                    onValueChange = { progressSeconds = it.toInt() },
                    valueRange = 0f..totalSeconds.toFloat(),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.primary,
                        activeTrackColor = MaterialTheme.colorScheme.primary,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                    )
                )
                Text(
                    text = formatTime(totalSeconds),
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Control Buttons Row (Rewind, Play, FastForward, Info)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info button (Teleskop / audio channels)
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Ovoz",
                        tint = Color.White
                    )
                }

                // Seek back 10 seconds
                IconButton(
                    onClick = {
                        progressSeconds = (progressSeconds - 10).coerceAtLeast(0)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "-10 soniya",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Play / Pause Circle FAB
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .clickable { isPlaying = !isPlaying },
                    tonalElevation = 6.dp
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Ijro",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                // Seek forward 10 seconds
                IconButton(
                    onClick = {
                        progressSeconds = (progressSeconds + 10).coerceAtMost(totalSeconds)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "+10 soniya",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Wide / Aspect mode indicator
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.Fullscreen,
                        contentDescription = "To'liq ekran",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

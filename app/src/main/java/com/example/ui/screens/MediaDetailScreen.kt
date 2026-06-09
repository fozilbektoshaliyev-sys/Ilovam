package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.MediaItem
import com.example.ui.MediaViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MediaDetailScreen(
    media: MediaItem,
    viewModel: MediaViewModel,
    onBack: () -> Unit
) {
    val downloads by viewModel.downloadedList.collectAsState()
    val isDownloaded = downloads.any { it.id == media.id && it.status == "COMPLETED" }
    val isDownloading = downloads.any { it.id == media.id && it.status == "DOWNLOADING" }
    val downloadingItem = downloads.find { it.id == media.id }

    val isUserVip by viewModel.isUserVip.collectAsState()
    var showVipUpgradeDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    if (showVipUpgradeDialog) {
        Dialog(onDismissRequest = { showVipUpgradeDialog = false }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(
                    width = 2.dp,
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF00E5FF), Color(0xFFFF007F), Color(0xFFFFD700))
                    )
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF0F172A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Glowing crown visual badge
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .background(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFFFD700).copy(alpha = 0.25f), Color.Transparent)
                                ),
                                shape = RoundedCornerShape(34.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "VIP Premium",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "VIP PREMIUM PASS",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Donghua va Animelar Dunyosiga Cheksiz Yo'llanma",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00E5FF),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Media Context Info card
                    Surface(
                        color = Color.White.copy(alpha = 0.04f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Maxsus kontent:",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "“${media.title}”",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Ushbu kontent ultra yuqori sifatli 3D formatda bo'lganligi sababli VIP tarifdagilar uchun ochiq.",
                                fontSize = 11.sp,
                                color = Color.LightGray.copy(alpha = 0.8f),
                                lineHeight = 15.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Privileges List
                    Column(
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val privileges = listOf(
                            "Mutlaqo reklamasiz (4K Ultra HD sifat)" to Color(0xFF00E5FF),
                            "Barcha premyeralarga birinchi bo'lib kirish" to Color(0xFFFF007F),
                            "Cheksiz yuklab olish va offline tomosha" to Color(0xFFFFD700)
                        )
                        privileges.forEach { (text, color) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .background(color.copy(alpha = 0.2f), RoundedCornerShape(4.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = color,
                                        modifier = Modifier.size(10.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = text,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Gradient glowing action button
                    Button(
                        onClick = {
                            showVipUpgradeDialog = false
                            viewModel.selectTab("profil")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Color(0xFF00E5FF), Color(0xFFFF007F))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VIP A'ZOLIKNI FAOLLASHTIRISH",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 1.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = { showVipUpgradeDialog = false },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text(
                            text = "YOPISH",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray.copy(alpha = 0.6f),
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero background poster with a deep dark gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            AsyncImage(
                model = media.imageUrl,
                contentDescription = media.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )
        }

        // Scrollable content layout
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 80.dp) // Space for action buttons
        ) {
            Spacer(modifier = Modifier.height(220.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                // Media Title
                Text(
                    text = media.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Stats / Badges Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Type Badge
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = media.type.uppercase(),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (media.isVipOnly) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFFD4AF37),
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(
                                    text = "VIP ONLY",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    // Rating
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = Color(0xFFFFB300),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = media.rating.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )

                    // Year
                    Text(
                        text = media.year,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    Text(
                        text = "•",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )

                    // Size
                    Text(
                        text = media.fileSize,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Genres flow
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    media.genre.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(text = tag, fontSize = 12.sp) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action controls buttons (Play and Download)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Main Play Button
                    Button(
                        onClick = {
                            if (media.isVipOnly && !isUserVip) {
                                showVipUpgradeDialog = true
                            } else {
                                viewModel.playOnline(media)
                            }
                        },
                        modifier = Modifier
                            .weight(1.2f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PlayArrow,
                            contentDescription = "Play"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tomosha qilish",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    // Download trigger button
                    if (isDownloaded) {
                        Button(
                            onClick = { /* Already complete, no-op */ },
                            modifier = Modifier
                                .weight(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2E7D32),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Yuklangan"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Yuklangan", fontSize = 13.sp)
                        }
                    } else if (isDownloading) {
                        Button(
                            onClick = { /* downloading */ },
                            modifier = Modifier
                                .weight(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "Yuklanmoqda ${downloadingItem?.progress ?: 0}%",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        OutlinedButton(
                            onClick = {
                                if (media.isVipOnly && !isUserVip) {
                                    showVipUpgradeDialog = true
                                } else {
                                    viewModel.startDownload(media)
                                }
                            },
                            modifier = Modifier
                                .weight(0.8f)
                                .height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Download,
                                contentDescription = "Yuklab olish"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Yuklab olish", fontSize = 13.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Studio Info Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = "Studio",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Studio / Ishlab chiqaruvchi",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                            )
                            Text(
                                text = media.studio,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Synopsis/Description
                Text(
                    text = "Tavsif",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = media.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Qismlar/Epizodlar Section
                Text(
                    text = if (media.type == "Film") "Film qismlari" else "Mavjud qismlar (Epizodlar)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(10.dp))
                
                val episodeCount = if (media.type == "Film") 1 else 12
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    for (i in 1..episodeCount) {
                        val episodeTitle = if (media.type == "Film") "To'liq qism" else "$i-qism"
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (media.isVipOnly && !isUserVip) {
                                        showVipUpgradeDialog = true
                                    } else {
                                        viewModel.playOnline(media, "${media.title} - $episodeTitle")
                                    }
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(14.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                                shape = RoundedCornerShape(8.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayArrow,
                                            contentDescription = "Ijro",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = episodeTitle,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (media.type == "Film") media.duration else "Uzbek tilida • 24 daqiqa",
                                            fontSize = 11.sp,
                                            color = Color.LightGray.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                                
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (media.isVipOnly) {
                                        Surface(
                                            color = Color(0xFFD4AF37).copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(6.dp),
                                            border = BorderStroke(1.dp, Color(0xFFD4AF37))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Stars,
                                                    contentDescription = "VIP",
                                                    tint = Color(0xFFD4AF37),
                                                    modifier = Modifier.size(10.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "VIP",
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    color = Color(0xFFD4AF37)
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                    }
                                    
                                    Icon(
                                        imageVector = Icons.Default.ChevronRight,
                                        contentDescription = "Batafsil",
                                        tint = Color.LightGray,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }

        // Back Action Floating Ribbon Button
        FilledIconButton(
            onClick = onBack,
            modifier = Modifier
                .safeDrawingPadding()
                .padding(16.dp)
                .align(Alignment.TopStart),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                contentColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Orqaga"
            )
        }
    }
}

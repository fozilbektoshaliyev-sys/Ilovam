package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.ui.MediaViewModel
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: MediaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkThemeSelected by viewModel.darkThemeSelected.collectAsState()

            MyApplicationTheme(darkTheme = darkThemeSelected) {
                var showSplashScreen by remember { mutableStateOf(true) }

                if (showSplashScreen) {
                    SplashScreen(onFinished = { showSplashScreen = false })
                } else {
                    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
                    val userEmail by viewModel.userEmail.collectAsState()
                    val adminPreviewAsUser by viewModel.adminPreviewAsUser.collectAsState()

                    if (!isLoggedIn) {
                    AuthScreen(viewModel = viewModel)
                } else if (userEmail == "fozilbektoshaliyev@gmail.com" && !adminPreviewAsUser) {
                    AdminDashboardScreen(viewModel = viewModel)
                } else {
                    val currentTab by viewModel.currentTab.collectAsState()
                    val selectedMediaItem by viewModel.selectedMediaItem.collectAsState()
                    val playingOfflineMedia by viewModel.playingOfflineMedia.collectAsState()
                    val playingOnlineMedia by viewModel.playingOnlineMedia.collectAsState()
                    val onlinePlaybackTitle by viewModel.onlinePlaybackTitle.collectAsState()

                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(modifier = Modifier.fillMaxSize()) {
                            // High-contrast, elegant admin toolbar overlay during user preview mode
                            if (userEmail == "fozilbektoshaliyev@gmail.com" && adminPreviewAsUser) {
                                Surface(
                                    color = Color(0xFF16222F),
                                    modifier = Modifier.fillMaxWidth().statusBarsPadding()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.AdminPanelSettings,
                                                contentDescription = null,
                                                tint = Color(0xFFFFA000),
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Foydalanuvchi ko'rinishi (Sinov rejimi)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Button(
                                            onClick = { viewModel.setAdminPreviewAsUser(false) },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                            modifier = Modifier.height(28.dp)
                                        ) {
                                            Text("Admin panelga qaytish", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }

                            // Regular Navigation Scaffold
                            Scaffold(
                                bottomBar = {
                                    NavigationBar(
                                        modifier = Modifier.testTag("bottom_nav_bar")
                                    ) {
                                        NavigationBarItem(
                                            selected = currentTab == "asosiy",
                                            onClick = { viewModel.selectTab("asosiy") },
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentTab == "asosiy") Icons.Filled.Home else Icons.Outlined.Home,
                                                    contentDescription = "Asosiy"
                                                )
                                            },
                                            label = { Text("Asosiy") },
                                            modifier = Modifier.testTag("tab_asosiy")
                                        )

                                        NavigationBarItem(
                                            selected = currentTab == "qidiruv",
                                            onClick = { viewModel.selectTab("qidiruv") },
                                            icon = {
                                                Icon(
                                                    imageVector = Icons.Filled.Search,
                                                    contentDescription = "Qidiruv"
                                                )
                                            },
                                            label = { Text("Qidiruv") },
                                            modifier = Modifier.testTag("tab_qidiruv")
                                        )

                                        NavigationBarItem(
                                            selected = currentTab == "yuklanmalar",
                                            onClick = { viewModel.selectTab("yuklanmalar") },
                                            icon = {
                                                val downloads by viewModel.downloadedList.collectAsState()
                                                val downloadingCount = downloads.count { it.status == "DOWNLOADING" }
                                                BadgedBox(
                                                    badge = {
                                                        if (downloadingCount > 0) {
                                                            Badge { Text("$downloadingCount") }
                                                        }
                                                    }
                                                ) {
                                                    Icon(
                                                        imageVector = if (currentTab == "yuklanmalar") Icons.Filled.CloudDownload else Icons.Outlined.CloudDownload,
                                                        contentDescription = "Yuklanmalar"
                                                    )
                                                }
                                            },
                                            label = { Text("Yuklanmalar") },
                                            modifier = Modifier.testTag("tab_yuklanmalar")
                                        )

                                        NavigationBarItem(
                                            selected = currentTab == "profil",
                                            onClick = { viewModel.selectTab("profil") },
                                            icon = {
                                                Icon(
                                                    imageVector = if (currentTab == "profil") Icons.Filled.Person else Icons.Outlined.Person,
                                                    contentDescription = "Profil"
                                                )
                                            },
                                            label = { Text("Profil") },
                                            modifier = Modifier.testTag("tab_profil")
                                        )
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    when (currentTab) {
                                        "asosiy" -> HomeScreen(viewModel = viewModel)
                                        "qidiruv" -> SearchScreen(viewModel = viewModel)
                                        "yuklanmalar" -> DownloadsScreen(viewModel = viewModel)
                                        "profil" -> ProfileScreen(viewModel = viewModel)
                                    }
                                }
                            }
                        }

                            // Slide-In overlay for Media Detailed content info
                            AnimatedVisibility(
                                visible = selectedMediaItem != null,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                selectedMediaItem?.let { media ->
                                    MediaDetailScreen(
                                        media = media,
                                        viewModel = viewModel,
                                        onBack = { viewModel.selectMedia(null) }
                                    )
                                }
                            }

                            // Fullscreen overlay video player for online streaming
                            AnimatedVisibility(
                                visible = playingOnlineMedia != null,
                                enter = fadeIn() + scaleIn(initialScale = 0.95f),
                                exit = fadeOut() + scaleOut(targetScale = 0.95f),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                playingOnlineMedia?.let { media ->
                                    PlayerScreen(
                                        title = onlinePlaybackTitle ?: media.title,
                                        isVideoOffline = false,
                                        onClosePlayer = { viewModel.playOnline(null) }
                                    )
                                }
                            }

                            // Fullscreen overlay video player for offline media playback
                            AnimatedVisibility(
                                visible = playingOfflineMedia != null,
                                enter = fadeIn() + scaleIn(initialScale = 0.95f),
                                exit = fadeOut() + scaleOut(targetScale = 0.95f),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                playingOfflineMedia?.let { download ->
                                    PlayerScreen(
                                        title = download.title,
                                        isVideoOffline = true,
                                        onClosePlayer = { viewModel.playOffline(null) }
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

package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MediaViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.provider.OpenableColumns

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminDashboardScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Forms States
    // 1. Add Media States defined first so launchers can capture them
    var newTitle by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }
    var newStudio by remember { mutableStateOf("") }
    var newYear by remember { mutableStateOf("2026") }
    var newGenres by remember { mutableStateOf("3D, Sarguzasht, Kultivatsiya") }
    var newTypeSelected by remember { mutableStateOf("Donghua") } // "Donghua", "Anime", "Film"
    var newIsVipOnly by remember { mutableStateOf(false) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var selectedVideoName by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var selectedImageName by remember { mutableStateOf("") }

    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedVideoUri = uri
            var name = "video.mp4"
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index >= 0) {
                            name = c.getString(index)
                        }
                    }
                }
            } catch (e: Exception) {
                name = uri.lastPathSegment ?: "video.mp4"
            }
            selectedVideoName = name
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            var name = "banner.jpg"
            try {
                val cursor = context.contentResolver.query(uri, null, null, null, null)
                cursor?.use { c ->
                    if (c.moveToFirst()) {
                        val index = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        if (index >= 0) {
                            name = c.getString(index)
                        }
                    }
                }
            } catch (e: Exception) {
                name = uri.lastPathSegment ?: "banner.jpg"
            }
            selectedImageName = name
        }
    }

    // ViewModel States
    val simulatedUsers by viewModel.simulatedUsers.collectAsState()
    val simulatedAdmins by viewModel.simulatedAdmins.collectAsState()
    val dynamicMediaCatalog by viewModel.dynamicMediaCatalog.collectAsState()
    val vipPriceOneMonth by viewModel.vipPriceOneMonth.collectAsState()
    val vipPriceSixMonths by viewModel.vipPriceSixMonths.collectAsState()
    val vipPriceTwelveMonths by viewModel.vipPriceTwelveMonths.collectAsState()

    // Sub-Screen/Section switcher: "overview", "add_media", "stats", "users", "admins", "vip"
    var activeAdminTab by remember { mutableStateOf("overview") }

    // 2. Add Admin
    var newAdminEmail by remember { mutableStateOf("") }
    var newAdminRole by remember { mutableStateOf("Kontent tahrirchi") }

    // 3. User Management helpers
    var userSearchQuery by remember { mutableStateOf("") }
    var showUserAddDialog by remember { mutableStateOf(false) }
    var addUserContact by remember { mutableStateOf("") }
    var addUserType by remember { mutableStateOf("Email") } // "Email" or "Telefon"

    // VIP Pricing overrides
    var vipPrice1mStr by remember { mutableStateOf(vipPriceOneMonth.toString()) }
    var vipPrice6mStr by remember { mutableStateOf(vipPriceSixMonths.toString()) }
    var vipPrice12mStr by remember { mutableStateOf(vipPriceTwelveMonths.toString()) }

    // Dynamic Telegram Support States
    val supportTelegram by viewModel.supportTelegram.collectAsState()
    var supportTelegramInput by remember { mutableStateOf(supportTelegram) }

    // Metrics for Statistics
    val totalUsers = simulatedUsers.size
    val vipCount = simulatedUsers.count { it.third == "VIP" }
    val standardCount = simulatedUsers.count { it.third == "Standard" }
    val bannedCount = simulatedUsers.count { it.third == "Banned" }
    val contentCount = dynamicMediaCatalog.size
    val totalViewsCount = dynamicMediaCatalog.fold(0) { sum, it -> sum + (it.title.hashCode() % 120 + 200).coerceAtLeast(12) }

    // Update VIP prices when tab state changes
    LaunchedEffect(activeAdminTab, vipPriceOneMonth, vipPriceSixMonths, vipPriceTwelveMonths, supportTelegram) {
        vipPrice1mStr = vipPriceOneMonth.toString()
        vipPrice6mStr = vipPriceSixMonths.toString()
        vipPrice12mStr = vipPriceTwelveMonths.toString()
        supportTelegramInput = supportTelegram
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0C1319)) // Deep Admin Navy Background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            
            // --- LUXURY ADMIN HEADER ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF16222F), Color(0xFF0C1319))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFA000).copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin",
                                tint = Color(0xFFFFA000),
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Admin Panel",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Tizim boshqaruvchisi: Fozilbek",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.6f)
                            )
                        }
                    }

                    // Dual Row for testing normal app preview vs logout
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        // User view test button
                        FilledTonalButton(
                            onClick = {
                                viewModel.setAdminPreviewAsUser(true)
                                Toast.makeText(context, "Foydalanuvchi rejimi sinab ko'rilmoqda", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0xFFFFA000).copy(alpha = 0.15f),
                                contentColor = Color(0xFFFFA000)
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Ko'rinish", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        // Logout Session
                        IconButton(
                            onClick = { 
                                viewModel.logout()
                                Toast.makeText(context, "Tizimdan chiqildi", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color.White.copy(alpha = 0.08f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ExitToApp,
                                contentDescription = "Chiqish",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            // Navigation/List selection banner if not in "overview" (Back button)
            if (activeAdminTab != "overview") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = { activeAdminTab = "overview" },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Ortga",
                            tint = Color(0xFFFFA000),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bosh admin menyusiga qaytish",
                            color = Color(0xFFFFA000),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // --- MAIN LIST DIRECTORY NAVIGATION OR DETAILS ---
            AnimatedContent(
                targetState = activeAdminTab,
                transitionSpec = {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                    slideOutHorizontally { width -> -width } + fadeOut()
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { targetTab ->
                when (targetTab) {
                    "overview" -> {
                        // Directory list format ("Ro'yhati shaklida") as requested
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 20.dp, vertical = 10.dp)
                        ) {
                            // High-contrast testing info card for the owner
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F62FE).copy(alpha = 0.15f)),
                                border = BorderStroke(1.5.dp, Color(0xFF0F62FE)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 20.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Tv,
                                            contentDescription = null,
                                            tint = Color(0xFFFF7A00),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            text = "ILOVANI TEKSHIRISH (USER REJIM)",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Siz hozirda Boshqaruv Panelidasiz. Ilovani yangi yorqin UZDIGITAL TV uslubidagi chiroyli dizaynini, darsliklar va kinolarni o'ynatishini hamda barcha bo'limlarini oddiy foydalanuvchi sifatida ko'rish va to'liq jonli test qilish uchun quyidagi tugmani bosing:",
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        lineHeight = 18.sp
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    Button(
                                        onClick = {
                                            viewModel.setAdminPreviewAsUser(true)
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F62FE)),
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(vertical = 12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PlayCircle,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "FOYDALANUVCHI SIFATIDA ONLINE TEKSHIRISH",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 11.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Text(
                                text = "Boshqaruv Ro'yxati",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
                            )

                            // 1. STATISTIKA CARD
                            AdminMenuRowItem(
                                title = "1. Tizim Statistikasi",
                                subtitle = "Foydalanuvchilar, VIP a'zolar, tomosha vaqti hisobotlari",
                                color = Color(0xFF1E88E5),
                                onClick = { activeAdminTab = "stats" }
                            )

                            // 2. ADD MEDIA CARD
                            AdminMenuRowItem(
                                title = "2. Yangi Anime / Donghua Qo'shish",
                                subtitle = "Katalogga yangi 3D animelar, sarlavha, janr, VIP toifalarini joylash",
                                color = Color(0xFF43A047),
                                onClick = { activeAdminTab = "add_media" }
                            )

                            // 3. USER MANAGEMENT CARD
                            AdminMenuRowItem(
                                title = "3. Foydalanuvchilarni Boshqarish",
                                subtitle = "Tizim a'zolari ro'yxati, bloklash, VIP ga aylantirish xizmati",
                                color = Color(0xFF00ACC1),
                                onClick = { activeAdminTab = "users" }
                            )

                            // 4. ADMIN MANAGEMENT CARD
                            AdminMenuRowItem(
                                title = "4. Admin Qo'shish & Ruxsatnomalar",
                                subtitle = "Yangi ma'murlar kiritish, xavfsizlik ruxsatlarini tahrirlash",
                                color = Color(0xFFD4AF37),
                                onClick = { activeAdminTab = "admins" }
                            )

                            // 5. VIP MANAGEMENT CARD
                            AdminMenuRowItem(
                                title = "5. VIP Paketlar Boshqaruvi",
                                subtitle = "VIP tariflarining 1, 6 va 12 oylik narxlarini sozlash va boshqarish",
                                color = Color(0xFFFFA000),
                                onClick = { activeAdminTab = "vip" }
                            )

                            // 6. SUPPORT TELEGRAM MANAGEMENT CARD
                            AdminMenuRowItem(
                                title = "6. Telegram Ko'makchi Sozlamalari",
                                subtitle = "Qo'llab-quvvatlash uchun Telegram foydalanuvchi nomini o'zgartirish (Hozirgi: $supportTelegram)",
                                color = Color(0xFF00ACC1),
                                onClick = { activeAdminTab = "support" }
                            )

                            Spacer(modifier = Modifier.height(30.dp))

                            // Overall status quick panel
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Tizim Holati Haqida",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Ushbu loyiha maxsus super-admin boshqaruv tizimi bilan boyitilgan. O'zgarishlar real vaqtda bazada saqlanadi va foydalanuvchilar oynalarida aks topadi.",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.6f),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }

                    "stats" -> {
                        // STATISTICS SUB-PANEL
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Tizim tahlili & Statistika",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // 2x2 statistics grid using flow layouts
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatDetailBox(
                                    title = "Jami foydalanuvchi",
                                    value = "$totalUsers ta",
                                    icon = Icons.Default.People,
                                    color = Color(0xFF00ACC1),
                                    modifier = Modifier.weight(1f)
                                )
                                StatDetailBox(
                                    title = "Faol VIP a'zolar",
                                    value = "$vipCount ta",
                                    icon = Icons.Default.WorkspacePremium,
                                    color = Color(0xFFFFB300),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                StatDetailBox(
                                    title = "Katalog hajmi",
                                    value = "$contentCount ta video",
                                    icon = Icons.Default.MovieFilter,
                                    color = Color(0xFF43A047),
                                    modifier = Modifier.weight(1f)
                                )
                                StatDetailBox(
                                    title = "Jami ko'rishlar soni",
                                    value = "$totalViewsCount ta",
                                    icon = Icons.Default.TrendingUp,
                                    color = Color(0xFF1E88E5),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            // Custom Simulated Line Graph representing monthly visitors
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Kunlik tashrifchilar o'sish dinamikasi (Iyul)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(14.dp))
                                    
                                    // Simulated simple progress chart lines representing dates
                                    listOf(
                                        "1-Iyul" to 320,
                                        "2-Iyul" to 420,
                                        "3-Iyul" to 580,
                                        "4-Iyul" to 820,
                                        "5-Iyul" to 1100,
                                        "6-Iyul" to 1420,
                                        "Bugun (7-Iyun)" to 1845
                                    ).forEach { (date, count) ->
                                        Row(
                                            modifier = Modifier.padding(vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = date, modifier = Modifier.width(90.dp), fontSize = 11.sp, color = Color.LightGray)
                                            Box(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(8.dp)
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(Color.White.copy(alpha = 0.05f))
                                            ) {
                                                val fillFraction = (count / 2000f).coerceIn(0.05f, 1f)
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(fillFraction)
                                                        .fillMaxHeight()
                                                        .background(
                                                            Brush.horizontalGradient(
                                                                colors = listOf(Color(0xFFFFA000), Color(0xFFD4AF37))
                                                            )
                                                        )
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "$count", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
                                border = BorderStroke(1.dp, Color(0xFFFFA000).copy(alpha = 0.15f))
                            ) {
                                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AttachMoney, contentDescription = null, tint = Color(0xFF43A047), modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text("Tahminiy oylik tushum: $${vipCount * vipPriceOneMonth}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                        Text("Ushbu ko'rsatkich barcha VIP foydalanuvchilarining 1 oylik obuna tarifiga qarab hisoblandi.", fontSize = 10.sp, color = Color.LightGray)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(40.dp))
                        }
                    }

                    "add_media" -> {
                        // ADD OR REMOVE multimedia catalog items
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Katalogga yangi anime yoki donghua qo'shish",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = newTitle,
                                onValueChange = { newTitle = it },
                                label = { Text("Mavzu nomi (Title)", color = Color.LightGray) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFA000),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newDesc,
                                onValueChange = { newDesc = it },
                                label = { Text("Tavsif (Description)", color = Color.LightGray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFA000),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = newStudio,
                                    onValueChange = { newStudio = it },
                                    label = { Text("Studio", color = Color.LightGray) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFA000),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                                OutlinedTextField(
                                    value = newYear,
                                    onValueChange = { newYear = it },
                                    label = { Text("Chor tili / Yili", color = Color.LightGray) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFA000),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = newGenres,
                                onValueChange = { newGenres = it },
                                label = { Text("Janrlar (Vergul bilan ajrating)", color = Color.LightGray) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFA000),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // MP4 Video Picker Section
                            Text(
                                text = "MP4 Video Fayli (Galereyadan) *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA000)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { videoPickerLauncher.launch("video/mp4") },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedVideoUri != null) Color(0xFF1B5E20).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selectedVideoUri != null) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (selectedVideoUri != null) Icons.Default.VideoFile else Icons.Default.CloudUpload,
                                        contentDescription = "Video",
                                        tint = if (selectedVideoUri != null) Color(0xFF81C784) else Color(0xFFFFA000),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (selectedVideoUri != null) "Tizimga ulandi: $selectedVideoName" else "Galereyadan MP4 fayl tanlash",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (selectedVideoUri != null) "Haqiqiy video turi: MP4 (Mahalliy URI)" else "Barcha anime qismlari turi MP4",
                                            fontSize = 10.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                    if (selectedVideoUri != null) {
                                        IconButton(onClick = { 
                                            selectedVideoUri = null 
                                            selectedVideoName = ""
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            // Photo Poster Cover Picker Section
                            Text(
                                text = "Poster Muqovasi (Galereyadan) *",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFA000)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { imagePickerLauncher.launch("image/*") },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedImageUri != null) Color(0xFF1B5E20).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f)
                                ),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = if (selectedImageUri != null) Color(0xFF4CAF50) else Color.White.copy(alpha = 0.15f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (selectedImageUri != null) Icons.Default.Image else Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Rasm",
                                        tint = if (selectedImageUri != null) Color(0xFF81C784) else Color(0xFFFFA000),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (selectedImageUri != null) "Tizimga ulandi: $selectedImageName" else "Galereyadan muqova rasmini tanlash",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (selectedImageUri != null) "Muqova rasm turi: JPEG/PNG" else "Tavsiya etilgan o'lcham: 600x400",
                                            fontSize = 10.sp,
                                            color = Color.LightGray
                                        )
                                    }
                                    if (selectedImageUri != null) {
                                        IconButton(onClick = { 
                                            selectedImageUri = null 
                                            selectedImageName = ""
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Delete",
                                                tint = Color.Red
                                            )
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            Text("Kategoriya (Turi) tanlang:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                listOf("Donghua", "Anime", "Film").forEach { cat ->
                                    val isSelected = newTypeSelected == cat
                                    Card(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { newTypeSelected = cat },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) Color(0xFFFFA000).copy(alpha = 0.2f) else Color.White.copy(alpha = 0.04f)
                                        ),
                                        border = BorderStroke(
                                            1.dp,
                                            if (isSelected) Color(0xFFFFA000) else Color.White.copy(alpha = 0.08f)
                                        )
                                    ) {
                                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                                            Text(text = cat, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color(0xFFFFA000) else Color.White)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = newIsVipOnly,
                                    onCheckedChange = { newIsVipOnly = it },
                                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFFA000))
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Ushbu video faqat VIP foydalanuvchilar uchun", fontSize = 12.sp, color = Color.White)
                            }
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (newTitle.trim().isEmpty()) {
                                        Toast.makeText(context, "Mavzu nomini kiriting!", Toast.LENGTH_SHORT).show()
                                    } else if (newDesc.trim().isEmpty()) {
                                        Toast.makeText(context, "Ma'lumot / tavsif qismini kiriting!", Toast.LENGTH_SHORT).show()
                                    } else if (selectedVideoUri == null) {
                                        Toast.makeText(context, "Iltimos, galereyadan MP4 video faylini yuklang / tanlang! 🎥", Toast.LENGTH_LONG).show()
                                    } else {
                                        val parseList = newGenres.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                        viewModel.addNewMedia(
                                            title = newTitle.trim(),
                                            description = newDesc.trim(),
                                            type = newTypeSelected,
                                            studio = newStudio.trim(),
                                            year = newYear.trim(),
                                            genres = parseList,
                                            isVipOnly = newIsVipOnly,
                                            videoUrl = selectedVideoUri.toString(),
                                            imageUrl = selectedImageUri?.toString()
                                        )
                                        Toast.makeText(context, "Muvaffaqiyatli qo'shildi: $newTitle 🎉", Toast.LENGTH_LONG).show()
                                        
                                        // Reset fields
                                        newTitle = ""
                                        newDesc = ""
                                        newStudio = ""
                                        selectedVideoUri = null
                                        selectedVideoName = ""
                                        selectedImageUri = null
                                        selectedImageName = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("KATALOGGA YANGI VIDEO QO'SHISH", fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Hozirdagi jami videolar ro'yxati (${dynamicMediaCatalog.size})", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            // Compact list representation of media to toggle VIP mode easily
                            dynamicMediaCatalog.take(15).forEach { item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(8.dp))
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = item.title, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = Color.White, maxLines = 1)
                                        Text(text = "${item.type} | ${item.studio}", fontSize = 10.sp, color = Color.LightGray)
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            text = if (item.isVipOnly) "Premium" else "Bepul",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (item.isVipOnly) Color(0xFFFFA000) else Color(0xFF43A047)
                                        )
                                        Switch(
                                            checked = item.isVipOnly,
                                            onCheckedChange = { viewModel.toggleMediaVipStatus(item.id) },
                                            modifier = Modifier.scale(0.7f)
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(45.dp))
                        }
                    }

                    "users" -> {
                        // DYNAMIC USERS DATABASE MANIPULATION
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Tizim Foydalanuvchilarni Boshqarish",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Barcha foydalanuvchilarning hisob statusini va ruxsatlarini tahrirlash",
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )

                            // Add simulated user banner button
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Search filter box
                                OutlinedTextField(
                                    value = userSearchQuery,
                                    onValueChange = { userSearchQuery = it },
                                    placeholder = { Text("Pochta yoki tel raqam qidiruv", color = Color.Gray, fontSize = 11.sp) },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFFFFA000),
                                        unfocusedBorderColor = Color.White.copy(alpha = 0.12f),
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = { showUserAddDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                                    modifier = Modifier.height(44.dp)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Yangi", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Simulated Users Table
                            LazyColumn(
                                modifier = Modifier.weight(1f).fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val filteredList = simulatedUsers.filter {
                                    userSearchQuery.isEmpty() || it.first.lowercase().contains(userSearchQuery.lowercase())
                                }

                                items(filteredList) { user ->
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
                                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Icon(
                                                        imageVector = if (user.second == "Email") Icons.Default.Email else Icons.Default.PhoneAndroid,
                                                        contentDescription = null,
                                                        tint = Color(0xFF0F62FE),
                                                        modifier = Modifier.size(16.dp)
                                                    )
                                                    Spacer(modifier = Modifier.width(8.dp))
                                                    Column {
                                                        Text(
                                                            text = user.first,
                                                            fontWeight = FontWeight.Bold,
                                                            fontSize = 13.sp,
                                                            color = Color.White
                                                        )
                                                        val uId = viewModel.getOrGenerateUserId(user.first)
                                                        Text(
                                                            text = "ID: $uId",
                                                            fontWeight = FontWeight.Black,
                                                            fontSize = 10.sp,
                                                            color = Color(0xFFFF7A00)
                                                        )
                                                    }
                                                }
                                                // Badge
                                                Surface(
                                                    color = when (user.third) {
                                                        "VIP" -> Color(0xFFFFA000).copy(alpha = 0.15f)
                                                        "Banned" -> Color(0xFFE53935).copy(alpha = 0.15f)
                                                        else -> Color.Gray.copy(alpha = 0.15f)
                                                    },
                                                    shape = RoundedCornerShape(4.dp)
                                                ) {
                                                    Text(
                                                        text = user.third,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = when (user.third) {
                                                            "VIP" -> Color(0xFFFFB300)
                                                            "Banned" -> Color(0xFFE53935)
                                                            else -> Color.LightGray
                                                        },
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))
                                            HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                                            Spacer(modifier = Modifier.height(8.dp))

                                            // Action Buttons for this user
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                    // Toggle VIP
                                                    AssistButton(
                                                        text = if (user.third == "VIP") "Standardga qaytarish" else "VIP qilish",
                                                        icon = Icons.Default.Stars,
                                                        color = Color(0xFFFFB300),
                                                        onClick = {
                                                            val next = if (user.third == "VIP") "Standard" else "VIP"
                                                            viewModel.setUserVipStatus(user.first, next)
                                                            Toast.makeText(context, "${user.first} statusi yangilandi!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    )

                                                    // Block/Ban toggle
                                                    AssistButton(
                                                        text = if (user.third == "Banned") "Aktivlashtirish" else "Bloklash",
                                                        icon = Icons.Default.Block,
                                                        color = Color(0xFFE53935),
                                                        onClick = {
                                                            viewModel.toggleUserBanStatus(user.first)
                                                            Toast.makeText(context, "Foydalanuvchi block holati o'zgartirildi!", Toast.LENGTH_SHORT).show()
                                                        }
                                                    )
                                                }

                                                // Deletion button
                                                IconButton(
                                                    onClick = {
                                                        if (user.first == "fozilbektoshaliyev@gmail.com") {
                                                            Toast.makeText(context, "Super adminni o'chirib bo'lmaydi!", Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            viewModel.deleteSimulatedUser(user.first)
                                                            Toast.makeText(context, "Foydalanuvchi o'chirildi", Toast.LENGTH_SHORT).show()
                                                        }
                                                    },
                                                    modifier = Modifier.size(28.dp)
                                                ) {
                                                    Icon(Icons.Default.DeleteOutline, contentDescription = "O'chirish", tint = Color.Gray)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(30.dp))
                        }
                    }

                    "admins" -> {
                        // ADMIN ADDITION AND PERMISSIONS LIST SCREEN
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Admin Qo'shish va Maxsus Ruxsatlar (Alohida)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // New admin registration form
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
                                border = BorderStroke(1.dp, Color(0xFFD4AF37).copy(alpha = 0.2f))
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Yangi Ma'mur (Admin) Qo'shish", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedTextField(
                                        value = newAdminEmail,
                                        onValueChange = { newAdminEmail = it },
                                        label = { Text("Ma'mur elektron pochtasi", color = Color.LightGray) },
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color(0xFFFFA000),
                                            focusedTextColor = Color.White,
                                            unfocusedTextColor = Color.White
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))

                                    Text("Ruxsat tipi (Admin ruxsatlari):", fontSize = 11.sp, color = Color.LightGray)
                                    Spacer(modifier = Modifier.height(4.dp))

                                    // Dropdown selection representations
                                    val rolesList = listOf("Kontent tahrirchi", "Foydalanuvchi boshqaruvchisi", "To'liq ruxsat (Super Admin)")
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        rolesList.forEach { role ->
                                            val isSelected = newAdminRole == role
                                            Card(
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .clickable { newAdminRole = role },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) Color(0xFFFFA000).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.04f)
                                                ),
                                                border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFA000) else Color.White.copy(alpha = 0.08f))
                                            ) {
                                                Box(
                                                    modifier = Modifier.padding(6.dp).fillMaxWidth(),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(text = role.substringBefore(" "), fontSize = 9.sp, fontWeight = FontWeight.Bold, color = if (isSelected) Color(0xFFFFA000) else Color.LightGray)
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Button(
                                        onClick = {
                                            if (newAdminEmail.trim().isEmpty() || !newAdminEmail.contains("@")) {
                                                Toast.makeText(context, "Iltimos pochtani to'g'ri to'ldiring!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                viewModel.addAdmin(newAdminEmail.trim(), newAdminRole)
                                                Toast.makeText(context, "${newAdminEmail.trim()} muvaffaqiyatli admin etib tayinlandi!", Toast.LENGTH_LONG).show()
                                                newAdminEmail = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ADMIN ETIB INOM ETISH", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Hozirdagi Jami Adminlar va Ularning Ruxsatlari", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                            Spacer(modifier = Modifier.height(10.dp))

                            // Interactive and manageable list of Admins
                            simulatedAdmins.forEach { admin ->
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f)),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = admin.first, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.White)
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Surface(
                                                color = Color(0xFFFFA000).copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = admin.second,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFFFFB300),
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        // Role modification or remove option
                                        if (admin.first != "fozilbektoshaliyev@gmail.com") {
                                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                                // Cycle Permission levels
                                                IconButton(
                                                    onClick = {
                                                        val nextRole = when (admin.second) {
                                                            "Kontent tahrirchi" -> "Foydalanuvchi boshqaruvchisi"
                                                            "Foydalanuvchi boshqaruvchisi" -> "To'liq ruxsat (Super Admin)"
                                                            else -> "Kontent tahrirchi"
                                                        }
                                                        viewModel.updateAdminRole(admin.first, nextRole)
                                                        Toast.makeText(context, "Admin ko'nikmasi o'zgartirildi!", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.Edit, contentDescription = "Ruxsat o'zgartirish", tint = Color.LightGray, modifier = Modifier.size(16.dp))
                                                }

                                                IconButton(
                                                    onClick = {
                                                        viewModel.deleteAdmin(admin.first)
                                                        Toast.makeText(context, "Admin huquqi o'chirildi", Toast.LENGTH_SHORT).show()
                                                    },
                                                    modifier = Modifier.size(32.dp)
                                                ) {
                                                    Icon(Icons.Default.RemoveCircleOutline, contentDescription = "Ruxsatni olish", tint = Color(0xFFE53935), modifier = Modifier.size(16.dp))
                                                }
                                            }
                                        } else {
                                            Text("Asosiy Boss", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(45.dp))
                        }
                    }

                    "vip" -> {
                        // VIP EXCLUSIVE SYSTEM BOSHQUROVI (pricing and trials)
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Premium VIP Obuna Narxlarini Sozlash",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            // Custom Input TextFields to edit VIP rates
                            OutlinedTextField(
                                value = vipPrice1mStr,
                                onValueChange = { vipPrice1mStr = it },
                                label = { Text("1 Oylik obuna narxi ($)", color = Color.LightGray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFA000),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = vipPrice6mStr,
                                onValueChange = { vipPrice6mStr = it },
                                label = { Text("6 Oylik obuna narxi ($)", color = Color.LightGray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFA000),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = vipPrice12mStr,
                                onValueChange = { vipPrice12mStr = it },
                                label = { Text("1 Yillik obuna narxi ($)", color = Color.LightGray) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFFFA000),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val val1 = vipPrice1mStr.toIntOrNull() ?: 10
                                    val val6 = vipPrice6mStr.toIntOrNull() ?: 50
                                    val val12 = vipPrice12mStr.toIntOrNull() ?: 90
                                    viewModel.updateVipPrices(val1, val6, val12)
                                    Toast.makeText(context, "VIP tarif narxlari yangilandi!", Toast.LENGTH_LONG).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA000)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("O'NAZART NARXLARNI YANGILASH", fontWeight = FontWeight.ExtraBold)
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Imtiyozlar & VIP Rejasi haqida", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "1. Har qanday 3D donghua xizmati faqat VIP-lar uchun ko'rinishi sozlanishi mumkin.\n\n" +
                                               "2. VIP bo'lgan foydalanuvchilar platformadagi barcha sarlavhalarni cheksiz sifatda hamda kesh/offline yuklash imkoni bilan ko'rishadi.",
                                        fontSize = 11.sp,
                                        color = Color.LightGray,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(45.dp))
                        }
                    }

                    "support" -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(scrollState)
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Texnik qo'llab-quvvatlash sozlamasi",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = supportTelegramInput,
                                onValueChange = { supportTelegramInput = it },
                                label = { Text("Qo'llab-quvvatlash Telegram foydalanuvchi nomi", color = Color.LightGray) },
                                placeholder = { Text("@Animanhwa3d_Support", color = Color.White.copy(alpha = 0.3f)) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF00ACC1),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    if (supportTelegramInput.trim().isEmpty()) {
                                        Toast.makeText(context, "Iltimos foydalanuvchi nomini bo'sh qoldirmang!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.updateSupportTelegram(supportTelegramInput.trim())
                                        Toast.makeText(context, "Telegram qo'llab-quvvatlash foydalanuvchi nomi yangilandi!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Default.CloudSync, contentDescription = null)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("TELEGRAM RAQAM / ISMNI YANGILASH", fontWeight = FontWeight.ExtraBold)
                            }

                            Spacer(modifier = Modifier.height(24.dp))
                            Text("Qo'llab-quvvatlash xizmati haqida", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.03f))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "1. Foydalanuvchi o'z profilidan texnik yordam tugmasini bosganida shu yerda sozlangan Telegram manziliga yo'naltiriladi.\n\n" +
                                               "2. Foydalanuvchi nomi '@' belgisi bilan boshlanishi tavsiya qilinadi (agar uningsiz kiritilsa ham tizim avtomatik qo'shib oladi).",
                                        fontSize = 11.sp,
                                        color = Color.LightGray,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(45.dp))
                        }
                    }
                }
            }
        }
    }

    // --- ADD DIALOG FORM FOR MANAGING COMPACT USERS ---
    if (showUserAddDialog) {
        AlertDialog(
            onDismissRequest = { showUserAddDialog = false },
            title = { Text("Yangi Foydalanuvchi Qo'shish") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = addUserContact,
                        onValueChange = { addUserContact = it },
                        label = { Text("Email yoki Telefon (contact)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Turi:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        listOf("Email", "Telefon").forEach { t ->
                            val isSel = addUserType == t
                            FilterChip(
                                selected = isSel,
                                onClick = { addUserType = t },
                                label = { Text(t) }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (addUserContact.trim().isEmpty()) {
                            Toast.makeText(context, "Iltimos ma'lumotni kiriting!", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.addSimulatedUser(addUserContact.trim(), addUserType)
                            Toast.makeText(context, "Foydalanuvchi muvaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show()
                            addUserContact = ""
                            showUserAddDialog = false
                        }
                    }
                ) {
                    Text("Qo'shish")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUserAddDialog = false }) {
                    Text("Bekor qilish")
                }
            }
        )
    }
}

// Custom Assistant Layout components
@Composable
fun AdminMenuRowItem(
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
        border = BorderStroke(1.dp, color.copy(alpha = 0.2f)),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, CircleShape)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    lineHeight = 15.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun StatDetailBox(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(22.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontSize = 11.sp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
fun AssistButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    onClick: () -> Unit
) {
    Surface(
        color = color.copy(alpha = 0.12f),
        border = BorderStroke(0.8.dp, color.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(6.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(10.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = text, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

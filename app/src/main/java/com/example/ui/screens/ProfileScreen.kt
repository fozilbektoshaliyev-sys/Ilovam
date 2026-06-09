package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.window.Dialog
import com.example.ui.MediaViewModel
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    val darkThemeActive by viewModel.darkThemeSelected.collectAsState()
    val watchTime by viewModel.watchTimeMinutes.collectAsState()
    val downloads by viewModel.downloadedList.collectAsState()

    // Persistent Email & Phone state from ViewModel
    val userEmail by viewModel.userEmail.collectAsState()
    val userPhone by viewModel.userPhone.collectAsState()
    val userNickname by viewModel.userNickname.collectAsState()
    val userLanguage by viewModel.userLanguage.collectAsState()
    val userId by viewModel.userId.collectAsState()

    // VIP Integration States
    val isUserVip by viewModel.isUserVip.collectAsState()
    val vipExpiryDate by viewModel.vipExpiryDate.collectAsState()
    val vipPriceOneMonth by viewModel.vipPriceOneMonth.collectAsState()
    val vipPriceSixMonths by viewModel.vipPriceSixMonths.collectAsState()
    val vipPriceTwelveMonths by viewModel.vipPriceTwelveMonths.collectAsState()
    val isAdminMode by viewModel.isAdminMode.collectAsState()

    // Users & Catalog state for Administrator
    val simulatedUsers by viewModel.simulatedUsers.collectAsState()
    val dynamicMediaCatalog by viewModel.dynamicMediaCatalog.collectAsState()

    // Dynamic Display Details
    val isSuperAdmin = userEmail == "fozilbektoshaliyev@gmail.com"
    val displayName = if (userNickname.isNotEmpty()) {
        userNickname
    } else if (isSuperAdmin) {
        "Fozilbek Toshaliyev"
    } else if (userEmail.isNotEmpty()) {
        userEmail.substringBefore("@")
    } else {
        "Foydalanuvchi"
    }
    val displayContact = if (userEmail.isNotEmpty()) userEmail else userPhone

    // Aggregate statistics
    val completedCount = downloads.count { it.status == "COMPLETED" }
    var totalSizeMb = downloads.fold(0) { acc, item ->
        val weight = when {
            item.fileSize.contains("GB") -> (item.fileSize.replace("GB", "").trim().toDoubleOrNull() ?: 1.0) * 1024
            item.fileSize.contains("MB") -> (item.fileSize.replace("MB", "").trim().toDoubleOrNull() ?: 200.0)
            else -> 150.0
        }
        acc + weight.toInt()
    }

    // Checkout UI dialog state flows
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }
    var editNickname by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPhone by remember { mutableStateOf("") }
    
    var selectedMonthsForCheckout by remember { mutableStateOf(1) }
    var checkoutPrice by remember { mutableStateOf(10) }
    var paymentOperatorSelected by remember { mutableStateOf("Payme") } // "Payme", "Click", "Uzum", "SMS"
    var isProcessingPayment by remember { mutableStateOf(false) }
    var isPaymentSuccess by remember { mutableStateOf(false) }

    val supportTelegram by viewModel.supportTelegram.collectAsState()

    // Admin price config text fields
    var inputPrice1m by remember { mutableStateOf("") }
    var inputPrice6m by remember { mutableStateOf("") }
    var inputPrice12m by remember { mutableStateOf("") }
    var isAdminMessageVisible by remember { mutableStateOf(false) }

    // Admin content creation form
    var contentTitle by remember { mutableStateOf("") }
    var contentDesc by remember { mutableStateOf("") }
    var contentStudio by remember { mutableStateOf("") }
    var contentYear by remember { mutableStateOf("2026") }
    var contentGenres by remember { mutableStateOf("3D, Sarguzasht") }
    var contentTypeSelected by remember { mutableStateOf("Donghua") } // "Donghua", "Anime", "Film"
    var contentIsVipOnly by remember { mutableStateOf(false) }

    // Sync admin fields with VM states when admin mode is loaded
    LaunchedEffect(isAdminMode, vipPriceOneMonth, vipPriceSixMonths, vipPriceTwelveMonths) {
        inputPrice1m = vipPriceOneMonth.toString()
        inputPrice6m = vipPriceSixMonths.toString()
        inputPrice12m = vipPriceTwelveMonths.toString()
    }

    // Simulated payment processing timer
    LaunchedEffect(isProcessingPayment) {
        if (isProcessingPayment) {
            delay(1500)
            isProcessingPayment = false
            isPaymentSuccess = true
        }
    }

    // Simulated checkout completion effect
    LaunchedEffect(isPaymentSuccess) {
        if (isPaymentSuccess) {
            delay(1500)
            viewModel.subscribeVip(selectedMonthsForCheckout)
            isPaymentSuccess = false
            showCheckoutDialog = false
        }
    }

    // Checkout Modal Dialog
    if (showCheckoutDialog) {
        Dialog(
            onDismissRequest = { 
                if (!isProcessingPayment && !isPaymentSuccess) showCheckoutDialog = false 
            }
        ) {
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
                    containerColor = Color(0xFF0F1722)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = "To'lov",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PREMIUM VIP TO'LOVI", 
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isProcessingPayment && !isPaymentSuccess) {
                        // Summary inner card
                        Surface(
                            color = Color.White.copy(alpha = 0.04f),
                            shape = RoundedCornerShape(14.dp),
                            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp).fillMaxWidth()) {
                                Text(
                                    text = "Tanlangan premium reja:",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$selectedMonthsForCheckout Oylik VIP Obuna",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                                Spacer(modifier = Modifier.height(6.dp))
                                val costUzsh = checkoutPrice * 12800
                                Text(
                                    text = "To'lov summasi: $$checkoutPrice (~${String.format("%,d", costUzsh)} so'm)",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            text = "To'lov operatorini tanlang:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.LightGray,
                            modifier = Modifier.align(Alignment.Start)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        // Operator cards
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val operators = listOf(
                                "Payme" to "Payme premium gateway", 
                                "Click" to "Click Up milliy to'lov", 
                                "Uzum" to "Uzum Pay hamyon", 
                                "SMS" to "SMS Direct to'lov"
                            )
                            
                            operators.forEach { (type, label) ->
                                val isSelected = paymentOperatorSelected == type
                                val operatorColor = when(type) {
                                    "Payme" -> Color(0xFF00E5FF)
                                    "Click" -> Color(0xFF00B4D8)
                                    "Uzum" -> Color(0xFFFF007F)
                                    else -> Color(0xFFFFD700)
                                }
                                
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { paymentOperatorSelected = type },
                                    border = BorderStroke(
                                        width = 1.dp,
                                        color = if (isSelected) operatorColor else Color.White.copy(alpha = 0.08f)
                                    ),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) 
                                            operatorColor.copy(alpha = 0.1f) 
                                            else Color.White.copy(alpha = 0.02f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(14.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = label,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = if (isSelected) Color.White else Color.LightGray.copy(alpha = 0.8f)
                                        )
                                        
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { paymentOperatorSelected = type },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = operatorColor,
                                                unselectedColor = Color.Gray
                                            ),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        
                        // Action buttons
                        Button(
                            onClick = { isProcessingPayment = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            val activeColor = when(paymentOperatorSelected) {
                                "Payme" -> Color(0xFF00E5FF)
                                "Click" -> Color(0xFF00B4D8)
                                "Uzum" -> Color(0xFFFF007F)
                                else -> Color(0xFFFFD700)
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(activeColor, activeColor.copy(alpha = 0.7f))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$paymentOperatorSelected ORQALI TO'LASH",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        TextButton(
                            onClick = { showCheckoutDialog = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("BEKOR QILISH", color = Color.LightGray.copy(alpha = 0.6f), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    } else if (isProcessingPayment) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val activeColor = when(paymentOperatorSelected) {
                                "Payme" -> Color(0xFF00E5FF)
                                "Click" -> Color(0xFF00B4D8)
                                "Uzum" -> Color(0xFFFF007F)
                                else -> Color(0xFFFFD700)
                            }
                            CircularProgressIndicator(color = activeColor, strokeWidth = 3.dp)
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = "$paymentOperatorSelected to'lov tizimiga xavfsiz ulanyapti...",
                                fontSize = 13.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Iltimos, sahifani yopmang",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    } else if (isPaymentSuccess) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .background(Color(0xFF43A047).copy(alpha = 0.15f), RoundedCornerShape(34.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Muvaffaqiyatli",
                                    tint = Color(0xFF43A047),
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "To'lov muvaffaqiyatli qabul qilindi!",
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF43A047),
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Sizning VIP premium obunangiz to'liq faollashtirildi. Reklamasiz va 4K Ultra HD formatidagi 3D donghualar olamiga xush kelibsiz!",
                                fontSize = 11.sp,
                                color = Color.LightGray.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                            
                            Button(
                                onClick = { showCheckoutDialog = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047)),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("RAHMAT, DAVOM ETISH", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }

    // Settings & Personalization Dialog
    if (showSettingsDialog) {
        var isClearingCache by remember { mutableStateOf(false) }
        var isSavingProfile by remember { mutableStateOf(false) }
        var pushNotificationsEnabled by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { if (!isSavingProfile && !isClearingCache) showSettingsDialog = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Gear",
                        tint = Color(0xFFFF7A00),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Tizim Sozlamalari",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // ---- SECTION 1: EDIT PROFILE ----
                    Text(
                        text = "PROFIL MA'LUMOTLARI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF7A00)
                    )

                    OutlinedTextField(
                        value = editNickname,
                        onValueChange = { editNickname = it },
                        label = { Text("Taxallus / Ism", fontSize = 11.sp) },
                        placeholder = { Text("Taxallus kiriting", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF7A00),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFFFF7A00)
                        )
                    )

                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Elektron pochta", fontSize = 11.sp) },
                        placeholder = { Text("pochta@example.com", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF7A00),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFFFF7A00)
                        )
                    )

                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Telefon raqami", fontSize = 11.sp) },
                        placeholder = { Text("+998 90 123 45 67", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFFF7A00),
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedLabelColor = Color(0xFFFF7A00)
                        )
                    )

                    Button(
                        onClick = {
                            if (editNickname.trim().isEmpty()) {
                                Toast.makeText(context, "Iltimos, taxallus kiriting!", Toast.LENGTH_SHORT).show()
                            } else {
                                isSavingProfile = true
                                viewModel.updateUserProfile(editNickname.trim(), editEmail.trim(), editPhone.trim())
                                Toast.makeText(context, "Profil ma'lumotlari muvaffaqiyatli saqlandi!", Toast.LENGTH_SHORT).show()
                                isSavingProfile = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF7A00)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 8.dp),
                        enabled = !isSavingProfile && !isClearingCache
                    ) {
                        if (isSavingProfile) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Profilni Saqlash", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))

                    // ---- SECTION 2: DARK / LIGHT MODE SWITCH ----
                    Text(
                        text = "INTERFEYS REJIMLARI",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF7A00)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { viewModel.toggleTheme() }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (darkThemeActive) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = "Theme Icon",
                                tint = if (darkThemeActive) Color(0xFFFF7A00) else Color(0xFFFFA000),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (darkThemeActive) "Tungi rejim (Qorong'u)" else "Kunduzgi rejim (Yorug')",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Mavzuni qo'lda o'zgartirish",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = darkThemeActive,
                            onCheckedChange = { viewModel.toggleTheme() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFFFF7A00),
                                checkedTrackColor = Color(0xFFFF7A00).copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }

                    // ---- SECTION 3: OTHER CONFIGURATIONS AND UTILITIES ----
                    Text(
                        text = "BOSHQALAR VA XIZMATLAR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF7A00)
                    )

                    // Language settings
                    Text(
                        text = "ILOVA TILI / App Language",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("O'zbekcha" to "UZ", "English" to "EN", "Русский" to "RU").forEach { (langKey, label) ->
                            val isSelected = userLanguage == langKey
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { 
                                        viewModel.updateLanguage(langKey)
                                        val greet = when(langKey) {
                                            "English" -> "App language changed to English!"
                                            "Русский" -> "Язык приложения изменен на русский!"
                                            else -> "Ilova tili o'zbek tiliga o'zgartirildi!"
                                        }
                                        Toast.makeText(context, greet, Toast.LENGTH_SHORT).show()
                                    },
                                border = BorderStroke(
                                    width = 1.5.dp,
                                    color = if (isSelected) Color(0xFFFF7A00) else Color.White.copy(alpha = 0.1f)
                                ),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) 
                                        Color(0xFFFF7A00).copy(alpha = 0.15f) 
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                )
                            ) {
                                Box(
                                    modifier = Modifier.padding(vertical = 10.dp).fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (isSelected) Color(0xFFFF7A00) else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Push Notifications Switch option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable { pushNotificationsEnabled = !pushNotificationsEnabled }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (pushNotificationsEnabled) Icons.Default.NotificationsActive else Icons.Default.NotificationsOff,
                                contentDescription = "Notify Icon",
                                tint = if (pushNotificationsEnabled) Color(0xFF43A047) else Color.Gray,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Tezkor bildirishnomalar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Yangi premeralar va donghua xabarlari",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Switch(
                            checked = pushNotificationsEnabled,
                            onCheckedChange = { pushNotificationsEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color(0xFF43A047),
                                checkedTrackColor = Color(0xFF43A047).copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.scale(0.8f)
                        )
                    }

                    // Simulated Cache Clear Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .clickable(enabled = !isClearingCache && !isSavingProfile) {
                                isClearingCache = true
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "Trash Icon",
                                tint = Color(0xFFE53935),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Ilova keshini tozalash",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Offline yuklanmagan kesh va vaqtinchalik xotira",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        if (isClearingCache) {
                            CircularProgressIndicator(
                                color = Color(0xFFE53935), 
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                            LaunchedEffect(Unit) {
                                delay(1200)
                                isClearingCache = false
                                Toast.makeText(context, "Ilova keshi muvaffaqiyatli tozalandi! 🧹", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.NavigateNext,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f), modifier = Modifier.padding(vertical = 4.dp))

                    // Simulated LOGOUT Button inside SettingsDialog
                    Button(
                        onClick = {
                            viewModel.logout()
                            showSettingsDialog = false
                            Toast.makeText(context, "Tizimdan muvaffaqiyatli chiqdingiz! 🚪", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(vertical = 10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "TIZIMDAN CHIQISH 🚪",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showSettingsDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("YOPISH", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        )
    }

    val scrollState = rememberScrollState()
    val adminPreviewAsUser by viewModel.adminPreviewAsUser.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isSuperAdmin && adminPreviewAsUser) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                border = BorderStroke(1.2.dp, Color(0xFF00ACC1)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Foydalanuvchi sinov rejimi faol",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00ACC1)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Asosiy oynalarni ko'rib bo'lib, admin boshqaruviga qayta olasiz.",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }
                    Button(
                        onClick = { viewModel.setAdminPreviewAsUser(false) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("Yopish", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // App Header Name
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Mening Profilim",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold
            )
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Visual VIP glow badge if active
                if (isUserVip) {
                    Surface(
                        color = Color(0xFFFFA000),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "VIP Active",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "VIP FAOL",
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                // Settings Button
                IconButton(
                    onClick = { 
                        editNickname = userNickname.ifEmpty { displayName }
                        editEmail = userEmail
                        editPhone = userPhone
                        showSettingsDialog = true 
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Sozlamalar",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Visual Profile Card with beautiful avatar background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = if (isUserVip) {
                            listOf(Color(0xFFD4AF37), Color(0xFFC5A02F), Color(0xFFAA7E10))
                        } else {
                            listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.tertiary)
                        }
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular Avatar Card
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.White.copy(alpha = 0.25f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isSuperAdmin) "F" else displayName.take(1).uppercase(),
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Profile descriptions
                Column {
                    Text(
                        text = displayName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = displayContact,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.82f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (userId.isNotEmpty()) {
                            Surface(
                                color = Color(0xFFFF7A00).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp),
                                border = BorderStroke(1.dp, Color(0xFFFF7A00).copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "ID: $userId",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFF7A00),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                        }
                        Surface(
                            color = if (isUserVip) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.25f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (isUserVip) "VIP LUXE STATUS" else "STANDART A'ZO",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Statistics Cards layout
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stat 1: Watch Time
            StatCard(
                title = "Tomosha vaqti",
                value = "$watchTime daqiqa",
                modifier = Modifier.weight(1f)
            )

            // Stat 2: Offline items
            StatCard(
                title = "Fayllar soni",
                value = "$completedCount ta",
                modifier = Modifier.weight(1f)
            )

            // Stat 3: Size
            StatCard(
                title = "Kesh hajmi",
                value = if (totalSizeMb >= 1024) String.format("%.2f GB", totalSizeMb / 1024f) else "$totalSizeMb MB",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ================= SECTION: GLOWING PREMIUM VIP REDESIGN =================
        Text(
            text = "VIP PREMIUM OBUNA TIZIMI",
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFD4AF37),
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1722)),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = if (isUserVip) {
                        listOf(Color(0xFFFFD700), Color(0xFFFF8C00))
                    } else {
                        listOf(Color(0xFF00E5FF), Color(0xFFFF007F))
                    }
                )
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (isUserVip) {
                    // VIP Info Dashboard Active (Sleek premium dark golden layout)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(
                                    brush = Brush.radialGradient(
                                        colors = listOf(Color(0xFFFFD700).copy(alpha = 0.25f), Color.Transparent)
                                    ),
                                    shape = RoundedCornerShape(27.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Stars,
                                contentDescription = "Active",
                                tint = Color(0xFFFFD700),
                                modifier = Modifier.size(34.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Surface(
                                color = Color(0xFFFFD700).copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, Color(0xFFFFD700)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "VIP PREMIUM • FAOL",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFFFFD700),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Premium obuna muddatingiz:",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = vipExpiryDate,
                                fontSize = 15.sp,
                                color = Color(0xFF00FFCC),
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.08f))
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Sizda faollashtirilgan premium imtiyozlar:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    // Bullet Privileges
                    val privilegesList = listOf(
                        "Reklamasiz cheksiz 3D donghualarni tomosha qilish",
                        "Ultra HD & 4K o'ta yuqori tasvir oqimi sifati",
                        "Cheksiz tezkor yuklab olish va mutlaqo offline kesh",
                        "Barcha 3D premium premyeralarga birinchi bo'lib ruxsat"
                    )
                    privilegesList.forEach { priv ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 5.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .background(Color(0xFFFFD700).copy(alpha = 0.15f), RoundedCornerShape(4.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color(0xFFFFD700),
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = priv, fontSize = 11.sp, fontWeight = FontWeight.Medium, color = Color.LightGray)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = { viewModel.cancelVip() },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFFF5252)),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.2.dp, Color(0xFFFF5252).copy(alpha = 0.45f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Obunani vaqtincha to'xtatish (Simulyatsiya)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    // VIP standard landing with 3 premium cards layout
                    Text(
                        text = "Animanhwa 3D premium a'zosi bo'ling hamda 4K HD sifatda barcha premera va donghualarni reklamasiz tomosha qiling!",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Highly professional, elegant plan components
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // 1 Month Plan
                        PlanPremiumBox(
                            title = "1 Oylik Standart Obuna",
                            desc = "Barcha 3D donghualarga 30 kunlik kirish",
                            price = "$$vipPriceOneMonth",
                            badge = null,
                            icon = Icons.Default.WorkspacePremium,
                            neonColor = Color(0xFF00E5FF),
                            bgColor = Color(0xFF0F1B2C),
                            onClick = {
                                selectedMonthsForCheckout = 1
                                checkoutPrice = vipPriceOneMonth
                                showCheckoutDialog = true
                            }
                        )

                        // 6 Month Plan (Best Seller)
                        PlanPremiumBox(
                            title = "6 Oylik Tejamkor Plan",
                            desc = "Cheksiz 180 kunlik 4K ravshanlik kanali",
                            price = "$$vipPriceSixMonths",
                            badge = "ENG OMMABOP",
                            icon = Icons.Default.ElectricBolt,
                            neonColor = Color(0xFFFF007F),
                            bgColor = Color(0xFF1E0E1B),
                            onClick = {
                                selectedMonthsForCheckout = 6
                                checkoutPrice = vipPriceSixMonths
                                showCheckoutDialog = true
                            }
                        )

                        // 12 Month Plan (Ultra Value)
                        PlanPremiumBox(
                            title = "1 Yillik Maxsus VIP Pass",
                            desc = "365 kunlik to'liq premium sarlavhalar va kesh",
                            price = "$$vipPriceTwelveMonths",
                            badge = "ENG YUQORI CHEGIRMA",
                            icon = Icons.Default.Stars,
                            neonColor = Color(0xFFFFD700),
                            bgColor = Color(0xFF24190F),
                            onClick = {
                                selectedMonthsForCheckout = 12
                                checkoutPrice = vipPriceTwelveMonths
                                showCheckoutDialog = true
                            }
                        )
                    }
                }
            }
        }

        // ================= CONFIGURED SUPPORT LINK SECTION =================
        Spacer(modifier = Modifier.height(14.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF15222E)),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Savol yoki muammolar bormi? Texnik ko'mak:",
                    fontSize = 11.sp,
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                // Clicking this allows contacting support telegram dynamically
                val supportName = supportTelegram
                Button(
                    onClick = {
                        val telegramUsername = supportName.replace("@", "").trim()
                        Toast.makeText(context, "Telegram ko'mak: $supportName ochilmoqda...", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACC1)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.SupportAgent, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Telegram orqali yordam ($supportName)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- SECTION: MANDATORY RESTRICTED ADMINISTRATOR ENGINE ---
        if (isSuperAdmin) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ADMINISTRATOR PANELI (MUKAMMAL)",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Admin panel", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = isAdminMode,
                        onCheckedChange = { viewModel.toggleAdminMode() },
                        modifier = Modifier.scale(0.8f).testTag("admin_mode_switch")
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = isAdminMode,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column {
                    // Sub-Part A: Prices edit Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "VIP Tarif Narxlarini Sozlash",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ushbu paneldan admin tarif narxlarini real vaqtda o'zgartira oladi. Narxlar o'zgarishi profil obuna tugmalarida darhol aks etadi.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                lineHeight = 14.sp
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = inputPrice1m,
                                onValueChange = { inputPrice1m = it },
                                label = { Text("1 Oylik obuna narxi ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("admin_price_1m"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = inputPrice6m,
                                onValueChange = { inputPrice6m = it },
                                label = { Text("6 Oylik obuna narxi ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("admin_price_6m"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = inputPrice12m,
                                onValueChange = { inputPrice12m = it },
                                label = { Text("1 Yillik obuna narxi ($)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth().testTag("admin_price_12m"),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary
                                )
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    val p1 = inputPrice1m.toIntOrNull() ?: 10
                                    val p6 = inputPrice6m.toIntOrNull() ?: 50
                                    val p12 = inputPrice12m.toIntOrNull() ?: 90
                                    viewModel.updateVipPrices(p1, p6, p12)
                                    isAdminMessageVisible = true
                                    Toast.makeText(context, "Tarif narxlari o'zgartirildi!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.fillMaxWidth().testTag("admin_save_prices_button")
                            ) {
                                Text("Yangi narxlarni saqlash")
                            }

                            if (isAdminMessageVisible) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Saved",
                                        tint = Color(0xFF2E7D32),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "VIP narxi muvaffaqiyatli o'zgartirildi!",
                                        fontSize = 11.sp,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    // Sub-Part B: ADDING DYNAMIC MULTIMEDIA CATALOG ITEM
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Katalogga Yangi Video Qo'shish",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Ushbu bo'limdan tizim katalogiga yangi 3D animelar qo'shishingiz mumkin, ular darhol Asosiy sahifada paydo bo'ladi.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            OutlinedTextField(
                                value = contentTitle,
                                onValueChange = { contentTitle = it },
                                label = { Text("Video sarlavhasi (Title)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = contentDesc,
                                onValueChange = { contentDesc = it },
                                label = { Text("Tavsif (Description)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = contentStudio,
                                    onValueChange = { contentStudio = it },
                                    label = { Text("Studio") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = contentYear,
                                    onValueChange = { contentYear = it },
                                    label = { Text("Yil") },
                                    singleLine = true,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = contentGenres,
                                onValueChange = { contentGenres = it },
                                label = { Text("Janrlar (Vergullar bilan ajratilgan)") },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(10.dp))

                            // Selector buttons for type
                            Text("Turi (Category):", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("Donghua", "Anime", "Film").forEach { cat ->
                                    val isSelected = contentTypeSelected == cat
                                    Button(
                                        onClick = { contentTypeSelected = cat },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                                        ),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = cat,
                                            fontSize = 11.sp,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = contentIsVipOnly,
                                    onCheckedChange = { contentIsVipOnly = it }
                                )
                                Text("Ushbu video faqat VIP foydalanuvchilar ko'rishi uchun (Faqat VIP)", fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    if (contentTitle.isEmpty()) {
                                        Toast.makeText(context, "Iltimos, sarlavhani to'ldiring!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        val genreList = contentGenres.split(",").map { it.trim() }
                                        viewModel.addNewMedia(
                                            title = contentTitle,
                                            description = contentDesc,
                                            type = contentTypeSelected,
                                            studio = contentStudio,
                                            year = contentYear,
                                            genres = genreList,
                                            isVipOnly = contentIsVipOnly
                                        )
                                        Toast.makeText(context, "$contentTitle katalogga muvaffaqiyatli qo'shildi!", Toast.LENGTH_LONG).show()
                                        // Reset fields
                                        contentTitle = ""
                                        contentDesc = ""
                                        contentStudio = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Video katalogga qo'shish")
                            }
                        }
                    }

                    // Sub-Part C: TOGGLING VIP STATUS OF EXISTING VIDEOS IN REAL TIME
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Videolarning VIP Statusini Boshqarish",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Katalogdagi har qanday filmning VIP rejimini darhol o'chiring yoki yoqing. O'zgarishlar barcha foydalanuvchilar ekranida real vaqtda ko'rinadi.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 12.dp)
                            )

                            // Quick interactive list of videos
                            dynamicMediaCatalog.take(12).forEach { media ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = media.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(text = "Id: ${media.id} | ${media.type}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (media.isVipOnly) "VIP" else "Bepul",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (media.isVipOnly) Color(0xFFC5A02F) else Color(0xFF2E7D32)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Switch(
                                            checked = media.isVipOnly,
                                            onCheckedChange = { viewModel.toggleMediaVipStatus(media.id) },
                                            modifier = Modifier.scale(0.7f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Sub-Part D: SIMULATED USERS MANAGEMENT
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Tizim Foydalanuvchilari Ro'yxati (Simulyatsiya) 👥",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Faol yoki ro'yxatdan o'tgan foydalanuvchilarning tahlili.",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            // Users mapping
                            simulatedUsers.forEach { userTuple ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (userTuple.second == "Email") Icons.Default.Email else Icons.Default.PhoneAndroid,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = userTuple.first, fontSize = 11.sp)
                                    }
                                    Surface(
                                        color = if (userTuple.third == "VIP") Color(0xFFFFA000).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = userTuple.third,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (userTuple.third == "VIP") Color(0xFFFFB300) else Color.Gray,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // App Footer versioning details
        Column(
            modifier = Modifier.fillMaxWidth().padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AniManhwa3D Uz - Birinchi Maxsus Milliy Markaz",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
            Text(
                text = "Versiya v1.0.6 - Premium VIP Edition",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlanOptionRow(
    title: String,
    price: String,
    onBuy: () -> Unit
) {
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
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Real vaqtda sozlangan admin narxi",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFA000)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(text = "$price", fontSize = 11.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
fun PlanPremiumBox(
    title: String,
    desc: String,
    price: String,
    badge: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    neonColor: Color,
    bgColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.2.dp, neonColor.copy(alpha = 0.45f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1.3f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circle glowing icon badge
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(neonColor.copy(alpha = 0.12f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = neonColor,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    if (badge != null) {
                        Surface(
                            color = neonColor,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Text(
                                text = badge,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = desc,
                        fontSize = 9.sp,
                        color = Color.LightGray.copy(alpha = 0.8f),
                        lineHeight = 12.sp
                    )
                }
            }
            
            // Glowing neon accent button
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .width(82.dp)
                    .height(36.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(neonColor, neonColor.copy(alpha = 0.7f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = price,
                        color = Color.Black,
                        fontWeight = FontWeight.Black,
                        fontSize = 11.sp,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(72.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1
            )
        }
    }
}

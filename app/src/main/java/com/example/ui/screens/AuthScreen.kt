package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MediaViewModel
import kotlinx.coroutines.delay

@Composable
fun AuthScreen(
    viewModel: MediaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isEmailTab by remember { mutableStateOf(true) }
    
    // Email credentials
    var emailInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var emailCodeSent by remember { mutableStateOf(false) }
    var emailGeneratingCode by remember { mutableStateOf("") }
    var typedEmailCode by remember { mutableStateOf("") }
    var emailRequestingActive by remember { mutableStateOf(false) }

    // Phone credentials
    var phoneInput by remember { mutableStateOf("+998 ") }
    var smsCodeSent by remember { mutableStateOf(false) }
    var smsGeneratingCode by remember { mutableStateOf("") }
    var typedSmsCode by remember { mutableStateOf("") }
    var smsRequestingActive by remember { mutableStateOf(false) }

    // Keep logged in setting
    var rememberMeSetting by remember { mutableStateOf(true) }

    // Error feedbacks
    var authErrorText by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
    ) {
        // High-contrast Ambient Background Illustration
        androidx.compose.foundation.Image(
            painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.img_welcome_hero),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
            alpha = 0.22f
        )

        // Translucent overlay layout
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F62FE).copy(alpha = 0.35f),
                            Color(0xFF070B16).copy(alpha = 0.90f),
                            Color(0xFF070B16).copy(alpha = 0.99f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // App Identity Logo Header
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = "AniManhwa 3D Uzbek Logo",
                    tint = Color(0xFFD4AF37),
                    modifier = Modifier.size(46.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AniManhwa3D Uz",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )

            Text(
                text = "Birinchi milliy 3D donghua va anime portali",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Auth Panel Card Container
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_form_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White.copy(alpha = 0.08f)
                ),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Kirish / Ro'yxatdan o'tish",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Dual Segmented Tab Selection Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.3f))
                            .padding(2.dp)
                    ) {
                        // Email selection tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isEmailTab) Color.White.copy(alpha = 0.18f) else Color.Transparent)
                                .clickable { 
                                    isEmailTab = true 
                                    authErrorText = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = null,
                                    tint = if (isEmailTab) Color.White else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "E-mail",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isEmailTab) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }

                        // Phone selection tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (!isEmailTab) Color.White.copy(alpha = 0.18f) else Color.Transparent)
                                .clickable { 
                                    isEmailTab = false 
                                    authErrorText = ""
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Phone,
                                    contentDescription = null,
                                    tint = if (!isEmailTab) Color.White else Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Tel / SMS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isEmailTab) Color.White else Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                     if (isEmailTab) {
                        // --- EMAIL PATHWAYS WITH SIMULATED EMAIL OTP ---
                        OutlinedTextField(
                            value = emailInput,
                            onValueChange = { 
                                emailInput = it
                                authErrorText = ""
                            },
                            label = { Text("Elektron pochta manzili", color = Color.White.copy(alpha = 0.6f)) },
                            placeholder = { Text("misol@gmail.com", color = Color.White.copy(alpha = 0.3f)) },
                            leadingIcon = { Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = Color.LightGray) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                cursorColor = Color(0xFFD4AF37)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Button to trigger simulator email OTP code
                        if (emailRequestingActive) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFD4AF37), modifier = Modifier.size(24.dp))
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (emailInput.isEmpty() || !emailInput.contains("@")) {
                                        authErrorText = "Iltimos, to'g'ri elektron pochta manzilini kiriting!"
                                    } else {
                                        emailRequestingActive = true
                                        authErrorText = ""
                                        // Generate 6 digit code for email
                                        val randomizedCode = (100000..999999).random().toString()
                                        emailGeneratingCode = randomizedCode
                                        
                                        emailRequestingActive = false
                                        emailCodeSent = true
                                        Toast.makeText(context, "Email tasdiqlash kodi yuborildi!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = if (emailCodeSent) "Tasdiqlash kodini qayta yuborish" else "Email tasdiqlash kodini olish",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Simulated Email OTP message badge banner
                        if (emailCodeSent) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1565C0).copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, Color(0xFF1565C0).copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = Color(0xFF2196F3),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Email Pochta Simulyatori: Sizning $emailInput pochtangizga tasdiqlash kodi yuborildi: $emailGeneratingCode",
                                        fontSize = 10.sp,
                                        color = Color(0xFF90CAF9),
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = typedEmailCode,
                                onValueChange = { 
                                    typedEmailCode = it
                                    authErrorText = ""
                                },
                                label = { Text("Email Tasdiqlash Kodi", color = Color.White.copy(alpha = 0.6f)) },
                                placeholder = { Text("Kodni kiriting (Masalan: $emailGeneratingCode)", color = Color.White.copy(alpha = 0.3f)) },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = Color.LightGray) },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD4AF37),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("auth_email_code_input")
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                    } else {
                        // --- TELEPHONE PATHWAYS WITH SIMULATED SMS SMS ---
                        OutlinedTextField(
                            value = phoneInput,
                            onValueChange = { 
                                if (it.startsWith("+998 ")) {
                                    phoneInput = it
                                }
                                authErrorText = ""
                            },
                            label = { Text("Telefon raqami", color = Color.White.copy(alpha = 0.6f)) },
                            placeholder = { Text("+998 90 123 45 67", color = Color.White.copy(alpha = 0.3f)) },
                            leadingIcon = { Icon(Icons.Default.PhoneIphone, contentDescription = null, tint = Color.LightGray) },
                            textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFD4AF37),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("auth_phone_input")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Button to trigger simulator text SMS code
                        if (smsRequestingActive) {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Color(0xFFD4AF37), modifier = Modifier.size(24.dp))
                            }
                        } else {
                            Button(
                                onClick = {
                                    if (phoneInput.length < 13) {
                                        authErrorText = "Iltimos, telefon raqamingizni to'liq kiriting!"
                                    } else {
                                        smsRequestingActive = true
                                        authErrorText = ""
                                        // Simulate loading response delay
                                        val randomizedSms = (1000..9999).random().toString()
                                        smsGeneratingCode = randomizedSms
                                        
                                        // Auto-fire delayed success
                                        smsRequestingActive = false
                                        smsCodeSent = true
                                        Toast.makeText(context, "Tasdiqlash kodi yuborildi!", Toast.LENGTH_LONG).show()
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(42.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.25f))
                            ) {
                                Text(
                                    text = if (smsCodeSent) "SMS kodini qayta jo'natish" else "SMS tasdiqlash kodini olish",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Simulated SMS message tooltip banner
                        if (smsCodeSent) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2E7D32).copy(alpha = 0.15f)),
                                modifier = Modifier.fillMaxWidth(),
                                border = BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.3f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Forum,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Simulyator SMS Xabari: Sizga kelgan tasdiqlash kodi: $smsGeneratingCode (Hozircha raqam faol)",
                                        fontSize = 10.sp,
                                        color = Color(0xFF81C784),
                                        fontWeight = FontWeight.Bold,
                                        lineHeight = 13.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = typedSmsCode,
                                onValueChange = { 
                                    typedSmsCode = it
                                    authErrorText = ""
                                },
                                label = { Text("SMS Tasdiqlash Kodi", color = Color.White.copy(alpha = 0.6f)) },
                                placeholder = { Text("Kodni kiriting (Masalan: $smsGeneratingCode)", color = Color.White.copy(alpha = 0.3f)) },
                                leadingIcon = { Icon(Icons.Default.Pin, contentDescription = null, tint = Color.LightGray) },
                                textStyle = androidx.compose.ui.text.TextStyle(color = Color.White, fontSize = 14.sp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFD4AF37),
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier.fillMaxWidth().testTag("auth_sms_code_input")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Keep logged in (Remember setting)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { rememberMeSetting = !rememberMeSetting }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Checkbox(
                            checked = rememberMeSetting,
                            onCheckedChange = { rememberMeSetting = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFD4AF37),
                                checkmarkColor = Color.Black
                            ),
                            modifier = Modifier.testTag("remember_me_checkbox")
                        )
                        Text(
                            text = "Meni eslab qolish (Tizimga kirganda qolish)",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Render dynamic errors
                    if (authErrorText.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = authErrorText,
                            color = Color(0xFFE53935),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Login/Register Button implementation
                    Button(
                        onClick = {
                            if (isEmailTab) {
                                if (emailInput.isEmpty() || !emailInput.contains("@")) {
                                    authErrorText = "Iltimos, to'g'ri elektron pochta manzilini kiriting!"
                                } else if (!emailCodeSent) {
                                    authErrorText = "Tizimda davom etish uchun avval kodni so'rang!"
                                } else if (typedEmailCode.trim() != emailGeneratingCode) {
                                    authErrorText = "Xato kiritilgan tasdiqlash kodi, iltimos qayta tekshiring!"
                                } else {
                                    // Successfully registered/logged in via email OTP
                                    viewModel.registerAndLogin(emailInput.trim(), "", rememberMeSetting)
                                    Toast.makeText(context, "$emailInput orqali muvaffaqiyatli kirdingiz!", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                if (phoneInput.length < 13) {
                                    authErrorText = "Telefon raqamini to'liq kiriting!"
                                } else if (!smsCodeSent) {
                                    authErrorText = "Tizimda davom etish uchun avval kodni so'rang!"
                                } else if (typedSmsCode.trim() != smsGeneratingCode) {
                                    authErrorText = "Xato kiritilgan SMS kod, iltimos qayta tekshiring!"
                                } else {
                                    // Successfully registered/logged in via telephone code
                                    viewModel.registerAndLogin("", phoneInput.trim(), rememberMeSetting)
                                    Toast.makeText(context, "Telefon orqali muvaffaqiyatli kirdingiz!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_submit_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFA000)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "TIZIMGA KIRISH / RO'YXATDAN O'TISH",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Little secure footer tagline
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Ma'lumotlar shifrlangan va xavfsiz holatda saqlanadi",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.4f)
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

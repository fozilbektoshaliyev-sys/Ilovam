package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // Animation triggers
    var startTextAnim by remember { mutableStateOf(false) }
    var startProgressAnim by remember { mutableStateOf(false) }

    // Ken Burns background scaling animation (Subtle zoom)
    val backgroundScale by animateFloatAsState(
        targetValue = if (startTextAnim) 1.10f else 1.0f,
        animationSpec = tween(durationMillis = 3500, easing = LinearOutSlowInEasing),
        label = "bg_scale"
    )

    // Text animations: Alpha and Translation (Slide up)
    val mainTitleAlpha by animateFloatAsState(
        targetValue = if (startTextAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutExpo),
        label = "title_alpha"
    )
    val mainTitleOffset by animateDpAsState(
        targetValue = if (startTextAnim) 0.dp else 40.dp,
        animationSpec = tween(durationMillis = 1000, easing = EaseOutExpo),
        label = "title_offset"
    )

    val subtitleAlpha by animateFloatAsState(
        targetValue = if (startTextAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, delayMillis = 400, easing = EaseOutExpo),
        label = "subtitle_alpha"
    )
    val subtitleOffset by animateDpAsState(
        targetValue = if (startTextAnim) 0.dp else 30.dp,
        animationSpec = tween(durationMillis = 1200, delayMillis = 400, easing = EaseOutExpo),
        label = "subtitle_offset"
    )

    // Animated filler simulation progress (0f to 1f)
    val progressFlow by animateFloatAsState(
        targetValue = if (startProgressAnim) 1f else 0f,
        animationSpec = tween(durationMillis = 2800, easing = FastOutSlowInEasing),
        label = "progress_val"
    )

    // Pulse effect for the branding icon
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    // Trigger animations sequentially
    LaunchedEffect(Unit) {
        delay(100)
        startTextAnim = true
        delay(300)
        startProgressAnim = true
        // Wait and finish splash
        delay(3200)
        onFinished()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // 1. Ken Burns Animated 3D Portrait Illustration Background
        Image(
            painter = painterResource(id = R.drawable.img_welcome_hero),
            contentDescription = "Background",
            modifier = Modifier
                .fillMaxSize()
                .scale(backgroundScale),
            contentScale = ContentScale.Crop
        )

        // 2. High-contrast Ambient Translucent Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.45f),
                            Color(0xFF0F172A).copy(alpha = 0.85f),
                            Color(0xFF020617).copy(alpha = 0.98f)
                        )
                    )
                )
        )

        // 3. Central Brand Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Elegant pulsing futuristic active token badge / icon
            Surface(
                color = Color(0xFFFFA000).copy(alpha = 0.15f),
                shape = RoundedCornerShape(24.dp),
                border = BoxStrokeRow(1.5.dp, Color(0xFFD4AF37).copy(alpha = 0.45f)),
                modifier = Modifier
                    .size(80.dp)
                    .scale(pulseScale)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Default.MovieFilter,
                        contentDescription = null,
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(40.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ANIMATED TITLE 1: "Animanhwa3D Uz" in glowing premium neon range
            val neonCyan = Color(0xFF00E5FF)
            val neonPink = Color(0xFFFF007F)
            val neonGold = Color(0xFFFFD700)

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = neonCyan,
                            shadow = Shadow(
                                color = neonCyan.copy(alpha = 0.95f),
                                offset = Offset(0f, 0f),
                                blurRadius = 24f
                            )
                        )
                    ) {
                        append("AniManhwa")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = neonPink,
                            shadow = Shadow(
                                color = neonPink.copy(alpha = 0.95f),
                                offset = Offset(0f, 0f),
                                blurRadius = 24f
                            )
                        )
                    ) {
                        append("3D")
                    }
                    withStyle(
                        style = SpanStyle(
                            color = neonGold,
                            shadow = Shadow(
                                color = neonGold.copy(alpha = 0.95f),
                                offset = Offset(0f, 0f),
                                blurRadius = 24f
                            )
                        )
                    ) {
                        append(" Uz")
                    }
                },
                fontSize = 34.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(mainTitleAlpha)
                    .offset(y = mainTitleOffset)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ANIMATED TITLE 2: "Platformasiga xush kelibsiz"
            Text(
                text = "Platformasiga xush kelibsiz",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFD4AF37),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .alpha(subtitleAlpha)
                    .offset(y = subtitleOffset)
            )
        }

        // 4. Glowing Linear Progress & Tech Status at the bottom
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.alpha(subtitleAlpha)
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = null,
                    tint = Color(0xFFFFA000),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Xavfsiz va tezkor yuklash: ${(progressFlow * 100).toInt()}%",
                    fontSize = 11.sp,
                    color = Color.LightGray,
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.height(10.dp))

            // Beautiful glowing styled Linear Loading slider
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                shape = RoundedCornerShape(3.dp),
                color = Color.White.copy(alpha = 0.08f)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progressFlow)
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFFFFA000), Color(0xFF00ACC1))
                            )
                        )
                )
            }
        }
    }
}

// Simple internal helper to avoid dependency on BoxStroke directly in custom code or keep compiles safe
@Composable
private fun BoxStrokeRow(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)

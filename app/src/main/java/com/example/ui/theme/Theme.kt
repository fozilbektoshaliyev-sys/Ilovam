package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = UzdigitalBlue,
    secondary = UzdigitalOrange,
    tertiary = UzdigitalTeal,
    background = UzdigitalDarkNavy,
    surface = UzdigitalNavyCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    surfaceVariant = Color(0xFF1C2742),
    onSurfaceVariant = Color.White.copy(alpha = 0.85f),
    primaryContainer = UzdigitalBlue.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = UzdigitalBlue,
    secondary = UzdigitalOrange,
    tertiary = UzdigitalTeal,
    background = UzdigitalLightBg,
    surface = UzdigitalLightCard,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFE4EBF7),
    onSurfaceVariant = Color(0xFF070B16),
    primaryContainer = UzdigitalBlue.copy(alpha = 0.12f),
    onPrimaryContainer = UzdigitalBlue
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Disable dynamic color to enforce UzdigitalTv brand design identity
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

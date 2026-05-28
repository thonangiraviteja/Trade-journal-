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

private val DarkColorScheme =
  darkColorScheme(
    primary = Color(0xFFD1E4FF),
    onPrimary = Color(0xFF00315C),
    secondary = Color(0xFFC4C6D0),
    onSecondary = Color(0xFF2E3033),
    tertiary = Color(0xFFB4F1BE),
    onTertiary = Color(0xFF224E31),
    background = Color(0xFF1A1C1E),
    onBackground = Color(0xFFE2E2E6),
    surface = Color(0xFF2E3033),
    onSurface = Color(0xFFE2E2E6),
    surfaceVariant = Color(0xFF212327),
    onSurfaceVariant = Color(0xFFC4C6D0),
    outline = Color(0xFF44474E)
  )

private val LightColorScheme = DarkColorScheme // Keep same theme consistency

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Force elegant dark design by default
  dynamicColor: Boolean = false, // Disable system dynamic accents to retain designer intention
  content: @Composable () -> Unit,
) {
  val colorScheme = DarkColorScheme
  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}

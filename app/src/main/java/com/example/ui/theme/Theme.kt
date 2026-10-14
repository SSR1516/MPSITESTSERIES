package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
    darkColorScheme(
        primary = BlueSoft,
        onPrimary = Color.White,
        primaryContainer = NavyLight,
        onPrimaryContainer = Color.White,
        secondary = GoldBright,
        onSecondary = Color.Black,
        background = NavyDark,
        surface = NavyPrimary,
        onBackground = Color.White,
        onSurface = Color.White
    )

private val LightColorScheme =
    lightColorScheme(
        primary = NavyPrimary,
        onPrimary = Color.White,
        primaryContainer = NavyLight,
        onPrimaryContainer = Color.White,
        secondary = BlueBright,
        onSecondary = Color.White,
        tertiary = GoldAccent,
        onTertiary = Color.White,
        background = SurfaceLight,
        surface = CardSurfaceLight,
        onBackground = TextDark,
        onSurface = TextDark
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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

  MaterialTheme(
      colorScheme = colorScheme,
      typography = Typography,
      content = content
  )
}

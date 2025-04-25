package com.nextstep.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 主色调
val PrimaryRed = Color(0xFFE94047)
val PrimaryYellow = Color(0xFFFEBF40)
val PrimaryBlue = Color(0xFF1D8CF8)

// 功能色调
val SuccessGreen = Color(0xFF28C76F)
val WarningOrange = Color(0xFFFF9F43)
val InfoBlue = Color(0xFF00CFE8)
val DangerRed = Color(0xFFEA5455)

// 中性色调
val NeutralDark = Color(0xFF4B4B4B)
val NeutralGray = Color(0xFF9A9A9A)
val NeutralLight = Color(0xFFE0E0E0)
val NeutralWhite = Color(0xFFFAFAFA)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryRed,
    secondary = PrimaryYellow,
    tertiary = PrimaryBlue,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onTertiary = Color.White,
    background = NeutralWhite,
    surface = Color.White,
    onBackground = NeutralDark,
    onSurface = NeutralDark
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryRed,
    secondary = PrimaryYellow,
    tertiary = PrimaryBlue,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun NextStepTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 
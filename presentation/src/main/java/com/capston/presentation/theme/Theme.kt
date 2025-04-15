package com.capston.presentation.theme

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

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80,
)

private val LightColorScheme = lightColorScheme(
    primary = MainPurple,
    secondary = MutePurple,
    tertiary = Pink40,
    background = Color.White,
    surface = Color.White

    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun CapstonTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // 사용은 계속 가능
    dynamicColor: Boolean = true, // 사용하지 않음
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // 항상 LightColorScheme 사용

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
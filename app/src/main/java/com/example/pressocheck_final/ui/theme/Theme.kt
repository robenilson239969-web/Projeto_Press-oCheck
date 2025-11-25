package com.example.pressocheck_final.ui.theme

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

/**
 * Esquema de cores claro com azul, verde e branco para interface acessível.
 */
private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = White,
    primaryContainer = PrimaryBlueLight,
    onPrimaryContainer = PrimaryBlueDark,
    
    secondary = SecondaryGreen,
    onSecondary = White,
    secondaryContainer = SecondaryGreenLight,
    onSecondaryContainer = SecondaryGreenDark,
    
    tertiary = AlertRed,
    onTertiary = White,
    tertiaryContainer = AlertRedLight,
    onTertiaryContainer = AlertRedDark,
    
    background = White,
    onBackground = DarkGray,
    surface = White,
    onSurface = DarkGray,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumGray,
    
    error = AlertRed,
    onError = White,
    errorContainer = AlertRedLight,
    onErrorContainer = AlertRedDark,
    
    outline = MediumGray,
    outlineVariant = LightGray
)

/**
 * Esquema de cores escuro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlueLight,
    onPrimary = PrimaryBlueDark,
    primaryContainer = PrimaryBlueDark,
    onPrimaryContainer = PrimaryBlueLight,
    
    secondary = SecondaryGreenLight,
    onSecondary = SecondaryGreenDark,
    secondaryContainer = SecondaryGreenDark,
    onSecondaryContainer = SecondaryGreenLight,
    
    tertiary = AlertRedLight,
    onTertiary = AlertRedDark,
    tertiaryContainer = AlertRedDark,
    onTertiaryContainer = AlertRedLight,
    
    background = DarkGray,
    onBackground = White,
    surface = DarkGray,
    onSurface = White,
    surfaceVariant = MediumGray,
    onSurfaceVariant = LightGray,
    
    error = AlertRedLight,
    onError = AlertRedDark,
    errorContainer = AlertRedDark,
    onErrorContainer = AlertRedLight,
    
    outline = MediumGray,
    outlineVariant = LightGray
)

/**
 * Tema principal do aplicativo PressãoCheck.
 * 
 * Utiliza cores suaves (azul, verde, branco) e tipografia acessível
 * para facilitar o uso por pessoas de meia-idade.
 * 
 * @param darkTheme Se deve usar tema escuro (baseado na preferência do sistema)
 * @param dynamicColor Se deve usar cores dinâmicas (Android 12+)
 * @param content Conteúdo Compose a ser renderizado com este tema
 */
@Composable
fun PressãoCheckFinalTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Desabilitado para manter cores consistentes
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
            // A aparência da status bar é gerenciada automaticamente pelo Material 3
            // e pelo enableEdgeToEdge() chamado no MainActivity
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
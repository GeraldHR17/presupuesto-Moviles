package com.huertas.rivera.wikibusqueda.ui.theme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Medieval Palette
val Parchment = Color(0xFFF2EAD3)    // Light parchment background
val OldPaper = Color(0xFFDFD3B6)     // Dark parchment background
val RoyalGold = Color(0xFFC5A059)    // Gold details
val KnightIron = Color(0xFF3E3C3A)   // Iron text and borders
val DragonCrimson = Color(0xFF8B0000) // Crimson for errors
val ForestKnight = Color(0xFF2D4628) // Heraldic green
private val DarkColorScheme = darkColorScheme(
    primary = RoyalGold,           // dorado para bordes y acentos
    secondary = ForestKnight,      // verde heráldico
    tertiary = DragonCrimson,      // carmesí para errores
    background = Color(0xFF1A1A1A),// negro profundo
    surface = Color(0xFF2A2520),   // superficie oscura cálida (no frío azulado)
    onSurface = Parchment,         // texto color pergamino
    onBackground = Parchment,
    error = DragonCrimson
)
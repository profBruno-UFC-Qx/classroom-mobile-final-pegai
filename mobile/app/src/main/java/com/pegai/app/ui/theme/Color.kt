package com.pegai.app.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.isSystemInDarkTheme

// --- Cores da Marca (Brand) ---
val BlueDark = Color(0xFF0A5C8A)
val BlueMedium = Color(0xFF0E8FC6)
val CyanPegai = Color(0xFF2ED1B2)

// --- Cores para Modo Escuro (Dark) ---
val GrayDark = Color(0xFF121212)      // Fundo principal
val GraySurface = Color(0xFF1E1E1E)   // Cards e superfícies
val GrayFieldDark = Color(0xFF2C2C2C) // Inputs no dark mode

// --- Cores para Modo Claro (Light) ---
val BackgroundPremium = Color(0xFFFBFBFB)
val FieldGrayLight = Color(0xFFF2F4F7)

// --- Auxiliares ---
val TextDark = Color(0xFF344054)
val TextLight = Color(0xFFF9FAFB)

// Função auxiliar para pegar a cor do campo dependendo do tema
@Composable
fun getFieldColor(): Color {
    return if (isSystemInDarkTheme()) GrayFieldDark else FieldGrayLight
}
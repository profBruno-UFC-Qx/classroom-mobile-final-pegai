package com.pegai.app.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Category(
    val id: String,
    val nomeExibicao: String,
    val icon: ImageVector
) {
    LIVROS(
        id = "books",
        nomeExibicao = "Livros e Apostilas",
        icon = Icons.AutoMirrored.Filled.MenuBook
    ),
    CALCULADORAS(
        id = "calculators",
        nomeExibicao = "Calculadoras e Tech", // HP12c, Científicas, Tablets
        icon = Icons.Default.Calculate // Se der erro, use Icons.Default.PhoneAndroid
    ),
    MATERIAIS_DESENHO(
        id = "design",
        nomeExibicao = "Desenho e Arquitetura", // Réguas, Tubos, Pranchetas, Marcadores
        icon = Icons.Default.Create // Ícone de lápis/edição
    ),
    LABORATORIO(
        id = "lab",
        nomeExibicao = "Saúde e Laboratório", // Jalecos, Óculos de proteção, Estetoscópios
        icon = Icons.Default.Healing // Ícone de saúde
    ),
    ACESSORIOS(
        id = "accessories",
        nomeExibicao = "Cabos e Adaptadores", // Carregadores, HDMI, Adaptadores
        icon = Icons.Default.ElectricalServices // Ícone de tomada/cabo
    ),
    ESPORTES(
        id = "sports",
        nomeExibicao = "Esportes Universitários", // Raquetes, Bolas (Para o intervalo/atlética)
        icon = Icons.Default.SportsSoccer
    ),
    PAPELARIA(
        id = "stationery",
        nomeExibicao = "Papelaria Especial", // Grampeadores industriais, Furadores, Guilhotinas
        icon = Icons.Default.AttachFile
    ),
    OUTROS(
        id = "others",
        nomeExibicao = "Outros",
        icon = Icons.Default.MoreHoriz
    );

    companion object {
        fun fromId(id: String): Category = entries.find { it.id == id } ?: OUTROS
        fun fromNome(nome: String): Category = entries.find { it.nomeExibicao == nome } ?: OUTROS
    }
}
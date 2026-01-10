package com.pegai.app.ui.viewmodel.details

import com.pegai.app.model.Product

data class ProductDetailsUiState(
    val isLoading: Boolean = true,
    val produto: Product? = null,
    val nomeDono: String = "Carregando...",
    val fotoDono: String = "",
    val erro: String? = null,
    val avaliacoesCount: Int = 0,
    val imagensCarrossel: List<String> = emptyList(),
    val reviews: List<ReviewUI> = emptyList()
)


// Classe auxiliar para os comentários
data class ReviewUI(
    val authorName: String,
    val comment: String,
    val rating: Int,
    val date: String
)
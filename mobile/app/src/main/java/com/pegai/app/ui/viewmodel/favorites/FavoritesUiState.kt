package com.pegai.app.ui.viewmodel.favorites

import com.pegai.app.model.Product

data class FavoritesUiState(
    val userId: String? = null,
    val favoriteIds: Set<String> = emptySet(),
    val favorites: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

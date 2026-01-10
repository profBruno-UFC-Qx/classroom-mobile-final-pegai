package com.pegai.app.ui.viewmodel.publicprofile

import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.model.Review

data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val produtos: List<Product> = emptyList(),
    val avaliacoes: List<Review> = emptyList(),
    val erro: String? = null
)
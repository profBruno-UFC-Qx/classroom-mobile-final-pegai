package com.pegai.app.ui.viewmodel.publicprofile

import com.pegai.app.model.Product
import com.pegai.app.model.User
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileUiState.kt
import com.pegai.app.model.Review
=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileUiState.kt

data class PublicProfileUiState(
    val isLoading: Boolean = true,
    val user: User? = null,
    val produtos: List<Product> = emptyList(),
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileUiState.kt
    val avaliacoes: List<Review> = emptyList(),
    val erro: String? = null
)
=======
    val reviews: List<ReviewMock> = emptyList(),
    val produtosSugeridos: List<ProdutoMock> = emptyList(),
    val nota: Number = 0.0,
    val totalAvaliacao: Number = 0.0,

    val erro: String? = null
)

// Classes movidas da tela para cá
data class ReviewMock(val nome: String, val comentario: String, val nota: Int, val data: String)
data class ProdutoMock(val nome: String, val preco: String, val imagem: String)
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileUiState.kt

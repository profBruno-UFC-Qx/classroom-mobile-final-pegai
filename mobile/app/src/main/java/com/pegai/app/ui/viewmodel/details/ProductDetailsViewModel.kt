package com.pegai.app.ui.viewmodel.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ProductDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    fun carregarDetalhes(productId: String?) {
        if (productId == null) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // 1. Carrega o Produto
                val doc = db.collection("products").document(productId).get().await()
                val produto = doc.toObject<Product>()?.copy(pid = doc.id)

                if (produto != null) {

                    // 2. Carrega Dados do Dono (Nome e Foto)
                    var nomeDono = "Anônimo"
                    var fotoDono = ""

                    if (produto.donoId.isNotBlank()) {
                        val userDoc = db.collection("users").document(produto.donoId).get().await()
                        nomeDono = userDoc.getString("nome") ?: userDoc.getString("name") ?: "Anônimo"
                        fotoDono = userDoc.getString("fotoUrl") ?: userDoc.getString("foto") ?: ""
                    }

                    // Fallback de imagens
                    val listaImagens = produto.imagens.ifEmpty {
                        if (produto.imageUrl.isNotEmpty()) listOf(produto.imageUrl) else emptyList()
                    }

                    // 3. Carrega Reviews
                    val reviewsReais = carregarReviewsDoRepository(productId)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            produto = produto,
                            nomeDono = nomeDono,
                            fotoDono = fotoDono, // <--- Salvando a foto no estado
                            avaliacoesCount = reviewsReais.size,
                            imagensCarrossel = listaImagens,
                            reviews = reviewsReais
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, erro = "Produto não encontrado") }
                }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, erro = e.message) }
            }
        }
    }

    private suspend fun carregarReviewsDoRepository(produtoId: String): List<ReviewUI> {
        val reviewsModel = ProductRepository.getProductReviews(produtoId)
        return reviewsModel.map { review ->
            ReviewUI(
                authorName = if(review.autorNome.isNotEmpty()) review.autorNome else "Usuário",
                comment = review.comentario,
                rating = review.nota,
                date = formatarDataLong(review.data)
            )
        }
    }

    private fun formatarDataLong(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
}
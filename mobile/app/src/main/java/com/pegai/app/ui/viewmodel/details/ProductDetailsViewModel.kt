package com.pegai.app.ui.viewmodel.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
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
                val doc = db.collection("products").document(productId).get().await()
                val produto = doc.toObject<Product>()?.copy(pid = doc.id)

                if (produto != null) {
                    val nomeDono = carregarNomeDono(produto.donoId)

                    // Fallback: usa 'imageUrl' se a lista de imagens estiver vazia
                    val listaImagens = produto.imagens.ifEmpty {
                        if (produto.imageUrl.isNotEmpty()) listOf(produto.imageUrl) else emptyList()
                    }

                    val reviewsReais = carregarReviewsDoFirebase(productId)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            produto = produto,
                            nomeDono = nomeDono,
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

    private suspend fun carregarNomeDono(donoId: String): String {
        return try {
            if (donoId.isBlank()) return "Anônimo"
            val doc = db.collection("users").document(donoId).get().await()
            doc.getString("nome") ?: "Anônimo"
        } catch (e: Exception) {
            "Desconhecido"
        }
    }

    private suspend fun carregarReviewsDoFirebase(produtoId: String): List<ReviewUI> {
        return try {
            val snapshot = db.collection("avaliacao")
                .whereEqualTo("produtoId", produtoId)
                .get()
                .await()

            snapshot.documents.map { doc ->
                val nomeAutor = doc.getString("autorNome") ?: "Usuário"
                val comentario = doc.getString("comentario") ?: doc.getString("texto") ?: ""
                val nota = doc.getDouble("nota")?.toInt() ?: 5

                val dataObj = doc.getDate("data")
                val dataFormatada = dataObj?.let {
                    SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).format(it)
                } ?: ""

                ReviewUI(
                    authorName = nomeAutor,
                    comment = comentario,
                    rating = nota,
                    date = dataFormatada
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
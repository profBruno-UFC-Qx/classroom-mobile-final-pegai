package com.pegai.app.ui.viewmodel.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pegai.app.data.data.repository.ProductRepository
=======
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.data.data.utils.formatarTempo
import com.pegai.app.model.Avaliacao
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
=======
import java.util.concurrent.TimeUnit
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt

class ProductDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
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
=======
    private val auth = FirebaseAuth.getInstance()

    suspend fun carregarAvaliacoesDoProduto(produtoId: String): List<Avaliacao> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("avaliacao")
            .whereEqualTo("produtoId", produtoId)
            .get()
            .await()

        return snapshot.toObjects(Avaliacao::class.java)
    }



    suspend fun carregarReviewsUI(produtoId: String): List<ReviewUI> {
        val avaliacoes = carregarAvaliacoesDoProduto(produtoId)

        return avaliacoes.map { avaliacao ->
            ReviewUI(
                authorName = UserRepository.getNomeUsuario(avaliacao.usuarioId),
                comment = avaliacao.descricao,
                rating = avaliacao.nota,
                date = formatarTempo(avaliacao.data)
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
            )
        }
    }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
    private fun formatarDataLong(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
=======
    fun carregarDetalhes(productId: String?) {
        _uiState.update { it.copy(isLoading = true) }

        if (productId == null) {
            _uiState.update { it.copy(isLoading = false, erro = "Produto não encontrado") }
            return
        }

        viewModelScope.launch {
            val produto = ProductRepository.getProdutoPorId(productId)

            if (produto == null) {
                _uiState.update { it.copy(isLoading = false, erro = "Produto indisponível") }
                return@launch
            }

            val reviews = carregarReviewsUI(productId)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    produto = produto,
                    imagensCarrossel = if (produto.imagens.isNotEmpty())
                        produto.imagens else listOf(produto.imageUrl),
                    nomeDono = produto.donoNome,
                    avaliacoesCount = reviews.size,
                    reviews = reviews,
                    isDonoDoAnuncio = produto.donoId == auth.currentUser?.uid
                )
            }
        }
    }


    private fun encontrarProdutoMock(id: String): Product? {
        val lista = listOf(
            Product(
                pid = "1",
                titulo = "Calculadora HP 12c",
                descricao = "Calculadora usada, perfeita para contabilidade.",
                preco = 15.0,
                categoria = "Calculadoras",
                imageUrl = "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                donoId = "user_mock_1",
                donoNome = "Maria",
                nota = 4.8,
                totalAvaliacoes = 12,
                imagens = listOf(
                    "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                    "https://http2.mlstatic.com/D_NQ_NP_787622-MLB48827768466_012022-O.webp"
                )
            ),
            Product(
                pid = "2",
                titulo = "Jaleco Quixadá",
                descricao = "Tamanho M. Pouco uso.",
                preco = 35.0,
                categoria = "Jalecos",
                imageUrl = "https://photos.enjoei.com.br/jaleco-branco-81336648/800x800/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy8xMzQ3Mzc3NC82MmY4Nzc0OGU2YTQwNzVkM2Q3OGNhMjFkZDZhY2NkNS5qcGc",
                donoId = "user_2",
                donoNome = "João",
                nota = 5.0,
                totalAvaliacoes = 3
            ),
            Product(
                pid = "3",
                titulo = "Kit Arduino",
                descricao = "Kit completo para iniciantes.",
                preco = 20.0,
                categoria = "Eletrônicos",
                imageUrl = "https://cdn.awsli.com.br/78/78150/produto/338952433/kit_arduino_uno_smd_starter_com_caixa_organizadora-3xak1vrhvm.png",
                donoId = "user_3",
                donoNome = "Pedro",
                nota = 4.5
            )
        )
        return lista.find { it.pid == id }
    }
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/details/ProductDetailsViewModel.kt
}
package com.pegai.app.ui.viewmodel.details

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProductDetailsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailsUiState())
    val uiState: StateFlow<ProductDetailsUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    fun carregarDetalhes(productId: String?) {
        _uiState.update { it.copy(isLoading = true) }

        if (productId == null) {
            _uiState.update { it.copy(isLoading = false, erro = "Produto não encontrado") }
            return
        }

        // Busca o produto REAL (Simulado)
        val produtoReal = encontrarProdutoMock(productId)

        if (produtoReal != null) {
            val meuId = auth.currentUser?.uid
            val isDono = false // Mock temporário

            //  Prepara imagens (Se a lista do produto for vazia, usa a capa)
            val imagensParaExibir = if (produtoReal.imagens.isNotEmpty()) {
                produtoReal.imagens
            } else {
                listOf(produtoReal.imageUrl)
            }

            // Prepara Reviews
            val reviewsMock = listOf(
                ReviewUI("Gabriel S.", "Muito precisa!", 5, "1 semana atrás"),
                ReviewUI("Julia M.", "Entregue em perfeito estado.", 5, "2 meses atrás"),
                ReviewUI("Lucas F.", "Excelente para a prova.", 4, "3 meses atrás")
            )

            _uiState.update {
                it.copy(
                    isLoading = false,
                    produto = produtoReal,
                    imagensCarrossel = imagensParaExibir,
                    nomeDono = produtoReal.donoNome,
                    fotoDono = "",
                    avaliacoesCount = 15,
                    reviews = reviewsMock, // Passando a lista para a tela
                    isDonoDoAnuncio = isDono
                )
            }
        } else {
            _uiState.update { it.copy(isLoading = false, erro = "Produto indisponível.") }
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
}
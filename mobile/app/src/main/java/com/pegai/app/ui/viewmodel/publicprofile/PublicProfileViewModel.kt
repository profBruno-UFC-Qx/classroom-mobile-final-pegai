package com.pegai.app.ui.viewmodel.publicprofile

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.Product
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PublicProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    private val db = FirebaseFirestore.getInstance()

    // Dados Mockados de Visual (Centralizados aqui no VM)
    private val reviewsFalsos = listOf(
        ReviewMock("João Silva", "Produto excelente, muito bem cuidado!", 5, "12 out"),
        ReviewMock("Ana Clara", "A Edineide foi super atenciosa na entrega, uma dyva.", 5, "05 set"),
        ReviewMock("Pedro H.", "Tudo certo, recomendo.", 4, "20 ago"),
        ReviewMock("Lucas M.", "A calculadora estava novinha.", 5, "10 ago")
    )

    private val produtosSugestao = listOf(
        ProdutoMock("Calculadora HP 12c", "R$ 15/dia", "https://m.media-amazon.com/images/I/61l4i7aoW2L._AC_SL1500_.jpg"),
        ProdutoMock("Vade Mecum 2024", "R$ 10/dia", "https://m.media-amazon.com/images/I/81xXj2yXyUL._SL1500_.jpg"),
        ProdutoMock("Jaleco Tam P", "R$ 20/dia", "https://m.media-amazon.com/images/I/51+uK8bC1KL._AC_SX522_.jpg")
    )

    fun carregarPerfil(userId: String) {
        _uiState.update { it.copy(isLoading = true, erro = null) }

        // Se for ID de teste
        if (userId.startsWith("user_") || userId.startsWith("mock") || userId.contains("mock")) {
            carregarPerfilMock(userId)
            return
        }

        // Se for ID real do Firebase
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(User::class.java)
                    // Filtra produtos reais
                    val produtosDoUsuario = gerarProdutosFalsosBackend().filter {
                        it.donoNome == usuario?.nome || it.donoId == userId
                    }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = usuario,
                            produtos = produtosDoUsuario,
                            // Passando os mocks visuais para a tela
                            reviews = reviewsFalsos,
                            produtosSugeridos = produtosSugestao
                        )
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false, erro = "Usuário não encontrado.") }
                }
            }
            .addOnFailureListener {
                _uiState.update { it.copy(isLoading = false, erro = "Erro de conexão.") }
            }
    }

    private fun carregarPerfilMock(userId: String) {
        val nomeFalso = when (userId) {
            "user_mock_1" -> "Maria da Silva"
            "user_2" -> "João Souza"
            else -> "Usuário Teste"
        }

        val usuarioMock = User(
            uid = userId,
            nome = nomeFalso,
            fotoUrl = ""
        )

        val produtosMock = gerarProdutosFalsosBackend().filter { it.donoId == userId }

        _uiState.update {
            it.copy(
                isLoading = false,
                user = usuarioMock,
                produtos = produtosMock,
                // Passando os mocks visuais
                reviews = reviewsFalsos,
                produtosSugeridos = produtosSugestao
            )
        }
    }

    // Simula produtos vindos do banco (backend)
    private fun gerarProdutosFalsosBackend(): List<Product> {
        return listOf(
            Product(pid = "1", titulo = "Calculadora HP", preco = 15.0, imageUrl = "https://via.placeholder.com/150", donoId = "user_mock_1", donoNome = "Maria"),
            Product(pid = "2", titulo = "Jaleco", preco = 35.0, imageUrl = "https://via.placeholder.com/150", donoId = "user_2", donoNome = "João")
        )
    }
}
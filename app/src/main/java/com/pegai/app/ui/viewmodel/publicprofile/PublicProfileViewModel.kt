package com.pegai.app.ui.viewmodel.publicprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.Review
import com.pegai.app.repository.ChatRepository
=======
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.data.data.utils.formatarTempo
import com.pegai.app.model.Avaliacao
import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.model.UserAvaliacao
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt

class PublicProfileViewModel : ViewModel() {

    private val chatRepository = ChatRepository()

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    fun carregarPerfil(userId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val usuario = UserRepository.getUsuarioPorId(userId)
                val listaAvaliacoes = chatRepository.getUserReviews(userId)

=======
import kotlin.math.roundToInt

class PublicProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    fun calcularNotaMedia(avaliacoes: List<UserAvaliacao>): Double {
        if (avaliacoes.isEmpty()) return 0.0

        val soma = avaliacoes.sumOf { it.nota }
        return soma.toDouble() / avaliacoes.size
    }

    fun calcularNotaArredondada(avaliacoes: List<UserAvaliacao>): Int {
        return calcularNotaMedia(avaliacoes).roundToInt()
    }

    fun carregarPerfil(userId: String) {
        _uiState.update { it.copy(isLoading = true, erro = null) }

        viewModelScope.launch {
            try {
                //suário
                val usuario: User? = UserRepository.getUsuarioPorId(userId)

                if (usuario == null) {
                    _uiState.update {
                        it.copy(isLoading = false, erro = "Usuário não encontrado")
                    }
                    return@launch
                }

                //Produtos anunciados
                val produtos: List<Product> =
                    ProductRepository.getProdutosPorDono(userId)

                //Avaliações recebidas (userAval)
                val avaliacoes = UserRepository.getAvaliacoesDoUsuario(userId)
                val nota = calcularNotaMedia(avaliacoes)
                val reviews = avaliacoes.map { avaliacao ->
                    val nomeAutor = UserRepository.getNomeUsuario(avaliacao.avalId)

                    ReviewMock(
                        nome = nomeAutor,
                        comentario = avaliacao.comentario,
                        nota = avaliacao.nota,
                        data = formatarTempo(avaliacao.data)
                    )
                }

                //Atualiza UI
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = usuario,
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
                        avaliacoes = listaAvaliacoes
                    )
                }
=======
                        produtos = produtos,
                        reviews = reviews,
                        nota = nota,
                        totalAvaliacao = reviews.size,
                        produtosSugeridos = emptyList()
                    )
                }

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
                        erro = "Não foi possível carregar o perfil."
=======
                        erro = "Erro ao carregar perfil"
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
                    )
                }
            }
        }
    }
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt
}
=======
}
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/publicprofile/PublicProfileViewModel.kt

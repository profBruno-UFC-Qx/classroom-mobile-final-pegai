package com.pegai.app.ui.viewmodel.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.User
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun carregarDadosUsuario(usuarioAuth: User?) {
        if (usuarioAuth == null) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            val usuarioDeferred = async { UserRepository.getUsuarioPorId(usuarioAuth.uid) ?: usuarioAuth }
            val qtdAnunciosDeferred = async { ProductRepository.getQuantidadeProdutosPorDono(usuarioAuth.uid) }

            val usuarioCompleto = usuarioDeferred.await()
            val totalAnuncios = qtdAnunciosDeferred.await()

            _uiState.update {
                it.copy(
                    user = usuarioCompleto,
                    chavePix = usuarioCompleto.chavePix,
                    anuncios = totalAnuncios.toString(),
                    isLoading = false
                )
            }
        }
    }

    fun atualizarFotoDePerfil(uri: Uri) {
        val currentUser = _uiState.value.user

        if (currentUser == null) {
            Log.e("ProfileViewModel", "ERRO: Usuário nulo. Não é possível enviar foto.")
            return
        }

        Log.d("ProfileViewModel", "Iniciando upload... URI: $uri")
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val novaUrl = UserRepository.atualizarFotoPerfil(currentUser.uid, uri)
                Log.d("ProfileViewModel", "Sucesso! Nova URL: $novaUrl")
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        user = state.user?.copy(fotoUrl = novaUrl)
                    )
                }
                carregarDadosUsuario(currentUser)

            } catch (e: Exception) {
                Log.e("ProfileViewModel", "ERRO no upload da imagem", e)
                _uiState.update {
                    it.copy(isLoading = false, erro = "Falha ao enviar imagem: ${e.message}")
                }
            }
        }
    }

    // --- Pix Management ---
    fun abrirPixDialog() { _uiState.update { it.copy(isPixDialogVisible = true, chavePixTemp = it.chavePix) } }
    fun fecharPixDialog() { _uiState.update { it.copy(isPixDialogVisible = false) } }
    fun atualizarChaveTemp(novaChave: String) { _uiState.update { it.copy(chavePixTemp = novaChave) } }

    fun salvarChavePix() {
        val currentUser = _uiState.value.user ?: return
        val novaChave = _uiState.value.chavePixTemp
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                UserRepository.atualizarChavePix(currentUser.uid, novaChave)
                _uiState.update {
                    it.copy(isLoading = false, chavePix = novaChave, isPixDialogVisible = false, user = it.user?.copy(chavePix = novaChave))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, erro = "Erro ao salvar Pix.") }
            }
        }
    }
}
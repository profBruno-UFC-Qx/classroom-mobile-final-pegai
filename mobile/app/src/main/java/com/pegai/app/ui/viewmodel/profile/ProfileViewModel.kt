package com.pegai.app.ui.viewmodel.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // -------- USUÁRIO --------

    fun carregarDadosUsuario(usuarioAuth: User?) {
        if (usuarioAuth == null) return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            // Busca dados completos
            val usuarioCompleto = UserRepository.getUsuarioPorId(usuarioAuth.uid) ?: usuarioAuth

            _uiState.update {
                it.copy(
                    user = usuarioCompleto,
                    chavePix = usuarioCompleto.chavePix,
                    isLoading = false
                )
            }
        }
    }

    // -------- FOTO DE PERFIL (NOVO) --------

    fun atualizarFotoDePerfil(uri: Uri) {
        val currentUser = _uiState.value.user ?: return

        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // Chama o repositório para fazer o upload
                val novaUrl = UserRepository.atualizarFotoPerfil(currentUser.uid, uri)

                // Atualiza o estado local com a nova URL para refletir na tela na hora
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        user = state.user?.copy(fotoUrl = novaUrl)
                    )
                }
            } catch (e: Exception) {
                // Se der erro, volta o loading e pode setar mensagem de erro
                _uiState.update { it.copy(isLoading = false, erro = "Erro ao atualizar foto.") }
            }
        }
    }

    // -------- PIX --------

    fun abrirPixDialog() {
        _uiState.update {
            it.copy(
                isPixDialogVisible = true,
                chavePixTemp = it.chavePix
            )
        }
    }

    fun fecharPixDialog() {
        _uiState.update { it.copy(isPixDialogVisible = false) }
    }

    fun atualizarChaveTemp(novaChave: String) {
        _uiState.update { it.copy(chavePixTemp = novaChave) }
    }

    fun salvarChavePix() {
        val novaChave = _uiState.value.chavePixTemp

        // TODO: Salvar chave Pix no Firestore

        _uiState.update {
            it.copy(
                chavePix = novaChave,
                isPixDialogVisible = false
            )
        }
    }
}
package com.pegai.app.ui.viewmodel.publicprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.UserAvaliacao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PublicProfileViewModel : ViewModel() {

    // Inicializa com o estado padrão (Loading)
    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState: StateFlow<PublicProfileUiState> = _uiState.asStateFlow()

    fun carregarPerfil(userId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val usuario = UserRepository.getUsuarioPorId(userId)
                val listaAvaliacoes = UserRepository.getTodasAvaliacoes(userId)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        user = usuario,
                        avaliacoes = listaAvaliacoes
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Não foi possível carregar o perfil."
                    )
                }
            }
        }
    }
}
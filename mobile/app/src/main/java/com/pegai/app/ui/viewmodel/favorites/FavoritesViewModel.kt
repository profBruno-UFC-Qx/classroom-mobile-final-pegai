package com.pegai.app.ui.viewmodel.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.data.data.repository.FavoritesRepository
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun setUserId(userId: String?) {
        val normalized = userId?.trim().takeUnless { it.isNullOrEmpty() }

        if (_uiState.value.userId == normalized) return

        // reset ao trocar usuário (ou logout)
        _uiState.value = FavoritesUiState(userId = normalized)

        if (normalized != null) {
            refresh()
        }
    }

    fun isFavorite(productId: String): Boolean {
        if (productId.isBlank()) return false
        return _uiState.value.favoriteIds.contains(productId)
    }

    fun refresh() {
        val uid = _uiState.value.userId ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val favorites = FavoritesRepository.getFavoriteProducts(uid)
                val ids = favorites.map { it.pid }.filter { it.isNotBlank() }.toSet()

                _uiState.update {
                    it.copy(
                        favorites = favorites,
                        favoriteIds = ids,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Erro ao carregar favoritos"
                    )
                }
            }
        }
    }

    fun toggleFavorite(product: Product) {
        val uid = _uiState.value.userId ?: return
        val pid = product.pid
        if (pid.isBlank()) return

        val currentlyFav = _uiState.value.favoriteIds.contains(pid)

        // Otimista: muda UI primeiro
        if (currentlyFav) {
            _uiState.update { st ->
                st.copy(
                    favoriteIds = st.favoriteIds - pid,
                    favorites = st.favorites.filterNot { it.pid == pid },
                    error = null
                )
            }

            viewModelScope.launch {
                try {
                    FavoritesRepository.removeFavorite(uid, pid)
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Erro ao remover favorito") }
                    refresh()
                }
            }
        } else {
            _uiState.update { st ->
                val newList = listOf(product) + st.favorites.filterNot { it.pid == pid }
                st.copy(
                    favoriteIds = st.favoriteIds + pid,
                    favorites = newList,
                    error = null
                )
            }

            viewModelScope.launch {
                try {
                    FavoritesRepository.addFavorite(uid, pid)
                } catch (e: Exception) {
                    _uiState.update { it.copy(error = e.message ?: "Erro ao adicionar favorito") }
                    refresh()
                }
            }
        }
    }
}

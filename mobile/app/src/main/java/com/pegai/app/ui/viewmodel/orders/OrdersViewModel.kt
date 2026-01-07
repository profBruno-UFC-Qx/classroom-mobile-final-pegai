package com.pegai.app.ui.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pegai.app.data.data.repository.RentalRepository
import com.pegai.app.model.Rental
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class OrdersUiState(
    val isLoading: Boolean = true,
    val activeRentals: List<Rental> = emptyList(),
    val inactiveRentals: List<Rental> = emptyList(),
    val erro: String? = null
)

class OrdersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()

    fun carregarAlugueis(currentUserId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val todosAlugueis = RentalRepository.getRentalsForUser(currentUserId)

                val ativos = todosAlugueis.filter { it.status.isActive }
                    .sortedByDescending { it.dataCriacao }

                val inativos = todosAlugueis.filter { !it.status.isActive }
                    .sortedByDescending { it.dataFim }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeRentals = ativos,
                        inactiveRentals = inativos
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, erro = e.message) }
            }
        }
    }
}
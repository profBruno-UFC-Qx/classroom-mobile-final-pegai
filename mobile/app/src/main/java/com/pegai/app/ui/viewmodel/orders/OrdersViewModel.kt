package com.pegai.app.ui.viewmodel.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.data.data.repository.RentalRepository
import com.pegai.app.model.Rental
import com.pegai.app.model.RentalStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class OrdersUiState(
    val isLoading: Boolean = true,
    val activeRentals: List<Rental> = emptyList(),
    val inactiveRentals: List<Rental> = emptyList(),
    val erro: String? = null
)

class OrdersViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OrdersUiState())
    val uiState: StateFlow<OrdersUiState> = _uiState.asStateFlow()
    private val db = FirebaseFirestore.getInstance()

    fun carregarAlugueis(currentUserId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val todosAlugueisRaw = RentalRepository.getRentalsForUser(currentUserId)
                val todosAlugueisEnriquecidos = todosAlugueisRaw.map { rental ->
                    async {
                        val locadorFoto = fetchUserPhoto(rental.locadorId)
                        val locatarioFoto = fetchUserPhoto(rental.locatarioId)
                        rental.copy(
                            locadorFoto = locadorFoto,
                            locatarioFoto = locatarioFoto
                        )
                    }
                }.awaitAll()
                val ativos = todosAlugueisEnriquecidos.filter {
                    it.status != RentalStatus.COMPLETED &&
                            it.status != RentalStatus.CANCELLED &&
                            it.status != RentalStatus.DECLINED
                }.sortedByDescending { it.dataCriacao }

                val inativos = todosAlugueisEnriquecidos.filter {
                    it.status == RentalStatus.COMPLETED ||
                            it.status == RentalStatus.CANCELLED ||
                            it.status == RentalStatus.DECLINED
                }.sortedByDescending { it.dataFim }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        activeRentals = ativos,
                        inactiveRentals = inativos
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false, erro = e.message) }
            }
        }
    }

    private suspend fun fetchUserPhoto(userId: String): String {
        if (userId.isEmpty()) return ""
        return try {
            val doc = db.collection("users").document(userId).get().await()
            doc.getString("fotoUrl") ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
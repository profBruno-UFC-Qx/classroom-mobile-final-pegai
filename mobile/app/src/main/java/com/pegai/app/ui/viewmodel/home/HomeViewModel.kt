package com.pegai.app.ui.viewmodel.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.Category
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var todosProdutosCache: List<Product> = emptyList()

    val categoriasFiltro = listOf("Todos") + Category.entries.map { it.nomeExibicao }

    init {
        carregarDadosIniciais()
    }

    fun carregarDadosIniciais() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val produtosReais = carregarProdutosCompletos()

                todosProdutosCache = produtosReais
                ProductRepository.salvarNoCache(produtosReais)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        produtos = produtosReais,
                        produtosPopulares = produtosReais.filter { p -> p.nota >= 4.5 }
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(isLoading = false, erro = "Erro ao carregar produtos")
                }
            }
        }
    }

    private suspend fun carregarProdutosCompletos(): List<Product> {
        return try {
            val snapshot = db.collection("products").get().await()

            val lista = snapshot.documents.mapNotNull { doc ->
                val produto = doc.toObject(Product::class.java)
                produto?.copy(pid = doc.id)
            }

            // Garante que o nome do dono esteja presente
            lista.map { produto ->
                val nomeFinal = if (produto.donoNome.isNotBlank()) produto.donoNome
                else UserRepository.getNomeUsuario(produto.donoId)
                produto.copy(donoNome = nomeFinal)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // --- Filtros ---

    fun selecionarCategoria(categoria: String) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
        aplicarFiltros()
    }

    fun atualizarPesquisa(texto: String) {
        _uiState.update { it.copy(textoPesquisa = texto) }
        aplicarFiltros()
    }

    private fun aplicarFiltros() {
        val estadoAtual = _uiState.value
        val termo = estadoAtual.textoPesquisa.lowercase()
        val cat = estadoAtual.categoriaSelecionada

        val listaFiltrada = todosProdutosCache.filter { produto ->
            val matchCategoria = if (cat == "Todos") true else produto.categoria.equals(cat, ignoreCase = true)
            val matchTexto = produto.titulo.lowercase().contains(termo) ||
                    produto.descricao.lowercase().contains(termo)

            matchCategoria && matchTexto
        }

        _uiState.update { it.copy(produtos = listaFiltrada) }
    }

    // --- Geolocalização ---

    @SuppressLint("MissingPermission")
    fun obterLocalizacaoAtual(context: Context) {
        _uiState.update { it.copy(localizacaoAtual = "Buscando...") }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    viewModelScope.launch {
                        converterCoordenadas(context, location.latitude, location.longitude)
                    }
                } else {
                    _uiState.update { it.copy(localizacaoAtual = "GPS indisponível") }
                }
            }.addOnFailureListener {
                _uiState.update { it.copy(localizacaoAtual = "Erro GPS") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(localizacaoAtual = "Sem permissão") }
        }
    }

    private fun converterCoordenadas(context: Context, lat: Double, long: Double) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, long, 1)

            if (!addresses.isNullOrEmpty()) {
                val end = addresses[0]
                val texto = if (end.subLocality != null)
                    "${end.thoroughfare}, ${end.subLocality}"
                else
                    end.thoroughfare ?: "Localização detectada"

                _uiState.update { it.copy(localizacaoAtual = texto) }
            }
        } catch (e: Exception) { }
    }
}
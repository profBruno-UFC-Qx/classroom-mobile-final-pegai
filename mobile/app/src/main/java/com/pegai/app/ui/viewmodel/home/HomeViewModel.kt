package com.pegai.app.ui.viewmodel.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.model.Category // Import da Enum de Categorias
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class HomeViewModel : ViewModel() {

    // Estado Único da Tela
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Lista "mestra" para filtros locais
    private var todosProdutosCache: List<Product> = emptyList()

    // --- LISTA DE CATEGORIAS PARA O CARROSSEL DA HOME ---
    val categoriasFiltro = listOf("Todos") + Category.entries.map { it.nomeExibicao }

    init {
        carregarDadosIniciais()
    }

    // --- AÇÕES DE UI ---

    fun selecionarCategoria(categoria: String) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
        aplicarFiltros()
    }

    fun atualizarPesquisa(texto: String) {
        _uiState.update { it.copy(textoPesquisa = texto) }
        aplicarFiltros()
    }

    // --- LÓGICA DE DADOS ---

    fun carregarDadosIniciais() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                // AGORA É 100% REAL: Carrega do Firebase. Se vier vazio, a tela fica vazia.
                val dadosCarregados = carregarProdutosCompletos()

                todosProdutosCache = dadosCarregados
                ProductRepository.salvarNoCache(dadosCarregados)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        produtos = dadosCarregados,
                        // Populares: Nota alta (> 4.5)
                        produtosPopulares = dadosCarregados.filter { p -> p.nota >= 4.5 }
                    )
                }

                // Log para depuração
                Log.d("HomeViewModel", "Produtos carregados: ${dadosCarregados.size}")

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, erro = "Erro ao carregar produtos")
                }
                Log.e("Firestore", "Erro crítico ao carregar produtos", e)
            }
        }
    }

    private fun aplicarFiltros() {
        val estadoAtual = _uiState.value
        val termo = estadoAtual.textoPesquisa.lowercase()
        val cat = estadoAtual.categoriaSelecionada

        val listaFiltrada = todosProdutosCache.filter { produto ->
            // 1. Filtro de Categoria
            val matchCategoria = if (cat == "Todos") {
                true
            } else {
                produto.categoria.equals(cat, ignoreCase = true)
            }

            // 2. Filtro de Texto (Título ou Descrição)
            val matchTexto = produto.titulo.lowercase().contains(termo) ||
                    produto.descricao.lowercase().contains(termo)

            matchCategoria && matchTexto
        }

        _uiState.update { it.copy(produtos = listaFiltrada) }
    }

    // --- LÓGICA DE LOCALIZAÇÃO ---

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
        } catch (e: Exception) {
            // Ignora silenciosamente
        }
    }

    // --- MÉTODOS AUXILIARES DE BUSCA NO FIREBASE ---

    suspend fun carregarProdutosCompletos(): List<Product> {
        val produtosBase = carregarProdutosBase()

        return produtosBase.map { produto ->
            // Enriquece o produto com nome do dono e avaliações
            val donoNome = carregarNomeDono(produto.donoId)
            val (notaMedia, totalAvaliacoes) = calcularAvaliacoes(produto.pid)

            produto.copy(
                donoNome = donoNome,
                nota = notaMedia,
                totalAvaliacoes = totalAvaliacoes
            )
        }
    }

    suspend fun carregarProdutosBase(): List<Product> {
        val snapshot = db.collection("products").get().await()
        return snapshot.documents.mapNotNull { doc ->
            // Importante: garante que o ID do documento vá para o objeto
            doc.toObject<Product>()?.copy(pid = doc.id)
        }
    }

    suspend fun carregarNomeDono(donoId: String): String {
        try {
            if (donoId.isBlank()) return "Usuário"
            val doc = db.collection("users").document(donoId).get().await()
            return doc.getString("nome") ?: "Usuário"
        } catch (e: Exception) {
            return "Usuário"
        }
    }

    suspend fun calcularAvaliacoes(produtoId: String): Pair<Double, Int> {
        try {
            val snapshot = db.collection("avaliacao")
                .whereEqualTo("produtoId", produtoId)
                .get()
                .await()

            if (snapshot.isEmpty) return Pair(5.0, 0)

            val notas = snapshot.documents.mapNotNull { it.getDouble("nota") }
            val media = if (notas.isNotEmpty()) notas.average() else 0.0

            return Pair(media, notas.size)
        } catch (e: Exception) {
            return Pair(0.0, 0)
        }
    }
}
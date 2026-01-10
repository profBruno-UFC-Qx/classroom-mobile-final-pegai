package com.pegai.app.ui.viewmodel.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
=======
import android.util.Log
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.Category
=======
import com.google.firebase.firestore.toObject
import com.pegai.app.data.data.repository.ProductRepository
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Locale

class HomeViewModel : ViewModel() {

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
=======
    // Estado Único da Tela
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
    private var todosProdutosCache: List<Product> = emptyList()

    val categoriasFiltro = listOf("Todos") + Category.entries.map { it.nomeExibicao }
=======

    // Lista "mestra" para filtros locais (evita requery no banco)
    private var todosProdutosCache: List<Product> = emptyList()
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt

    init {
        carregarDadosIniciais()
    }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
    // --- FUNÇÕES DO MODAL DE MAPA ---

    fun openMapModal() {
        _uiState.update { it.copy(isMapModalVisible = true) }
    }

    fun closeMapModal() {
        _uiState.update { it.copy(isMapModalVisible = false) }
    }

    fun updateRadius(radius: Float) {
        _uiState.update { it.copy(radiusKm = radius) }
    }

    // --------------------------------

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

            lista.map { produto ->
                val nomeFinal = if (produto.donoNome.isNotBlank()) produto.donoNome
                else UserRepository.getNomeUsuario(produto.donoId)
                produto.copy(donoNome = nomeFinal)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
=======
    // --- AÇÕES DE UI (Intent) ---
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt

    fun selecionarCategoria(categoria: String) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
        aplicarFiltros()
    }

    fun atualizarPesquisa(texto: String) {
        _uiState.update { it.copy(textoPesquisa = texto) }
        aplicarFiltros()
    }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
=======
    // --- LÓGICA DE DADOS ---

    fun carregarDadosIniciais() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val dadosCarregados = carregarProdutosCompletos()
                todosProdutosCache = dadosCarregados
                ProductRepository.salvarNoCache(dadosCarregados)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        produtos = dadosCarregados,
                        produtosPopulares = dadosCarregados.filter { p -> p.nota >= 4.5 }
                    )
                }
                println(dadosCarregados)

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Erro ao carregar produtos"

                    )
                }
                Log.e("Firestore", "Erro ao carregar produtos", e)

            }
        }
    }


>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
    private fun aplicarFiltros() {
        val estadoAtual = _uiState.value
        val termo = estadoAtual.textoPesquisa.lowercase()
        val cat = estadoAtual.categoriaSelecionada

        val listaFiltrada = todosProdutosCache.filter { produto ->
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
            val matchCategoria = if (cat == "Todos") true else produto.categoria.equals(cat, ignoreCase = true)
            val matchTexto = produto.titulo.lowercase().contains(termo) ||
                    produto.descricao.lowercase().contains(termo)

=======
            // Filtro de Categoria
            val matchCategoria = if (cat == "Todos") {
                true
            } else {
                produto.categoria.equals(cat, ignoreCase = true)
            }

            // Filtro de Texto (Título ou Descrição)
            val matchTexto = produto.titulo.lowercase().contains(termo) ||
                    produto.descricao.lowercase().contains(termo)

            // Só passa se atender aos DOIS critérios
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
            matchCategoria && matchTexto
        }

        _uiState.update { it.copy(produtos = listaFiltrada) }
    }

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
=======
    // --- LÓGICA DE LOCALIZAÇÃO  ---

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
    @SuppressLint("MissingPermission")
    fun obterLocalizacaoAtual(context: Context) {
        _uiState.update { it.copy(localizacaoAtual = "Buscando...") }

        try {
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
                    // --- ATUALIZAÇÃO: SALVAR LAT/LNG NO ESTADO ---
                    _uiState.update {
                        it.copy(
                            userLat = location.latitude,
                            userLng = location.longitude
                        )
                    }

=======
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
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
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
        } catch (e: Exception) { }
    }
=======
        } catch (e: Exception) {
            // Ignora erro de geocoder de forma sileciosa
        }
    }

    // --- MOCK DATA ---
    private fun gerarDadosFalsos(): List<Product> {
        return listOf(
            // PRODUTO 1
            Product(
                pid = "1",
                titulo = "Calculadora HP 12c",
                descricao = "Calculadora usada, perfeita para contabilidade.",
                preco = 15.0,
                categoria = "Calculadoras",
                imageUrl = "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                // Novos campos nomeados:
                donoId = "user_mock_1",
                donoNome = "Maria",
                nota = 4.8,
                totalAvaliacoes = 12,
                imagens = listOf(
                    "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw",
                    "https://http2.mlstatic.com/D_NQ_NP_787622-MLB48827768466_012022-O.webp"
                )
            ),

            // PRODUTO 2 (O que estava dando erro)
            Product(
                pid = "2",
                titulo = "Jaleco Quixadá",
                descricao = "Tamanho M. Pouco uso.",
                preco = 35.0,
                categoria = "Jalecos",
                imageUrl = "https://photos.enjoei.com.br/jaleco-branco-81336648/800x800/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy8xMzQ3Mzc3NC82MmY4Nzc0OGU2YTQwNzVkM2Q3OGNhMjFkZDZhY2NkNS5qcGc",
                // Corrigido aqui:
                donoId = "user_2",
                donoNome = "João",
                nota = 5.0,
                totalAvaliacoes = 3
            ),

            // PRODUTO 3
            Product(
                pid = "3",
                titulo = "Kit Arduino",
                descricao = "Kit completo para iniciantes.",
                preco = 20.0,
                categoria = "Eletrônicos",
                imageUrl = "https://cdn.awsli.com.br/78/78150/produto/338952433/kit_arduino_uno_smd_starter_com_caixa_organizadora-3xak1vrhvm.png",
                donoId = "user_3",
                donoNome = "Pedro",
                nota = 4.5
            )
        )
    }

    suspend fun carregarProdutosCompletos(): List<Product> {
        val produtosBase = carregarProdutosBase()

        return produtosBase.map { produto ->

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
            doc.toObject<Product>()?.copy(pid = doc.id)
        }
    }

    suspend fun carregarNomeDono(donoId: String): String {
        val doc = FirebaseFirestore.getInstance()
            .collection("users")
            .document(donoId)
            .get()
            .await()

        return doc.getString("nome") ?: "Usuário"
    }

    suspend fun calcularAvaliacoes(produtoId: String): Pair<Double, Int> {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("avaliacao")
            .whereEqualTo("produtoId", produtoId)
            .get()
            .await()

        if (snapshot.isEmpty) return Pair(5.0, 0)

        val notas = snapshot.documents.mapNotNull {
            it.getDouble("nota")
        }

        val media = notas.average()
        val total = notas.size

        return Pair(media, total)
    }

>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeViewModel.kt
}
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

    fun selecionarCategoria(categoria: String) {
        _uiState.update { it.copy(categoriaSelecionada = categoria) }
        aplicarFiltros()
    }

    fun atualizarPesquisa(texto: String) {
        _uiState.update { it.copy(textoPesquisa = texto) }
        aplicarFiltros()
    }

    fun carregarDadosIniciais() {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val produtosReais = try {
                    carregarProdutosCompletos()
                } catch (e: Exception) {
                    emptyList()
                }

                val produtosFakes = gerarDadosMock()
                val listaCombinada = produtosReais + produtosFakes

                todosProdutosCache = listaCombinada
                ProductRepository.salvarNoCache(listaCombinada)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        produtos = listaCombinada,
                        produtosPopulares = listaCombinada.filter { p -> p.nota >= 4.5 }
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, erro = "Erro ao carregar produtos")
                }
            }
        }
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

    private suspend fun carregarProdutosBase(): List<Product> {
        val snapshot = db.collection("products").get().await()
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject<Product>()?.copy(pid = doc.id)
        }
    }

    private suspend fun carregarNomeDono(donoId: String): String {
        return try {
            if (donoId.isBlank()) return "Usuário"
            val doc = db.collection("users").document(donoId).get().await()
            doc.getString("nome") ?: "Usuário"
        } catch (e: Exception) {
            "Usuário"
        }
    }

    private suspend fun calcularAvaliacoes(produtoId: String): Pair<Double, Int> {
        return try {
            val snapshot = db.collection("avaliacao")
                .whereEqualTo("produtoId", produtoId)
                .get()
                .await()

            if (snapshot.isEmpty) return Pair(5.0, 0)

            val notas = snapshot.documents.mapNotNull { it.getDouble("nota") }
            val media = if (notas.isNotEmpty()) notas.average() else 0.0

            Pair(media, notas.size)
        } catch (e: Exception) {
            Pair(0.0, 0)
        }
    }

    private fun gerarDadosMock(): List<Product> {
        return listOf(
            Product(
                pid = "mock1",
                titulo = "Câmera Canon T7",
                descricao = "Câmera profissional ideal para iniciantes.",
                preco = 150.0,
                categoria = "Eletrônicos",
                imageUrl = "https://m.media-amazon.com/images/I/71EWRyqzw0L._AC_SL1500_.jpg",
                nota = 4.8,
                totalAvaliacoes = 12,
                donoNome = "Ana Souza"
            ),
            Product(
                pid = "mock2",
                titulo = "Furadeira Bosch",
                descricao = "Furadeira de impacto potente.",
                preco = 45.0,
                categoria = "Ferramentas",
                imageUrl = "https://images.tcdn.com.br/img/img_prod/463223/furadeira_de_impacto_bosch_gsb_13_re_650w_127v_62_1_20200427150935.jpg",
                nota = 4.9,
                totalAvaliacoes = 34,
                donoNome = "Carlos Ferragens"
            ),
            Product(
                pid = "mock3",
                titulo = "Barraca de Camping",
                descricao = "Barraca para 4 pessoas, impermeável.",
                preco = 60.0,
                categoria = "Esportes e Lazer",
                imageUrl = "https://m.media-amazon.com/images/I/61k1b2+6CLL._AC_SX679_.jpg",
                nota = 4.5,
                totalAvaliacoes = 8,
                donoNome = "Marcos Aventura"
            ),
            Product(
                pid = "mock4",
                titulo = "PlayStation 5",
                descricao = "Console última geração com 2 controles.",
                preco = 120.0,
                categoria = "Jogos",
                imageUrl = "https://m.media-amazon.com/images/I/51051FiD9UL._SX522_.jpg",
                nota = 5.0,
                totalAvaliacoes = 156,
                donoNome = "João Gamer"
            ),
            Product(
                pid = "mock5",
                titulo = "Mala de Viagem G",
                descricao = "Mala rígida 360 graus.",
                preco = 35.0,
                categoria = "Moda e Acessórios",
                imageUrl = "https://m.media-amazon.com/images/I/61sGj2-gJFL._AC_SX679_.jpg",
                nota = 4.2,
                totalAvaliacoes = 5,
                donoNome = "Clara Viagens"
            ),
            Product(
                pid = "mock6",
                titulo = "Projetor Epson",
                descricao = "Ideal para apresentações e cinema em casa.",
                preco = 90.0,
                categoria = "Eletrônicos",
                imageUrl = "https://m.media-amazon.com/images/I/61s7s+eIruL._AC_SX679_.jpg",
                nota = 4.7,
                totalAvaliacoes = 22,
                donoNome = "Tech Solutions"
            ),
            Product(
                pid = "mock7",
                titulo = "Bicicleta Mountain Bike",
                descricao = "Aro 29, freio a disco.",
                preco = 70.0,
                categoria = "Esportes e Lazer",
                imageUrl = "https://m.media-amazon.com/images/I/81wGn2TQJeL._AC_SX679_.jpg",
                nota = 4.6,
                totalAvaliacoes = 40,
                donoNome = "Pedro Pedal"
            ),
            Product(
                pid = "mock8",
                titulo = "Kit Ferramentas",
                descricao = "Maleta completa com 100 peças.",
                preco = 25.0,
                categoria = "Ferramentas",
                imageUrl = "https://m.media-amazon.com/images/I/71+2Z8m-EWL._AC_SX679_.jpg",
                nota = 4.3,
                totalAvaliacoes = 10,
                donoNome = "Zé da Obra"
            )
        )
    }
}
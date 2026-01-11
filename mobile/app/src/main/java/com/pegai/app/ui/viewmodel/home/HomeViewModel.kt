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
import java.util.Locale

class HomeViewModel : ViewModel() {

    // ----------------------------------------------------------------
    // STATE
    // ----------------------------------------------------------------

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private var todosProdutosCache: List<Product> = emptyList()
    private var uidUsuarioLogado: String? = null

    val categoriasFiltro =
        listOf("Todos") + Category.entries.map { it.nomeExibicao }

    // ----------------------------------------------------------------
    // INIT
    // ----------------------------------------------------------------

    init {
        carregarProdutos()
    }

    // ----------------------------------------------------------------
    // PRODUTOS
    // ----------------------------------------------------------------

    private fun carregarProdutos() {
        _uiState.update { it.copy(isLoading = true) }

        ProductRepository.sourceProdutos(
            onChange = { produtos ->
                viewModelScope.launch {
                    val produtosComNome = produtos.map { produto ->
                        val nomeFinal =
                            if (produto.donoNome.isNotBlank()) produto.donoNome
                            else UserRepository.getNomeUsuario(produto.donoId)

                        produto.copy(donoNome = nomeFinal)
                    }

                    todosProdutosCache = produtosComNome

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            produtos = produtosComNome,
                            produtosPopulares = produtosComNome.filter { it.nota >= 4.5 }
                        )
                    }
                }
            },
            onError = {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Erro ao observar produtos"
                    )
                }
            }
        )
    }

    // ----------------------------------------------------------------
    // USUÁRIO LOGADO
    // ----------------------------------------------------------------

    fun definirUsuarioLogado(uid: String) {
        uidUsuarioLogado = uid
        localizacaoExistente()
    }

    // ----------------------------------------------------------------
    // FILTROS (CATEGORIA / TEXTO)
    // ----------------------------------------------------------------

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
            val matchCategoria =
                if (cat == "Todos") true
                else produto.categoria.equals(cat, ignoreCase = true)

            val matchTexto =
                produto.titulo.lowercase().contains(termo) ||
                        produto.descricao.lowercase().contains(termo)

            matchCategoria && matchTexto
        }

        _uiState.update { it.copy(produtos = listaFiltrada) }
    }

    // ----------------------------------------------------------------
    // MODAL DE MAPA
    // ----------------------------------------------------------------

    fun openMapModal() {
        _uiState.update { it.copy(isMapModalVisible = true) }
    }

    fun closeMapModal() {
        _uiState.update { it.copy(isMapModalVisible = false) }
    }

    fun updateRadius(radius: Float) {
        _uiState.update { it.copy(radiusKm = radius) }
    }

    // ----------------------------------------------------------------
    // LOCALIZAÇÃO
    // ----------------------------------------------------------------

    @SuppressLint("MissingPermission")
    fun obterLocalizacaoAtual(context: Context) {
        _uiState.update { it.copy(localizacaoAtual = "Buscando...") }

        try {
            val fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(context)

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location ->
                    if (location != null) {
                        _uiState.update {
                            it.copy(
                                userLat = location.latitude,
                                userLng = location.longitude
                            )
                        }

                        localizacaoExistente()

                        viewModelScope.launch {
                            converterCoordenadas(
                                context,
                                location.latitude,
                                location.longitude
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(localizacaoAtual = "GPS indisponível")
                        }
                    }
                }
                .addOnFailureListener {
                    _uiState.update {
                        it.copy(localizacaoAtual = "Erro GPS")
                    }
                }

        } catch (e: Exception) {
            _uiState.update {
                it.copy(localizacaoAtual = "Sem permissão")
            }
        }
    }

    private fun converterCoordenadas(
        context: Context,
        lat: Double,
        long: Double
    ) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            @Suppress("DEPRECATION")
            val addresses = geocoder.getFromLocation(lat, long, 1)

            if (!addresses.isNullOrEmpty()) {
                val end = addresses[0]
                val texto =
                    if (end.subLocality != null)
                        "${end.thoroughfare}, ${end.subLocality}"
                    else
                        end.thoroughfare ?: "Localização detectada"

                _uiState.update {
                    it.copy(localizacaoAtual = texto)
                }
            }
        } catch (e: Exception) {
        }
    }

    // ----------------------------------------------------------------
    // FILTRO POR LOCALIZAÇÃO
    // ----------------------------------------------------------------

    fun filtroPorLocalizacao() {
        val latUser = _uiState.value.userLat ?: return
        val lngUser = _uiState.value.userLng ?: return
        val raioKm = _uiState.value.radiusKm

        viewModelScope.launch {
            val filtrados = todosProdutosCache.filter { produto ->
                val dono = UserRepository.getUsuarioPorId(produto.donoId)

                dono?.latitude != null &&
                        dono.longitude != null &&
                        distanciaKm(
                            latUser,
                            lngUser,
                            dono.latitude,
                            dono.longitude
                        ) <= raioKm
            }

            _uiState.update {
                it.copy(produtos = filtrados)
            }
        }
    }

    fun limparFiltroLocalizacao() {
        _uiState.update {
            it.copy(produtos = todosProdutosCache)
        }
    }

    // ----------------------------------------------------------------
    // LOCALIZAÇÃO -> FIREBASE
    // ----------------------------------------------------------------

    fun localizacaoExistente() {
        val uid = uidUsuarioLogado ?: return
        val lat = _uiState.value.userLat ?: return
        val lng = _uiState.value.userLng ?: return

        Log.d("LOC_DEBUG", "uid=$uid lat=$lat lng=$lng")

        viewModelScope.launch {
            UserRepository.atualizarLocalizacaoUsuario(
                userId = uid,
                latitude = lat,
                longitude = lng
            )
        }
    }

    // ----------------------------------------------------------------
    // UTIL
    // ----------------------------------------------------------------

    private fun distanciaKm(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a =
            Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat1)) *
                    Math.cos(Math.toRadians(lat2)) *
                    Math.sin(dLon / 2) *
                    Math.sin(dLon / 2)

        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }
}

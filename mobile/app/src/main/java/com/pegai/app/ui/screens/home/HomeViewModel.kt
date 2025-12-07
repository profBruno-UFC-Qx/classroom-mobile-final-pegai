package com.pegai.app.ui.screens.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.LocationServices
import com.pegai.app.model.Product
import com.pegai.app.model.User
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Locale

/**
 * ViewModel responsável pela lógica de negócios da tela Home.
 * Gerencia produtos, estado de autenticação e localização do usuário.
 */
class HomeViewModel : ViewModel() {

    // --- ESTADOS DE UI ---

    private val _produtos = MutableStateFlow<List<Product>>(emptyList())
    /** Lista completa de produtos disponíveis. */
    val produtos: StateFlow<List<Product>> = _produtos.asStateFlow()

    /**
     * Lista filtrada contendo apenas produtos com alta avaliação (>= 4.7).
     * Atualiza-se automaticamente quando a lista principal `_produtos` muda.
     */
    val produtosPopulares: StateFlow<List<Product>> = _produtos.map { lista ->
        lista.filter { it.nota >= 4.7 }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _usuarioLogado = MutableStateFlow<User?>(null)
    /** Usuário da sessão atual. Null se estiver deslogado. */
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()

    private val _enderecoAtual = MutableStateFlow("Localização desconhecida")
    /** String formatada com a Rua/Bairro atual do usuário. */
    val enderecoAtual: StateFlow<String> = _enderecoAtual.asStateFlow()

    private val _categoriaSelecionada = MutableStateFlow("Todos")
    val categoriaSelecionada: StateFlow<String> = _categoriaSelecionada.asStateFlow()

    // --- DADOS ESTÁTICOS ---

    val categorias = listOf("Todos", "Livros", "Calculadoras", "Jalecos", "Eletrônicos", "Outros")

    // --- INICIALIZAÇÃO ---

    init {
        carregarDadosFalsos()
    }

    // --- AÇÕES (Intents) ---

    fun selecionarCategoria(novaCategoria: String) {
        _categoriaSelecionada.value = novaCategoria
        // TODO: Implementar filtro real da lista `_produtos` aqui se necessário
    }

    fun simularLogin() {
        _usuarioLogado.value = User(
            id = "1",
            nome = "Edinaldo",
            fotoUrl = "https://media-for2-2.cdn.whatsapp.net/v/t61.24694-24/537374086_697212073422555_5417296598778872192_n.jpg?ccb=11-4&oh=01_Q5Aa3QEAZeWdNi1rJWQgcPs--M18nmUGu1gpjp3p2XzVamf9dg&oe=69425A94&_nc_sid=5e03e0&_nc_cat=105"
        )
    }

    /**
     * Tenta obter a localização atual do dispositivo e converter em endereço legível.
     * Requer que a permissão de localização já tenha sido concedida na UI.
     */
    @SuppressLint("MissingPermission") // A permissão é garantida pela UI antes de chamar esta função
    fun obterLocalizacaoAtual(context: Context) {
        _enderecoAtual.value = "Buscando localização..."

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                viewModelScope.launch {
                    converterCoordenadasParaEndereco(context, location.latitude, location.longitude)
                }
            } else {
                _enderecoAtual.value = "GPS indisponível"
            }
        }.addOnFailureListener {
            _enderecoAtual.value = "Erro no GPS"
        }
    }

    private fun converterCoordenadasParaEndereco(context: Context, lat: Double, long: Double) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())

            @Suppress("DEPRECATION") // getFromLocation é deprecated na API 33+, mas necessário para suporte legado
            val addresses = geocoder.getFromLocation(lat, long, 1)

            if (!addresses.isNullOrEmpty()) {
                val endereco = addresses[0]
                val rua = endereco.thoroughfare ?: "Rua desconhecida"
                val bairro = endereco.subLocality ?: endereco.locality ?: ""

                _enderecoAtual.value = if (bairro.isNotEmpty()) "$rua, $bairro" else rua
            } else {
                _enderecoAtual.value = "Endereço não encontrado"
            }
        } catch (e: Exception) {
            _enderecoAtual.value = "Erro ao obter endereço"
            e.printStackTrace()
        }
    }

    private fun carregarDadosFalsos() {
        _produtos.value = listOf(
            Product(
                id = "1",
                titulo = "Calculadora HP 12c",
                descricao = "Usada, em bom estado",
                preco = 5.0,
                dono = "João",
                nota = 4.8,
                imageUrl = "https://photos.enjoei.com.br/calculadora-financeira-hp-12c-91594098/1200xN/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy80NTg3OTc2L2RjNzU0ZDMzOWY1MGNkYjZhMjM4ZjFhYWIxMzc1MzdkLmpwZw"
            ),
            Product(
                id = "2",
                titulo = "Jaleco Quixadá",
                descricao = "Tamanho M, bordado UFC",
                preco = 15.0,
                dono = "Maria",
                nota = 5.0,
                imageUrl = "https://photos.enjoei.com.br/jaleco-branco-81336648/800x800/czM6Ly9waG90b3MuZW5qb2VpLmNvbS5ici9wcm9kdWN0cy8xMzQ3Mzc3NC82MmY4Nzc0OGU2YTQwNzVkM2Q3OGNhMjFkZDZhY2NkNS5qcGc"
            ),
            Product(
                id = "3",
                titulo = "Kit Arduino",
                descricao = "Completo com sensores",
                preco = 20.0,
                dono = "Pedro",
                nota = 4.5,
                imageUrl = "https://cdn.awsli.com.br/78/78150/produto/338952433/kit_arduino_uno_smd_starter_com_caixa_organizadora-3xak1vrhvm.png"
            ),
            Product(
                id = "4",
                titulo = "Livro Cormen",
                descricao = "A bíblia da computação",
                preco = 10.0,
                dono = "Ana",
                nota = 4.9,
                imageUrl = "https://img.olx.com.br/images/87/874568196905386.jpg"
            ),
            Product(
                id = "5",
                titulo = "Câmera Canon Antiga",
                descricao = "Para amantes de fotografia",
                preco = 35.0,
                dono = "Carlos",
                nota = 4.7,
                imageUrl = "https://d1o6h00a1h5k7q.cloudfront.net/imagens/img_m/28309/13751831.jpg"
            )
        )
    }
}
package com.pegai.app.ui.viewmodel.home

import com.pegai.app.model.Product

data class HomeUiState(
    val isLoading: Boolean = false,

    // Listas de Produtos
    val produtos: List<Product> = emptyList(),
    val produtosPopulares: List<Product> = emptyList(),

    // Estados de Seleção/Filtro
    val categoriaSelecionada: String = "Todos",
    val textoPesquisa: String = "",

    // Estados de Sistema
    val localizacaoAtual: String = "Buscando localização...",
    val erro: String? = null,

    val isMapModalVisible: Boolean = false, // Controla se o modal está aberto
    val radiusKm: Float = 5f,               // Valor do slider
    val userLat: Double? = null,            // Latitude para centralizar o mapa
    val userLng: Double? = null             // Longitude para centralizar o mapa
)
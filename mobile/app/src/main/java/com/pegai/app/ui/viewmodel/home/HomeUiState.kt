package com.pegai.app.ui.viewmodel.home

import com.pegai.app.model.Product

data class HomeUiState(
    val isLoading: Boolean = false,

    // Listas de Produtos (Dados dinâmicos)
    val produtos: List<Product> = emptyList(),
    val produtosPopulares: List<Product> = emptyList(),

    // OBS: Removemos 'categorias' daqui pois agora ela vem do ViewModel (Category.entries)

    // Estados de Seleção/Filtro
    val categoriaSelecionada: String = "Todos",
    val textoPesquisa: String = "",

    // Estados de Sistema
    val localizacaoAtual: String = "Buscando localização...",
    val erro: String? = null
)
package com.pegai.app.ui.viewmodel.home

import com.pegai.app.model.Product
<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeUiState.kt

data class HomeUiState(
    val isLoading: Boolean = false,

    // Listas de Produtos
    val produtos: List<Product> = emptyList(),
    val produtosPopulares: List<Product> = emptyList(),
=======
import com.pegai.app.model.User

data class HomeUiState(
    // Dados da UI
    val produtos: List<Product> = emptyList(),
    val produtosPopulares: List<Product> = emptyList(),
    val categorias: List<String> = listOf("Todos", "Livros", "Calculadoras", "Jalecos", "Eletrônicos", "Outros"),
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeUiState.kt

    // Estados de Seleção/Filtro
    val categoriaSelecionada: String = "Todos",
    val textoPesquisa: String = "",

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeUiState.kt
    // Estados de Sistema
    val localizacaoAtual: String = "Buscando localização...",
    val erro: String? = null,

    val isMapModalVisible: Boolean = false, // Controla se o modal está aberto
    val radiusKm: Float = 5f,               // Valor do slider
    val userLat: Double? = null,            // Latitude para centralizar o mapa
    val userLng: Double? = null             // Longitude para centralizar o mapa
=======
    // Estados de Hardware/Sistema
    val localizacaoAtual: String = "Localização desconhecida",
    val isLoading: Boolean = false,
    val erro: String? = null,
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/ui/viewmodel/home/HomeUiState.kt
)
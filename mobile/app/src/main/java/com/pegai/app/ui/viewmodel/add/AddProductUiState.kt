package com.pegai.app.ui.viewmodel.add

import com.pegai.app.model.Product

data class AddProductUiState(
    val isLoading: Boolean = false,
    val meusProdutos: List<Product> = emptyList(),

    // Dados do Formulário
    val idEmEdicao: String? = null,
    val titulo: String = "",
    val descricao: String = "",
    val preco: String = "",
    val categoria: String = "",
    val imagensSelecionadas: List<String> = emptyList(),

    val mensagemSucesso: String? = null,
    val erro: String? = null
)
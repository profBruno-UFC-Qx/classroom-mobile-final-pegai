package com.pegai.app.ui.screens.home

import androidx.lifecycle.ViewModel
import com.pegai.app.model.Product
import com.pegai.app.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gerencia o estado da tela Home, incluindo a lista de produtos,
 * filtros de categoria e estado de autenticação do usuário.
 */
class HomeViewModel : ViewModel() {

    // --- ESTADOS DA TELA  ---

    private val _produtos = MutableStateFlow<List<Product>>(emptyList())
    /** Lista de produtos exibidos na grade. */
    val produtos: StateFlow<List<Product>> = _produtos.asStateFlow()

    private val _usuarioLogado = MutableStateFlow<User?>(null)
    /** Usuário atual da sessão. Se null, o usuário é um visitante. */
    val usuarioLogado: StateFlow<User?> = _usuarioLogado.asStateFlow()

    private val _categoriaSelecionada = MutableStateFlow("Todos")
    /** Categoria ativa no filtro horizontal. */
    val categoriaSelecionada: StateFlow<String> = _categoriaSelecionada.asStateFlow()

    // --- DADOS ESTÁTICOS ---

    val categorias = listOf("Todos", "Livros", "Calculadoras", "Jalecos", "Eletrônicos", "Outros")

    // --- INICIALIZAÇÃO ---

    init {
        carregarDadosFalsos()
    }

    // --- AÇÕES ---

    fun selecionarCategoria(novaCategoria: String) {
        _categoriaSelecionada.value = novaCategoria
        // TODO: Futuramente, filtrar a lista `_produtos` com base na categoria
    }

    /**
     * Simula um login para fins de teste de UI/UX.
     */
    fun simularLogin() {
        _usuarioLogado.value = User(
            id = "1",
            nome = "Francisco Edinaldo",
            fotoUrl = null
        )
    }

    private fun carregarDadosFalsos() {
        _produtos.value = listOf(
            Product("1", "Calculadora HP 12c", "Usada, em bom estado", 5.0, "João", 4.8),
            Product("2", "Jaleco Quixadá", "Tamanho M, bordado UFC", 15.0, "Maria", 5.0),
            Product("3", "Kit Arduino", "Completo com sensores", 20.0, "Pedro", 4.5),
            Product("4", "Livro Cormen", "A bíblia da computação", 10.0, "Ana", 4.9)
        )
    }
}
package com.pegai.app.ui.viewmodel.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pegai.app.model.Category // Import da nova classe de Categoria
import com.pegai.app.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class AddProductViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val _bancoDeDadosMock = mutableListOf<Product>()

    val categoriasDisponiveis = Category.entries

    init {
        carregarMeusProdutos()
    }

    fun carregarMeusProdutos() {
        val meuId = auth.currentUser?.uid ?: "user_mock"
        val meus = _bancoDeDadosMock.filter { it.donoId == meuId }
        _uiState.update { it.copy(meusProdutos = meus) }
    }

    // Inputs de texto
    fun onTituloChange(v: String) { _uiState.update { it.copy(titulo = v) } }
    fun onDescricaoChange(v: String) { _uiState.update { it.copy(descricao = v) } }
    fun onPrecoChange(v: String) { _uiState.update { it.copy(preco = v) } }
    fun onCategoriaChange(v: String) { _uiState.update { it.copy(categoria = v) } }

    // --- FUNÇÃO PARA FOTOS ---
    fun onFotosSelecionadas(uris: List<Uri>) {
        val caminhos = uris.map { it.toString() }
        _uiState.update { it.copy(imagensSelecionadas = caminhos) }
    }

    fun removerFoto(caminho: String) {
        val novaLista = _uiState.value.imagensSelecionadas.toMutableList()
        novaLista.remove(caminho)
        _uiState.update { it.copy(imagensSelecionadas = novaLista) }
    }

    // --- CRUD ---

    fun salvarProduto() {
        val state = _uiState.value
        val meuId = auth.currentUser?.uid ?: "user_mock"
        val meuNome = auth.currentUser?.displayName ?: "Eu"

        if (state.titulo.isBlank() || state.preco.isBlank()) {
            _uiState.update { it.copy(erro = "Preencha título e preço") }
            return
        }

        // Define a foto de capa (a primeira da lista ou placeholder)
        val capa = state.imagensSelecionadas.firstOrNull() ?: "https://via.placeholder.com/300"

        if (state.idEmEdicao == null) {
            // CREATE
            val novoProduto = Product(
                pid = UUID.randomUUID().toString(),
                titulo = state.titulo,
                descricao = state.descricao,
                preco = state.preco.toDoubleOrNull() ?: 0.0,
                categoria = state.categoria,
                imageUrl = capa,
                imagens = state.imagensSelecionadas,
                donoId = meuId,
                donoNome = meuNome
            )
            _bancoDeDadosMock.add(novoProduto)
            _uiState.update { it.copy(mensagemSucesso = "Produto criado com sucesso!") }
        } else {
            // UPDATE
            val index = _bancoDeDadosMock.indexOfFirst { it.pid == state.idEmEdicao }
            if (index != -1) {
                val produtoAtualizado = _bancoDeDadosMock[index].copy(
                    titulo = state.titulo,
                    descricao = state.descricao,
                    preco = state.preco.toDoubleOrNull() ?: 0.0,
                    categoria = state.categoria,
                    imageUrl = capa,
                    imagens = state.imagensSelecionadas
                )
                _bancoDeDadosMock[index] = produtoAtualizado
                _uiState.update { it.copy(mensagemSucesso = "Produto atualizado!") }
            }
        }

        limparFormulario()
        carregarMeusProdutos()
    }

    fun deletarProduto(produto: Product) {
        _bancoDeDadosMock.remove(produto)
        carregarMeusProdutos()
    }

    fun editarProduto(produto: Product) {
        _uiState.update {
            it.copy(
                idEmEdicao = produto.pid,
                titulo = produto.titulo,
                descricao = produto.descricao,
                preco = produto.preco.toString(),
                categoria = produto.categoria,
                imagensSelecionadas = produto.imagens.ifEmpty { listOf(produto.imageUrl) }
            )
        }
    }

    fun limparFormulario() {
        _uiState.update {
            it.copy(
                idEmEdicao = null, titulo = "", descricao = "", preco = "", categoria = "", imagensSelecionadas = emptyList(), erro = null, mensagemSucesso = null
            )
        }
    }

    fun limparMensagens() {
        _uiState.update { it.copy(mensagemSucesso = null, erro = null) }
    }
}
package com.pegai.app.ui.viewmodel.add

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.pegai.app.model.Category
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

    // --- INPUTS ---
    fun onTituloChange(v: String) { _uiState.update { it.copy(titulo = v) } }
    fun onDescricaoChange(v: String) { _uiState.update { it.copy(descricao = v) } }
    fun onPrecoChange(v: String) { _uiState.update { it.copy(preco = v) } }
    fun onCategoriaChange(v: String) { _uiState.update { it.copy(categoria = v) } }

    fun onFotosSelecionadas(uris: List<Uri>) {
        val caminhos = uris.map { it.toString() }
        _uiState.update { it.copy(imagensSelecionadas = caminhos) }
    }

    fun removerFoto(caminho: String) {
        val novaLista = _uiState.value.imagensSelecionadas.toMutableList()
        novaLista.remove(caminho)
        _uiState.update { it.copy(imagensSelecionadas = novaLista) }
    }

    // --- LÓGICA DO MODAL (BOTTOM SHEET) ---

    fun abrirModalEdicao(produto: Product) {
        _uiState.update {
            it.copy(
                mostrarModalEdicao = true,
                idEmEdicao = produto.pid,
                titulo = produto.titulo,
                descricao = produto.descricao,
                preco = produto.preco.toString(),
                categoria = produto.categoria,
                imagensSelecionadas = produto.imagens.ifEmpty { listOf(produto.imageUrl) },
                erro = null
            )
        }
    }

    fun fecharModal() {
        limparFormulario()
        _uiState.update { it.copy(mostrarModalEdicao = false, mostrarDialogoExclusao = false) }
    }

    // --- LÓGICA DE EXCLUSÃO ---

    fun solicitarExclusao() {
        _uiState.update { it.copy(mostrarDialogoExclusao = true) }
    }

    fun cancelarExclusao() {
        _uiState.update { it.copy(mostrarDialogoExclusao = false) }
    }

    fun confirmarExclusao() {
        val idParaDeletar = _uiState.value.idEmEdicao
        if (idParaDeletar != null) {
            _bancoDeDadosMock.removeAll { it.pid == idParaDeletar }
            carregarMeusProdutos()
            fecharModal()
            _uiState.update { it.copy(mensagemSucesso = "Produto excluído com sucesso.") }
        }
    }

    // --- SALVAR (CRIAR OU ATUALIZAR) ---

    fun salvarProduto() {
        val state = _uiState.value
        val meuId = auth.currentUser?.uid ?: "user_mock"
        val meuNome = auth.currentUser?.displayName ?: "Eu"

        // VALIDAÇÃO
        if (state.titulo.isBlank()) {
            _uiState.update { it.copy(erro = "O título é obrigatório.") }
            return
        }
        if (state.preco.isBlank()) {
            _uiState.update { it.copy(erro = "O preço é obrigatório.") }
            return
        }
        if (state.categoria.isBlank()) {
            _uiState.update { it.copy(erro = "Selecione uma categoria.") }
            return
        }
        if (state.descricao.isBlank()) {
            _uiState.update { it.copy(erro = "A descrição é obrigatória.") }
            return
        }
        if (state.imagensSelecionadas.isEmpty()) {
            _uiState.update { it.copy(erro = "Adicione pelo menos uma foto do produto.") }
            return
        }

        val capa = state.imagensSelecionadas.first()

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

            // 1. Define a mensagem PRIMEIRO
            _uiState.update { it.copy(mensagemSucesso = "Produto cadastrado com sucesso!") }

            // 2. Limpa o formulário DEPOIS
            limparFormulario()
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
                _uiState.update { it.copy(mensagemSucesso = "Produto atualizado com sucesso!") }
            }
            fecharModal()
        }

        carregarMeusProdutos()
    }

    fun limparFormulario() {
        _uiState.update {
            it.copy(
                idEmEdicao = null,
                titulo = "",
                descricao = "",
                preco = "",
                categoria = "",
                imagensSelecionadas = emptyList(),
                erro = null,
                mostrarModalEdicao = false,
                mostrarDialogoExclusao = false
            )
        }
    }
    fun limparMensagens() {
        _uiState.update { it.copy(mensagemSucesso = null, erro = null) }
    }
}
package com.pegai.app.ui.viewmodel.add

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.pegai.app.data.data.repository.ProductRepository
import com.pegai.app.data.data.repository.UserRepository
import com.pegai.app.model.Category
import com.pegai.app.model.Product
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class AddProductViewModel : ViewModel() {

    // ----------------------------------------------------------------
    // STATE
    // ----------------------------------------------------------------

    private val _uiState = MutableStateFlow(AddProductUiState())
    val uiState: StateFlow<AddProductUiState> = _uiState.asStateFlow()

    private val auth = FirebaseAuth.getInstance()
    private val uriCache = mutableMapOf<String, Uri>()

    val categoriasDisponiveis = Category.entries

    // ----------------------------------------------------------------
    // LOAD
    // ----------------------------------------------------------------

    fun carregarMeusProdutos(userId: String) {
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                val lista = ProductRepository.getProdutosPorDono(userId)
                _uiState.update {
                    it.copy(
                        meusProdutos = lista,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // ----------------------------------------------------------------
    // FORM INPUTS
    // ----------------------------------------------------------------

    fun onTituloChange(v: String) {
        _uiState.update { it.copy(titulo = v) }
    }

    fun onDescricaoChange(v: String) {
        _uiState.update { it.copy(descricao = v) }
    }

    fun onPrecoChange(v: String) {
        _uiState.update { it.copy(preco = v) }
    }

    fun onCategoriaChange(v: String) {
        _uiState.update { it.copy(categoria = v) }
    }

    // ----------------------------------------------------------------
    // IMAGENS
    // ----------------------------------------------------------------

    fun onFotosSelecionadas(uris: List<Uri>) {
        val caminhosString = uris.map { uri ->
            val stringUri = uri.toString()
            uriCache[stringUri] = uri
            stringUri
        }

        val listaAtual = _uiState.value.imagensSelecionadas.toMutableList()
        listaAtual.addAll(caminhosString)

        _uiState.update {
            it.copy(imagensSelecionadas = listaAtual.take(5))
        }
    }

    fun removerFoto(caminho: String) {
        val novaLista = _uiState.value.imagensSelecionadas.toMutableList()
        novaLista.remove(caminho)

        uriCache.remove(caminho)

        _uiState.update {
            it.copy(imagensSelecionadas = novaLista)
        }
    }

    // ----------------------------------------------------------------
    // MODAIS
    // ----------------------------------------------------------------

    fun abrirModalEdicao(produto: Product) {
        _uiState.update {
            it.copy(
                mostrarModalEdicao = true,
                idEmEdicao = produto.pid,
                titulo = produto.titulo,
                descricao = produto.descricao,
                preco = produto.preco.toString(),
                categoria = produto.categoria,
                imagensSelecionadas = produto.imagens.ifEmpty {
                    listOf(produto.imageUrl)
                },
                erro = null
            )
        }
    }

    fun fecharModal() {
        limparFormulario()
        _uiState.update {
            it.copy(
                mostrarModalEdicao = false,
                mostrarDialogoExclusao = false
            )
        }
    }

    // ----------------------------------------------------------------
    // EXCLUSÃO
    // ----------------------------------------------------------------

    fun solicitarExclusao() {
        _uiState.update { it.copy(mostrarDialogoExclusao = true) }
    }

    fun cancelarExclusao() {
        _uiState.update { it.copy(mostrarDialogoExclusao = false) }
    }

    fun confirmarExclusao() {
        val idParaDeletar = _uiState.value.idEmEdicao ?: return
        val userId = auth.currentUser?.uid ?: return

        viewModelScope.launch {
            try {
                ProductRepository.excluirProduto(idParaDeletar)
                _uiState.update { it.copy(mensagemSucesso = "Produto excluído.") }
                fecharModal()
                carregarMeusProdutos(userId)
            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(erro = "Erro ao excluir: ${e.message}")
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // SALVAR PRODUTO
    // ----------------------------------------------------------------

    fun salvarProduto() {
        val state = _uiState.value
        val user = auth.currentUser

        if (user == null) {
            _uiState.update { it.copy(erro = "Usuário não autenticado.") }
            return
        }

        if (
            state.titulo.isBlank() ||
            state.preco.isBlank() ||
            state.categoria.isBlank()
        ) {
            _uiState.update {
                it.copy(erro = "Preencha os campos obrigatórios.")
            }
            return
        }

        if (state.imagensSelecionadas.isEmpty()) {
            _uiState.update {
                it.copy(erro = "Adicione ao menos uma foto.")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, erro = null) }

        viewModelScope.launch {
            try {
                val imagensDeffered = state.imagensSelecionadas.map { caminhoString ->
                    async {
                        try {
                            if (caminhoString.startsWith("http")) {
                                caminhoString
                            } else {
                                val uriOriginal =
                                    uriCache[caminhoString]
                                        ?: Uri.parse(caminhoString)

                                ProductRepository.uploadImagemProduto(uriOriginal)
                            }
                        } catch (e: Throwable) {
                            Log.e("Upload", "Falha imagem: $caminhoString", e)
                            ""
                        }
                    }
                }

                val imagensResultados = imagensDeffered.awaitAll()
                val imagensFinais = imagensResultados.filter { it.isNotEmpty() }

                if (imagensFinais.isEmpty()) {
                    throw Exception("Falha no upload das fotos.")
                }

                val nomeDono = try {
                    UserRepository.getNomeUsuario(user.uid)
                } catch (e: Exception) {
                    "Anunciante"
                }

                val precoTratado =
                    state.preco.replace(",", ".").toDoubleOrNull() ?: 0.0

                val produtoFinal = Product(
                    pid = state.idEmEdicao ?: UUID.randomUUID().toString(),
                    donoId = user.uid,
                    donoNome = nomeDono,
                    titulo = state.titulo,
                    descricao = state.descricao,
                    preco = precoTratado,
                    categoria = state.categoria,
                    imageUrl = imagensFinais.first(),
                    imagens = imagensFinais,
                    nota = 0.0,
                    totalAvaliacoes = 0
                )

                ProductRepository.salvarProduto(produtoFinal)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        mensagemSucesso = "Produto salvo com sucesso!"
                    )
                }

                if (state.idEmEdicao == null) {
                    limparFormulario()
                } else {
                    fecharModal()
                }

                carregarMeusProdutos(user.uid)

            } catch (e: Throwable) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        erro = "Erro ao salvar: ${e.message}"
                    )
                }
            }
        }
    }

    // ----------------------------------------------------------------
    // LIMPEZA
    // ----------------------------------------------------------------

    fun limparFormulario() {
        uriCache.clear()
        _uiState.update {
            it.copy(
                idEmEdicao = null,
                titulo = "",
                descricao = "",
                preco = "",
                categoria = "",
                imagensSelecionadas = emptyList(),
                erro = null,
                mostrarModalEdicao = false
            )
        }
    }

    fun limparMensagens() {
        _uiState.update {
            it.copy(
                mensagemSucesso = null,
                erro = null
            )
        }
    }
}

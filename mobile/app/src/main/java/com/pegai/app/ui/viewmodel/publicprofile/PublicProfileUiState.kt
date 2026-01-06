package com.pegai.app.ui.viewmodel.publicprofile

import com.pegai.app.model.Product
import com.pegai.app.model.User
import com.pegai.app.model.UserAvaliacao

data class PublicProfileUiState(
    val isLoading: Boolean = true,

    // O usuário dono do perfil (já contém as notas: user.notaLocador e user.notaLocatario)
    val user: User? = null,

    // Lista real de produtos que esse usuário está anunciando
    val produtos: List<Product> = emptyList(),

    // Lista real de comentários/avaliações que ele recebeu
    val avaliacoes: List<UserAvaliacao> = emptyList(),

    val erro: String? = null
)
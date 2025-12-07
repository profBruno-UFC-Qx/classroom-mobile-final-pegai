package com.pegai.app.model

/**
 * Representa um usuário cadastrado no Pegaí.
 */
data class User(
    val id: String,
    val nome: String,
    val fotoUrl: String? = null,
    val localizacao: String? = null
)
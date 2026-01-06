package com.pegai.app.model

import com.google.firebase.Timestamp

data class UserAvaliacao(
    val avalId: String = "",
    val userId: String = "",       // Quem recebeu (dono do perfil)
    val autorId: String = "",
    val autorNome: String = "",
    val autorFoto: String = "",

    val nota: Double = 0.0,
    val comentario: String = "",

    val papel: String = "locador", // "locador" ou "locatario"

    val data: Timestamp? = null    // Data do Firebase
)
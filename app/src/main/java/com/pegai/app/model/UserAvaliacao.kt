package com.pegai.app.model

<<<<<<< HEAD:mobile/app/src/main/java/com/pegai/app/model/UserAvaliacao.kt
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
=======
data class UserAvaliacao(
    val avalId: String = "",
    val userID: String = "",
    val nota: Int = 0,
    val comentario: String = "",
    val data: com.google.firebase.Timestamp? = null
)
>>>>>>> develop_guilherme:app/src/main/java/com/pegai/app/model/UserAvaliacao.kt

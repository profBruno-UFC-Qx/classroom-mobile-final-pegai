package com.pegai.app.model

import com.google.firebase.firestore.DocumentId

data class Review(
    @DocumentId
    var id: String = "",
    var autorId: String = "",
    var autorNome: String = "",
    var autorFoto: String = "",
    var nota: Int = 0,
    var comentario: String = "",
    var data: Long = System.currentTimeMillis(),
    var papel: String? = null
) {
    constructor() : this("", "", "", "", 0, "", System.currentTimeMillis(), null)
}
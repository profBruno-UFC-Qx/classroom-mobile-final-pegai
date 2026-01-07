package com.pegai.app.model

data class Product(
    val pid: String = "",
    val donoId: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val categoria: String = "",
    val preco: Double = 0.0,
    val imageUrl: String = "", // Foto de capa

    val imagens: List<String> = emptyList(),

    val nota: Double = 0.0,
    val totalAvaliacoes: Int = 0,
    val donoNome: String = ""
)
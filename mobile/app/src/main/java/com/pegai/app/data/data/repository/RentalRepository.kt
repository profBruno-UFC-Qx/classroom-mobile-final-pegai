package com.pegai.app.data.data.repository

import com.google.firebase.Timestamp
import com.pegai.app.model.Rental
import com.pegai.app.model.RentalStatus
import kotlinx.coroutines.delay
import java.util.Date

object RentalRepository {

    // Simula uma busca no banco de dados com TODOS os cenários possíveis
    suspend fun getRentalsForUser(userId: String): List<Rental> {
        delay(500) // Pequeno delay para ver o loading

        val listaGerada = mutableListOf<Rental>()
        val statuses = RentalStatus.values()

        // Datas auxiliares
        val hoje = Timestamp.now()
        val futuro = Timestamp(Date(System.currentTimeMillis() + 86400000 * 5)) // +5 dias
        val passado = Timestamp(Date(System.currentTimeMillis() - 86400000 * 10)) // -10 dias

        // Nomes de produtos fictícios para variar
        val produtos = listOf("Furadeira Bosch", "Câmera Canon", "Mala de Viagem", "PS5", "Projetor Epson", "Barraca Camping", "Drone DJI")
        val imagens = listOf(
            "https://images.tcdn.com.br/img/img_prod/463223/furadeira_de_impacto_bosch_gsb_13_re_650w_127v_62_1_20200427150935.jpg",
            "https://m.media-amazon.com/images/I/71EWRyqzw0L._AC_SL1500_.jpg",
            "https://m.media-amazon.com/images/I/61sGj2-gJFL._AC_SX679_.jpg",
            "https://m.media-amazon.com/images/I/51051FiD9UL._SX522_.jpg",
            "https://m.media-amazon.com/images/I/61s7s+eIruL._AC_SX679_.jpg",
            "https://m.media-amazon.com/images/I/61k1b2+6CLL._AC_SX679_.jpg",
            "https://m.media-amazon.com/images/I/61s7s+eIruL._AC_SX679_.jpg"
        )

        // GERA AS COMBINAÇÕES
        statuses.forEachIndexed { index, status ->
            val produtoNome = produtos[index % produtos.size]
            val imagemUrl = imagens[index % imagens.size]
            val dataFim = if (status.isActive) futuro else passado

            // CENÁRIO A: Usuário é o DONO (Locador)
            listaGerada.add(
                Rental(
                    id = "locador_$index",
                    productId = "prod_L_$index",
                    productName = "$produtoNome (Dono)",
                    productImageUrl = imagemUrl,
                    productPrice = 50.0 + (index * 10),
                    locadorId = userId,
                    locadorNome = "Você",
                    locatarioId = "user_x",
                    locatarioNome = "Maria Cliente",
                    status = status,
                    dataInicio = hoje,
                    dataFim = dataFim,
                    dataCriacao = if (status.isActive) hoje else passado
                )
            )

            // CENÁRIO B: Usuário é quem ALUGA (Locatário)
            listaGerada.add(
                Rental(
                    id = "locatario_$index",
                    productId = "prod_C_$index",
                    productName = "$produtoNome (Cliente)",
                    productImageUrl = imagemUrl,
                    productPrice = 30.0 + (index * 5),
                    locadorId = "user_y",
                    locadorNome = "João Dono",
                    locatarioId = userId,
                    locatarioNome = "Você",
                    status = status,
                    dataInicio = hoje,
                    dataFim = dataFim,
                    dataCriacao = if (status.isActive) hoje else passado
                )
            )
        }

        return listaGerada
    }
}
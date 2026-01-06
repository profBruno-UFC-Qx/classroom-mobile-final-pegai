package com.pegai.app.data.data.repository

import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.pegai.app.model.Product
import kotlinx.coroutines.tasks.await

object ProductRepository {

    // Cache simples em memória
    private val cache = mutableMapOf<String, Product>()
    private val db = FirebaseFirestore.getInstance()

    suspend fun getProdutosPorDono(donoId: String): List<Product> {
        return try {
            val snapshot = db.collection("products")
                .whereEqualTo("donoId", donoId)
                .get()
                .await()

            val lista = snapshot.documents.mapNotNull { doc ->
                // O toObject exige que Product tenha valores padrão no construtor
                doc.toObject(Product::class.java)?.copy(
                    pid = doc.id
                )
            }

            // Atualiza o cache com o que baixamos
            salvarNoCache(lista)
            lista
        } catch (e: Exception) {
            Log.e("ProductRepo", "Erro ao buscar produtos do dono: $donoId", e)
            emptyList()
        }
    }

    suspend fun getQuantidadeProdutosPorDono(donoId: String): Int {
        return try {
            val query = db.collection("products")
                .whereEqualTo("donoId", donoId)

            // Tenta usar a contagem do servidor (mais rápido e barato)
            val snapshot = query.count().get(AggregateSource.SERVER).await()
            snapshot.count.toInt()
        } catch (e: Exception) {
            Log.e("ProductRepo", "Erro ao contar produtos", e)
            0
        }
    }

    fun salvarNoCache(produtos: List<Product>) {
        produtos.forEach { product ->
            cache[product.pid] = product
        }
    }

    fun getProdutoPorId(id: String): Product? {
        return cache[id]
    }
}
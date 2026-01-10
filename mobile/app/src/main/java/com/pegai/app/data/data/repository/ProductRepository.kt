package com.pegai.app.data.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.pegai.app.model.Product
import com.pegai.app.model.Review
import kotlinx.coroutines.tasks.await
import java.util.UUID

object ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val cache = mutableMapOf<String, Product>()

    fun salvarNoCache(produtos: List<Product>) {
        produtos.forEach { product ->
            if (product.pid.isNotEmpty()) {
                cache[product.pid] = product
            }
        }
    }

    fun getProdutoPorId(id: String): Product? {
        return cache[id]
    }

    suspend fun getProdutosPorDono(donoId: String): List<Product> {
        return try {
            val snapshot = db.collection("products")
                .whereEqualTo("donoId", donoId)
                .get()
                .await()

            val lista = snapshot.toObjects(Product::class.java)
            salvarNoCache(lista)
            lista
        } catch (e: Exception) {
            Log.e("ProductRepo", "Erro ao buscar produtos", e)
            emptyList()
        }
    }

    suspend fun getQuantidadeProdutosPorDono(donoId: String): Int {
        return try {
            val snapshot = db.collection("products")
                .whereEqualTo("donoId", donoId)
                .count()
                .get(AggregateSource.SERVER)
                .await()
            snapshot.count.toInt()
        } catch (e: Exception) {
            0
        }
    }

    suspend fun getProductReviews(productId: String): List<Review> {
        return try {
            val snapshot = db.collection("products")
                .document(productId)
                .collection("reviews")
                .orderBy("data", Query.Direction.DESCENDING)
                .get()
                .await()

            snapshot.toObjects(Review::class.java)
        } catch (e: Exception) {
            Log.e("ProductRepo", "Erro ao buscar reviews do produto", e)
            emptyList()
        }
    }

    suspend fun uploadImagemProduto(uri: Uri): String {
        return try {
            val filename = UUID.randomUUID().toString()
            val ref = storage.reference.child("product_images/$filename.jpg")

            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Falha ao enviar imagem: ${e.message}")
        }
    }

    suspend fun salvarProduto(product: Product) {
        try {
            db.collection("products").document(product.pid)
                .set(product)
                .await()

            cache[product.pid] = product
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Falha ao salvar dados: ${e.message}")
        }
    }

    suspend fun excluirProduto(productId: String) {
        try {
            db.collection("products").document(productId)
                .delete()
                .await()

            cache.remove(productId)
        } catch (e: Exception) {
            e.printStackTrace()
            throw Exception("Falha ao excluir: ${e.message}")
        }
    }
}
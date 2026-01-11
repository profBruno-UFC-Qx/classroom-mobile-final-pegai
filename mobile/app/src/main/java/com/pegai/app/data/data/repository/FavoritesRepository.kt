package com.pegai.app.data.data.repository

import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pegai.app.model.Product
import kotlinx.coroutines.tasks.await

object FavoritesRepository {

    private val db = FirebaseFirestore.getInstance()

    private fun favoritesCol(userId: String) =
        db.collection("users").document(userId).collection("favorites")

    suspend fun addFavorite(userId: String, productId: String) {
        require(userId.isNotBlank()) { "userId vazio" }
        require(productId.isNotBlank()) { "productId vazio" }

        favoritesCol(userId)
            .document(productId)
            .set(
                mapOf(
                    "productId" to productId,
                    "createdAt" to FieldValue.serverTimestamp()
                )
            )
            .await()
    }

    suspend fun removeFavorite(userId: String, productId: String) {
        require(userId.isNotBlank()) { "userId vazio" }
        require(productId.isNotBlank()) { "productId vazio" }

        favoritesCol(userId)
            .document(productId)
            .delete()
            .await()
    }

    suspend fun getFavoriteProducts(userId: String): List<Product> {
        require(userId.isNotBlank()) { "userId vazio" }

        // pega os favoritos em ordem (mais recente primeiro)
        val favSnap = favoritesCol(userId)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val orderedIds = favSnap.documents.map { it.id }
        if (orderedIds.isEmpty()) return emptyList()

        // busca produtos por chunks (whereIn tem limite)
        val productMap = mutableMapOf<String, Product>()

        for (chunk in orderedIds.chunked(10)) {
            val prodSnap = db.collection("products")
                .whereIn(FieldPath.documentId(), chunk)
                .get()
                .await()

            for (doc in prodSnap.documents) {
                val p = doc.toObject(Product::class.java) ?: continue
                productMap[doc.id] = p.copy(pid = doc.id)
            }
        }

        // devolve na mesma ordem dos favoritos
        return orderedIds.mapNotNull { productMap[it] }
    }
}

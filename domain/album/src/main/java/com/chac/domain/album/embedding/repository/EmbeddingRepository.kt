package com.chac.domain.album.embedding.repository

import com.chac.domain.album.embedding.model.PhotoEmbeddingSnapshot
import com.chac.domain.album.embedding.model.PhotoSearchResult

interface EmbeddingRepository {
    suspend fun syncImages()

    suspend fun cacheEmbedding(
        id: Long,
        uri: String,
        timestamp: Long = System.currentTimeMillis(),
    )

    suspend fun searchByText(
        query: String,
        topK: Int = 30,
    ): List<PhotoSearchResult>

    suspend fun getCachedEmbeddings(): List<PhotoEmbeddingSnapshot>
}

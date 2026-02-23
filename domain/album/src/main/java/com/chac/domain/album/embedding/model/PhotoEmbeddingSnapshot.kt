package com.chac.domain.album.embedding.model

data class PhotoEmbeddingSnapshot(
    val id: Long,
    val uri: String,
    val embedding: FloatArray,
    val timestamp: Long,
)
